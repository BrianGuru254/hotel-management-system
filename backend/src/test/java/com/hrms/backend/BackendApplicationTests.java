package com.hrms.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:hrms-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.flyway.enabled=false",
		"spring.sql.init.mode=always",
		"spring.sql.init.schema-locations=classpath:schema-test.sql",
		"spring.jpa.properties.hibernate.default_schema=PUBLIC",
		"spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureMockMvc
class BackendApplicationTests {
	@Autowired JdbcTemplate jdbc;
	@Autowired MockMvc mvc;
	@Autowired ObjectMapper mapper;
	@Autowired JwtEncoder jwtEncoder;

	@BeforeEach
	void resetTestData() {
		jdbc.update("DELETE FROM stay_status_history");
		jdbc.update("DELETE FROM stay_guests");
		jdbc.update("DELETE FROM stays");
		jdbc.update("DELETE FROM reservation_status_history");
		jdbc.update("DELETE FROM reservations");
		jdbc.update("DELETE FROM refresh_tokens");
		jdbc.update("DELETE FROM users");
		jdbc.update("DELETE FROM room_amenities");
		jdbc.update("DELETE FROM rooms");
		jdbc.update("DELETE FROM room_types");
		jdbc.update("DELETE FROM amenities");
		jdbc.update("DELETE FROM guests");
		jdbc.update("DELETE FROM departments");
		jdbc.update("DELETE FROM properties");
		jdbc.update("DELETE FROM branches");
		jdbc.update("DELETE FROM organizations");
		jdbc.update("UPDATE system_bootstrap_state SET completed = FALSE WHERE id = 1");
	}

	@Test
	void contextLoads() { }

	@Test
	void databaseRejectsPropertyWithBranchOwnedByAnotherOrganization() {
		UUID organizationA = UUID.randomUUID();
		UUID organizationB = UUID.randomUUID();
		UUID branchA = UUID.randomUUID();
		jdbc.update("INSERT INTO organizations (id, name, country, currency_code, timezone, active, created_at, updated_at) VALUES (?, ?, 'Kenya', 'KES', 'Africa/Nairobi', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", organizationA, "Group A");
		jdbc.update("INSERT INTO organizations (id, name, country, currency_code, timezone, active, created_at, updated_at) VALUES (?, ?, 'Kenya', 'KES', 'Africa/Nairobi', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", organizationB, "Group B");
		jdbc.update("INSERT INTO branches (id, organization_id, name, code, country, currency_code, timezone, active, created_at, updated_at) VALUES (?, ?, 'Branch A', 'A', 'Kenya', 'KES', 'Africa/Nairobi', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", branchA, organizationA);
		assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
				"INSERT INTO properties (id, organization_id, branch_id, name, code, country, currency_code, timezone, active, created_at, updated_at) VALUES (?, ?, ?, 'Hotel', 'H1', 'Kenya', 'KES', 'Africa/Nairobi', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
				UUID.randomUUID(), organizationB, branchA));
	}

	@Test
	void bootstrapLoginAndRefreshRotationPersistOnlyHashesAndRevokeAReusedFamily() throws Exception {
		Session session = bootstrap();
		String header = mvc.perform(post("/api/v1/auth/login").header("X-Auth-Request", "true")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"root@example.test\",\"password\":\"Strong-HMS-Password-2026\"}"))
			.andExpect(status().isOk()).andReturn().getResponse().getHeader("Set-Cookie");
		String refreshA = cookieValue(header);
		String hashA = sha256(refreshA);
		assertEquals(hashA, jdbc.queryForObject("SELECT token_hash FROM refresh_tokens WHERE token_hash = ?", String.class, hashA).trim());
		assertNotEquals(refreshA, hashA);
		String passwordHash = jdbc.queryForObject("SELECT password_hash FROM users WHERE email = 'root@example.test'", String.class);
		assertTrue(passwordHash.startsWith("$2"));
		assertNotEquals("Strong-HMS-Password-2026", passwordHash);
		assertTrue(header.contains("HttpOnly"));
		assertTrue(header.contains("SameSite=Strict"));
		assertTrue(header.contains("Secure") == false, "test profile permits HTTP cookies only in the test runtime");

		MvcResult rotated = mvc.perform(post("/api/v1/auth/refresh").header("X-Auth-Request", "true")
				.cookie(new MockCookie("hms_refresh", refreshA)))
			.andExpect(status().isOk()).andReturn();
		String refreshB = cookieValue(rotated.getResponse().getHeader("Set-Cookie"));
		assertNotEquals(refreshA, refreshB);
		String family = jdbc.queryForObject("SELECT family_id FROM refresh_tokens WHERE token_hash = ?", String.class, hashA);
		assertNotNull(jdbc.queryForObject("SELECT replaced_by_token_id FROM refresh_tokens WHERE token_hash = ?", Object.class, hashA));
		assertNotNull(jdbc.queryForObject("SELECT revoked_at FROM refresh_tokens WHERE token_hash = ?", Object.class, hashA));
		assertEquals(900, mapper.readTree(rotated.getResponse().getContentAsString()).get("expiresIn").asInt());

		mvc.perform(post("/api/v1/auth/refresh").header("X-Auth-Request", "true")
				.cookie(new MockCookie("hms_refresh", refreshA))).andExpect(status().isUnauthorized());
		Integer activeInFamily = jdbc.queryForObject("SELECT COUNT(*) FROM refresh_tokens WHERE family_id = ? AND revoked_at IS NULL", Integer.class, UUID.fromString(family));
		assertEquals(0, activeInFamily);
		mvc.perform(post("/api/v1/auth/refresh").header("X-Auth-Request", "true")
				.cookie(new MockCookie("hms_refresh", refreshB))).andExpect(status().isUnauthorized());
		mvc.perform(post("/api/v1/auth/bootstrap").header("X-Auth-Request", "true").contentType(MediaType.APPLICATION_JSON)
				.content(bootstrapBody())).andExpect(status().isConflict());
	}

	@Test
	void protectedEndpointsEnforceOrganizationBranchAndPropertyBoundaries() throws Exception {
		String systemToken = bootstrap().accessToken();
		String organizationA = createOrganization(systemToken, "Tenant A");
		String organizationB = createOrganization(systemToken, "Tenant B");
		String branchA = createBranch(systemToken, organizationA, "A-MAIN");
		String branchA2 = createBranch(systemToken, organizationA, "A-SECOND");
		String branchB = createBranch(systemToken, organizationB, "B-MAIN");
		String propertyA = createProperty(systemToken, organizationA, branchA, "A-HOTEL");
		String propertyA2 = createProperty(systemToken, organizationA, branchA2, "A-OTHER");
		String propertyB = createProperty(systemToken, organizationB, branchB, "B-HOTEL");
		String departmentA = createDepartment(systemToken, organizationA, branchA, propertyA, "FRONT-DESK");
		String departmentA2 = createDepartment(systemToken, organizationA, branchA, propertyA, "HOUSEKEEPING");

		createUser(systemToken, "orgadmin@example.test", "ORGANIZATION_ADMIN", "ORGANIZATION", organizationA, null);
		String orgAdmin = login("orgadmin@example.test");
		mvc.perform(get("/api/v1/organizations/" + organizationA).header("Authorization", bearer(orgAdmin))).andExpect(status().isOk());
		mvc.perform(get("/api/v1/organizations/" + organizationB).header("Authorization", bearer(orgAdmin))).andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/properties/" + propertyB).header("Authorization", bearer(orgAdmin))).andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/properties?organizationId=" + organizationB).header("Authorization", bearer(orgAdmin)))
				.andExpect(status().isOk()).andExpect(content().json("[]"));

		createUser(systemToken, "branchmanager@example.test", "BRANCH_MANAGER", "BRANCH", organizationA, branchA);
		String branchManager = login("branchmanager@example.test");
		mvc.perform(get("/api/v1/properties/" + propertyA).header("Authorization", bearer(branchManager))).andExpect(status().isOk());
		mvc.perform(get("/api/v1/properties/" + propertyA2).header("Authorization", bearer(branchManager))).andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/organizations").header("Authorization", bearer(branchManager))).andExpect(status().isForbidden());

		createScopedUser(systemToken, "propertymanager@example.test", "PROPERTY_MANAGER", "PROPERTY", organizationA, branchA, propertyA, null);
		String propertyManager = login("propertymanager@example.test");
		mvc.perform(get("/api/v1/properties/" + propertyA).header("Authorization", bearer(propertyManager))).andExpect(status().isOk());
		mvc.perform(get("/api/v1/departments/" + departmentA).header("Authorization", bearer(propertyManager))).andExpect(status().isOk());
		mvc.perform(get("/api/v1/branches/" + branchA).header("Authorization", bearer(propertyManager))).andExpect(status().isForbidden());

		createScopedUser(systemToken, "departmentadmin@example.test", "ORGANIZATION_ADMIN", "DEPARTMENT", organizationA, branchA, propertyA, departmentA);
		String departmentAdmin = login("departmentadmin@example.test");
		mvc.perform(get("/api/v1/departments/" + departmentA).header("Authorization", bearer(departmentAdmin))).andExpect(status().isOk());
		mvc.perform(get("/api/v1/departments/" + departmentA2).header("Authorization", bearer(departmentAdmin))).andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/properties/" + propertyA).header("Authorization", bearer(departmentAdmin))).andExpect(status().isForbidden());
		assertEquals(19, mapper.readTree(mvc.perform(get("/api/v1/roles").header("Authorization", bearer(orgAdmin)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).size());
	}

	@Test
	void guestRecordsAreIsolatedByPropertyAndIdentityCanRepeatAcrossProperties() throws Exception {
		String systemToken = bootstrap().accessToken();
		String organizationA = createOrganization(systemToken, "Guest Tenant A");
		String organizationB = createOrganization(systemToken, "Guest Tenant B");
		String branchA = createBranch(systemToken, organizationA, "G-A");
		String branchB = createBranch(systemToken, organizationB, "G-B");
		String propertyA = createProperty(systemToken, organizationA, branchA, "G-HOTEL-A");
		String propertyB = createProperty(systemToken, organizationB, branchB, "G-HOTEL-B");
		String guestA = createGuest(systemToken, propertyA, "Alice", "AB123456");
		String guestB = createGuest(systemToken, propertyB, "Alice", "AB123456");

		createScopedUser(systemToken, "guestdesk@example.test", "FRONT_DESK", "PROPERTY",
				organizationA, branchA, propertyA, null);
		String propertyUser = login("guestdesk@example.test");

		String visibleGuests = mvc.perform(get("/api/v1/guests").header("Authorization", bearer(propertyUser))
				.param("propertyId", propertyA).param("name", "Ali"))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		var page = mapper.readTree(visibleGuests);
		assertEquals(1, page.get("content").size());
		assertEquals(guestA, page.get("content").get(0).get("id").asText());
		assertEquals(propertyA, page.get("content").get(0).get("propertyId").asText());

		mvc.perform(get("/api/v1/guests/" + guestA).header("Authorization", bearer(propertyUser)))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/guests/" + guestB).header("Authorization", bearer(propertyUser)))
			.andExpect(status().isForbidden());
		String outsideProperty = mvc.perform(get("/api/v1/guests").header("Authorization", bearer(propertyUser))
				.param("propertyId", propertyB))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(0, mapper.readTree(outsideProperty).get("content").size());

		mvc.perform(post("/api/v1/guests").header("Authorization", bearer(propertyUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of(
						"propertyId", propertyB, "firstName", "Out", "lastName", "Of Scope"))))
			.andExpect(status().isForbidden());
		mvc.perform(post("/api/v1/guests").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of(
						"propertyId", propertyA, "firstName", "Duplicate", "lastName", "Identity",
						"identificationType", "NATIONAL_ID", "identificationNumber", "AB123456"))))
			.andExpect(status().isConflict());
		mvc.perform(get("/api/v1/guests/" + UUID.randomUUID()).header("Authorization", bearer(systemToken)))
			.andExpect(status().isNotFound());
		mvc.perform(get("/api/v1/guests").header("Authorization", bearer(propertyUser)).param("size", "101"))
			.andExpect(status().isBadRequest());
		mvc.perform(post("/api/v1/guests").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of(
						"propertyId", propertyA, "firstName", " ", "lastName", "Invalid"))))
			.andExpect(status().isBadRequest());
		mvc.perform(post("/api/v1/guests").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of(
						"propertyId", propertyA, "firstName", "Missing", "lastName", "Identity",
						"identificationType", "NATIONAL_ID"))))
			.andExpect(status().isBadRequest());
		MvcResult updated = mvc.perform(put("/api/v1/guests/" + guestA)
				.header("Authorization", bearer(propertyUser)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("firstName", "Alicia", "lastName", "Guest"))))
			.andExpect(status().isOk()).andReturn();
		assertEquals("Alicia", mapper.readTree(updated.getResponse().getContentAsString()).get("firstName").asText());
	}

	@Test
	void roomTypeAndAmenityCatalogsAreCrudManagedAndIsolatedByProperty() throws Exception {
		String systemToken = bootstrap().accessToken();
		String organizationA = createOrganization(systemToken, "Catalog Tenant A");
		String organizationB = createOrganization(systemToken, "Catalog Tenant B");
		String branchA = createBranch(systemToken, organizationA, "C-A");
		String branchB = createBranch(systemToken, organizationB, "C-B");
		String propertyA = createProperty(systemToken, organizationA, branchA, "C-HOTEL-A");
		String propertyB = createProperty(systemToken, organizationB, branchB, "C-HOTEL-B");

		String roomTypeA = createRoomType(systemToken, propertyA, "Standard", "STD");
		String roomTypeB = createRoomType(systemToken, propertyB, "Standard", "STD");
		String amenityA = createAmenity(systemToken, propertyA, "Wi-Fi");
		String amenityB = createAmenity(systemToken, propertyB, "Wi-Fi");

		createScopedUser(systemToken, "catalogdesk@example.test", "FRONT_DESK", "PROPERTY",
				organizationA, branchA, propertyA, null);
		String propertyUser = login("catalogdesk@example.test");
		String visibleRoomTypes = mvc.perform(get("/api/v1/room-types").header("Authorization", bearer(propertyUser)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(1, mapper.readTree(visibleRoomTypes).get("content").size());
		assertEquals(roomTypeA, mapper.readTree(visibleRoomTypes).get("content").get(0).get("id").asText());
		String visibleAmenities = mvc.perform(get("/api/v1/amenities").header("Authorization", bearer(propertyUser)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(1, mapper.readTree(visibleAmenities).get("content").size());
		assertEquals(amenityA, mapper.readTree(visibleAmenities).get("content").get(0).get("id").asText());
		mvc.perform(get("/api/v1/room-types/" + roomTypeB).header("Authorization", bearer(propertyUser)))
				.andExpect(status().isForbidden());
		mvc.perform(get("/api/v1/amenities/" + amenityB).header("Authorization", bearer(propertyUser)))
				.andExpect(status().isForbidden());
		String outsideRoomTypeFilter = mvc.perform(get("/api/v1/room-types").header("Authorization", bearer(propertyUser))
				.param("propertyId", propertyB)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(0, mapper.readTree(outsideRoomTypeFilter).get("content").size());
		String outsideAmenityFilter = mvc.perform(get("/api/v1/amenities").header("Authorization", bearer(propertyUser))
				.param("propertyId", propertyB)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(0, mapper.readTree(outsideAmenityFilter).get("content").size());
		mvc.perform(post("/api/v1/room-types").header("Authorization", bearer(propertyUser)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyB, "name", "Out of Scope", "code", "OUT"))))
				.andExpect(status().isForbidden());
		mvc.perform(post("/api/v1/amenities").header("Authorization", bearer(propertyUser)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyB, "name", "Out of Scope"))))
				.andExpect(status().isForbidden());

		mvc.perform(post("/api/v1/room-types").header("Authorization", bearer(systemToken)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyA, "name", "Duplicate", "code", "std"))))
				.andExpect(status().isConflict());
		mvc.perform(post("/api/v1/amenities").header("Authorization", bearer(systemToken)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyA, "name", "wi-fi"))))
				.andExpect(status().isConflict());
		mvc.perform(post("/api/v1/room-types").header("Authorization", bearer(systemToken)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyA, "name", " ", "code", "BAD"))))
				.andExpect(status().isBadRequest());

		MvcResult updatedRoomType = mvc.perform(put("/api/v1/room-types/" + roomTypeA).header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Deluxe\",\"code\":\"DLX\",\"basePrice\":125.50,\"maxOccupancy\":2}"))
				.andExpect(status().isOk()).andReturn();
		assertEquals("DLX", mapper.readTree(updatedRoomType.getResponse().getContentAsString()).get("code").asText());
		MvcResult updatedAmenity = mvc.perform(put("/api/v1/amenities/" + amenityA).header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"High-speed Wi-Fi\",\"description\":\"Included\"}"))
				.andExpect(status().isOk()).andReturn();
		assertEquals("High-speed Wi-Fi", mapper.readTree(updatedAmenity.getResponse().getContentAsString()).get("name").asText());
		mvc.perform(get("/api/v1/room-types").header("Authorization", bearer(systemToken)).param("size", "101"))
				.andExpect(status().isBadRequest());
		mvc.perform(delete("/api/v1/room-types/" + roomTypeA).header("Authorization", bearer(systemToken)))
				.andExpect(status().isNoContent());
		mvc.perform(delete("/api/v1/amenities/" + amenityA).header("Authorization", bearer(systemToken)))
				.andExpect(status().isNoContent());
		mvc.perform(get("/api/v1/room-types/" + UUID.randomUUID()).header("Authorization", bearer(systemToken)))
				.andExpect(status().isNotFound());
		mvc.perform(get("/api/v1/amenities/" + UUID.randomUUID()).header("Authorization", bearer(systemToken)))
				.andExpect(status().isNotFound());
	}

	@Test
	void roomsEnforcePropertyHierarchyScopesStatusRulesAndAmenityOwnership() throws Exception {
		String systemToken = bootstrap().accessToken();
		String organizationA = createOrganization(systemToken, "Room Tenant A");
		String organizationB = createOrganization(systemToken, "Room Tenant B");
		String branchA = createBranch(systemToken, organizationA, "R-A");
		String branchB = createBranch(systemToken, organizationB, "R-B");
		String propertyA = createProperty(systemToken, organizationA, branchA, "R-HOTEL-A");
		String propertyB = createProperty(systemToken, organizationB, branchB, "R-HOTEL-B");
		String typeA = createRoomType(systemToken, propertyA, "Standard", "STD");
		String typeB = createRoomType(systemToken, propertyB, "Standard", "STD");
		String amenityA = createAmenity(systemToken, propertyA, "Wi-Fi");
		String amenityB = createAmenity(systemToken, propertyB, "Wi-Fi");
		String roomA = createRoom(systemToken, propertyA, typeA, "101A", 2);
		String roomB = createRoom(systemToken, propertyB, typeB, "101A", 2);

		mvc.perform(post("/api/v1/rooms").header("Authorization", bearer(systemToken)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyA, "roomTypeId", typeB,
						"roomNumber", "201", "capacity", 2))))
				.andExpect(status().isUnprocessableEntity());
		mvc.perform(post("/api/v1/rooms").header("Authorization", bearer(systemToken)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyA, "roomTypeId", typeA,
						"roomNumber", "101a", "capacity", 2))))
				.andExpect(status().isConflict());
		mvc.perform(post("/api/v1/rooms").header("Authorization", bearer(systemToken)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyA, "roomTypeId", typeA,
						"roomNumber", "201", "capacity", 3))))
				.andExpect(status().isUnprocessableEntity());

		mvc.perform(put("/api/v1/rooms/" + roomA + "/amenities").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of("amenityIds", List.of(amenityA)))))
				.andExpect(status().isOk()).andExpect(jsonPath("$.amenityIds[0]").value(amenityA));
		mvc.perform(put("/api/v1/rooms/" + roomA + "/amenities").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of("amenityIds", List.of(amenityB)))))
				.andExpect(status().isUnprocessableEntity());
		mvc.perform(put("/api/v1/rooms/" + roomA + "/amenities").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of("amenityIds", List.of(amenityA, amenityA)))))
				.andExpect(status().isBadRequest());

		createScopedUser(systemToken, "roomdesk@example.test", "FRONT_DESK", "PROPERTY",
				organizationA, branchA, propertyA, null);
		String frontDesk = login("roomdesk@example.test");
		String visibleRooms = mvc.perform(get("/api/v1/rooms").header("Authorization", bearer(frontDesk)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(1, mapper.readTree(visibleRooms).get("content").size());
		assertEquals(roomA, mapper.readTree(visibleRooms).get("content").get(0).get("id").asText());
		mvc.perform(get("/api/v1/rooms/" + roomA).header("Authorization", bearer(frontDesk))).andExpect(status().isOk());
		mvc.perform(get("/api/v1/rooms/" + roomB).header("Authorization", bearer(frontDesk))).andExpect(status().isForbidden());
		String outsidePropertyRooms = mvc.perform(get("/api/v1/rooms").header("Authorization", bearer(frontDesk))
				.param("propertyId", propertyB)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(0, mapper.readTree(outsidePropertyRooms).get("content").size());
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(frontDesk))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DIRTY\"}"))
				.andExpect(status().isForbidden());

		createScopedUser(systemToken, "housekeeper@example.test", "HOUSEKEEPING_STAFF", "PROPERTY",
				organizationA, branchA, propertyA, null);
		String housekeeper = login("housekeeper@example.test");
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(housekeeper))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DIRTY\"}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.currentStatus").value("DIRTY"));
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(housekeeper))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"AVAILABLE\"}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.currentStatus").value("AVAILABLE"));
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(housekeeper))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"MAINTENANCE\",\"reason\":\"repair\"}"))
				.andExpect(status().isForbidden());
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"OCCUPIED\"}"))
				.andExpect(status().isOk());
		mvc.perform(put("/api/v1/rooms/" + roomA).header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of(
						"roomTypeId", typeA, "roomNumber", "109", "capacity", 2))))
				.andExpect(status().isUnprocessableEntity());
		mvc.perform(delete("/api/v1/rooms/" + roomA).header("Authorization", bearer(systemToken)))
				.andExpect(status().isUnprocessableEntity());
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(housekeeper))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"DIRTY\"}"))
				.andExpect(status().isUnprocessableEntity());
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"AVAILABLE\"}"))
				.andExpect(status().isOk());

		createScopedUser(systemToken, "maintenance@example.test", "MAINTENANCE_STAFF", "PROPERTY",
				organizationA, branchA, propertyA, null);
		String maintenance = login("maintenance@example.test");
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(maintenance))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"MAINTENANCE\",\"reason\":\"repairs\"}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.currentStatus").value("MAINTENANCE"));
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(maintenance))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"AVAILABLE\"}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.currentStatus").value("AVAILABLE"));

		MvcResult updatedRoom = mvc.perform(put("/api/v1/rooms/" + roomA).header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of(
						"roomTypeId", typeA, "roomNumber", "102", "floorNumber", 2, "capacity", 1, "description", "Corner room"))))
				.andExpect(status().isOk()).andReturn();
		assertEquals("102", mapper.readTree(updatedRoom.getResponse().getContentAsString()).get("roomNumber").asText());
		assertEquals(1, mapper.readTree(updatedRoom.getResponse().getContentAsString()).get("amenityIds").size());
		mvc.perform(get("/api/v1/rooms").header("Authorization", bearer(systemToken)).param("status", "AVAILABLE")
				.param("roomTypeId", typeA).param("propertyId", propertyA))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1));
		mvc.perform(get("/api/v1/rooms").header("Authorization", bearer(systemToken)).param("size", "101"))
				.andExpect(status().isBadRequest());
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"MAINTENANCE\"}"))
				.andExpect(status().isBadRequest());
		mvc.perform(patch("/api/v1/rooms/" + roomA + "/status").header("Authorization", bearer(systemToken))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"MAINTENANCE\",\"reason\":\"Air conditioning repair\"}"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.statusReason").value("Air conditioning repair"));
		mvc.perform(delete("/api/v1/rooms/" + roomA).header("Authorization", bearer(systemToken)))
				.andExpect(status().isNoContent());
		assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM room_amenities WHERE room_id = ?", Integer.class, UUID.fromString(roomA)));
		mvc.perform(get("/api/v1/rooms/" + UUID.randomUUID()).header("Authorization", bearer(systemToken)))
				.andExpect(status().isNotFound());
	}

	@Test
	void rejectsUnauthenticatedAndExpiredAccessTokens() throws Exception {
		mvc.perform(get("/api/v1/organizations")).andExpect(status().isUnauthorized());
		String accessToken = bootstrap().accessToken();
		UUID userId = UUID.fromString(jdbc.queryForObject("SELECT id FROM users WHERE email = 'root@example.test'", String.class));
		Instant now = Instant.now();
		String expired = jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).type("JWT").build(),
				JwtClaimsSet.builder().issuer("hotel-management-system-test").subject(userId.toString())
						.issuedAt(now.minus(2, ChronoUnit.HOURS)).expiresAt(now.minus(1, ChronoUnit.HOURS))
						.id(UUID.randomUUID().toString()).build())).getTokenValue();
		mvc.perform(get("/api/v1/organizations").header("Authorization", bearer(expired))).andExpect(status().isUnauthorized());
		assertNotNull(accessToken);
	}

	@Test
	void reservationLifecycleAndPropertyIsolation() throws Exception {
		// ── Bootstrap superuser ───────────────────────────────────────────────────
		Session root = bootstrap();
		String rootToken = root.accessToken();

		// ── Create org / branch / property A ─────────────────────────────────────
		String orgId   = createOrganization(rootToken, "Reservations Org");
		String branchId = createBranch(rootToken, orgId, "RB1");
		String propAId  = createProperty(rootToken, orgId, branchId, "PROP-A");

		// ── Create a room type (2 rooms of this type) ─────────────────────────────
		String roomTypeId = createRoomType(rootToken, propAId, "Deluxe Room", "DLX");
		String room1Id    = createRoom(rootToken, propAId, roomTypeId, "101", 2);
		String room2Id    = createRoom(rootToken, propAId, roomTypeId, "102", 2);

		// ── Create a guest ────────────────────────────────────────────────────────
		String guestId = createGuest(rootToken, propAId, "Alice", "ID-TEST-001");

		// ── Create a reservation (no specific room — pool allocation) ─────────────
		Map<String, Object> createReq = new HashMap<>();
		createReq.put("propertyId",    propAId);
		createReq.put("guestId",       guestId);
		createReq.put("roomTypeId",    roomTypeId);
		createReq.put("checkInDate",   "2027-06-01");
		createReq.put("checkOutDate",  "2027-06-05");
		createReq.put("adults",        2);
		createReq.put("children",      0);
		createReq.put("numberOfRooms", 1);
		createReq.put("nightlyRate",   150.00);
		createReq.put("depositAmount", 75.00);

		MvcResult createResult = mvc.perform(post("/api/v1/reservations")
						.header("Authorization", bearer(rootToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(createReq)))
				.andExpect(status().isCreated())
				.andReturn();

		String respBody = createResult.getResponse().getContentAsString();
		String reservationId     = mapper.readTree(respBody).get("id").asText();
		String reservationNumber = mapper.readTree(respBody).get("reservationNumber").asText();
		String statusAfterCreate = mapper.readTree(respBody).get("status").asText();

		assertNotNull(reservationId);
		assertNotNull(reservationNumber);
		assertEquals("PENDING", statusAfterCreate);

		// ── Check availability for same room type and dates ───────────────────────
		Map<String, Object> availReq = new HashMap<>();
		availReq.put("propertyId",    propAId);
		availReq.put("roomTypeId",    roomTypeId);
		availReq.put("checkInDate",   "2027-06-01");
		availReq.put("checkOutDate",  "2027-06-05");
		availReq.put("numberOfRooms", 1);

		MvcResult availResult = mvc.perform(post("/api/v1/reservations/check-availability")
						.header("Authorization", bearer(rootToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(availReq)))
				.andExpect(status().isOk())
				.andReturn();

		String availBody = availResult.getResponse().getContentAsString();
		assertTrue(mapper.readTree(availBody).get("available").asBoolean(),
				"Should still be available (2 rooms, 1 reserved)");

		// ── Fetch reservation by ID ───────────────────────────────────────────────
		mvc.perform(get("/api/v1/reservations/" + reservationId)
						.header("Authorization", bearer(rootToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(reservationId))
				.andExpect(jsonPath("$.status").value("PENDING"));

		// ── Confirm reservation ───────────────────────────────────────────────────
		mvc.perform(patch("/api/v1/reservations/" + reservationId + "/confirm")
						.header("Authorization", bearer(rootToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CONFIRMED"));

		// ── Property isolation: second property user cannot see prop-A reservations ─
		String propBId = createProperty(rootToken, orgId, branchId, "PROP-B");
		createScopedUser(rootToken, "frontdesk.b@example.test", "FRONT_DESK",
				"PROPERTY", orgId, branchId, propBId, null);
		String propBToken = login("frontdesk.b@example.test");

		mvc.perform(get("/api/v1/reservations/" + reservationId)
						.header("Authorization", bearer(propBToken)))
				.andExpect(status().isForbidden());

		// ── Cancel the reservation ────────────────────────────────────────────────
		Map<String, Object> cancelReq = new HashMap<>();
		cancelReq.put("reason", "Guest changed plans");

		mvc.perform(post("/api/v1/reservations/" + reservationId + "/cancel")
						.header("Authorization", bearer(rootToken))
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(cancelReq)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CANCELLED"));

		// ── Verify status history contains all transitions ────────────────────────
		MvcResult histResult = mvc.perform(get("/api/v1/reservations/" + reservationId + "/history")
						.header("Authorization", bearer(rootToken)))
				.andExpect(status().isOk())
				.andReturn();

		String histBody = histResult.getResponse().getContentAsString();
		int histCount = mapper.readTree(histBody).size();
		assertTrue(histCount >= 2, "Should have at least 2 status history entries (PENDING→CONFIRMED, CONFIRMED→CANCELLED)");
	}

	@Test
	void stayCheckInCheckOutAndWalkInRespectPropertyScopeAndRoomInventory() throws Exception {
		String rootToken = bootstrap().accessToken();
		String organizationId = createOrganization(rootToken, "Stay Organization");
		String branchId = createBranch(rootToken, organizationId, "STAY-BR");
		String propertyA = createProperty(rootToken, organizationId, branchId, "STAY-A");
		String propertyB = createProperty(rootToken, organizationId, branchId, "STAY-B");
		String roomTypeId = createRoomType(rootToken, propertyA, "Stay Standard", "STY-STD");
		String roomA1 = createRoom(rootToken, propertyA, roomTypeId, "301", 2);
		String roomA2 = createRoom(rootToken, propertyA, roomTypeId, "302", 2);
		String guestA = createGuest(rootToken, propertyA, "Stay Guest", "STAY-GUEST-A");
		String guestA2 = createGuest(rootToken, propertyA, "Additional Guest", "STAY-GUEST-A2");
		String guestB = createGuest(rootToken, propertyB, "Other Property Guest", "STAY-GUEST-B");
		LocalDate today = LocalDate.now(ZoneId.of("Africa/Nairobi"));
		LocalDate checkOutDate = today.plusDays(2);

		Map<String, Object> reservationRequest = new HashMap<>();
		reservationRequest.put("propertyId", propertyA);
		reservationRequest.put("guestId", guestA);
		reservationRequest.put("roomTypeId", roomTypeId);
		reservationRequest.put("roomId", roomA1);
		reservationRequest.put("checkInDate", today.toString());
		reservationRequest.put("checkOutDate", checkOutDate.toString());
		reservationRequest.put("adults", 2);
		reservationRequest.put("children", 0);
		reservationRequest.put("numberOfRooms", 1);
		reservationRequest.put("nightlyRate", 150.00);
		reservationRequest.put("depositAmount", 75.00);
		MvcResult reservationResult = mvc.perform(post("/api/v1/reservations")
				.header("Authorization", bearer(rootToken)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(reservationRequest)))
			.andExpect(status().isCreated()).andReturn();
		String reservationId = mapper.readTree(reservationResult.getResponse().getContentAsString()).get("id").asText();
		mvc.perform(patch("/api/v1/reservations/" + reservationId + "/confirm")
				.header("Authorization", bearer(rootToken))).andExpect(status().isOk());

		createScopedUser(rootToken, "staydesk.a@example.test", "FRONT_DESK", "PROPERTY",
				organizationId, branchId, propertyA, null);
		createScopedUser(rootToken, "staydesk.b@example.test", "FRONT_DESK", "PROPERTY",
				organizationId, branchId, propertyB, null);
		String deskA = login("staydesk.a@example.test");
		String deskB = login("staydesk.b@example.test");

		MvcResult checkInResult = mvc.perform(post("/api/v1/check-ins")
				.header("Authorization", bearer(deskA)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("reservationId", reservationId, "roomId", roomA1))))
			.andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("ACTIVE"))
			.andExpect(jsonPath("$.guestId").value(guestA)).andReturn();
		String stayId = mapper.readTree(checkInResult.getResponse().getContentAsString()).get("id").asText();
		mvc.perform(get("/api/v1/rooms/" + roomA1).header("Authorization", bearer(deskA)))
			.andExpect(status().isOk()).andExpect(jsonPath("$.currentStatus").value("OCCUPIED"));
		mvc.perform(get("/api/v1/reservations/" + reservationId).header("Authorization", bearer(deskA)))
			.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CHECKED_IN"));
		mvc.perform(get("/api/v1/stays/" + stayId).header("Authorization", bearer(deskB)))
			.andExpect(status().isForbidden());
		mvc.perform(post("/api/v1/stays/" + stayId + "/guests")
				.header("Authorization", bearer(deskA)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("guestId", guestA2, "isPrimaryGuest", false))))
			.andExpect(status().isOk()).andExpect(jsonPath("$.guests.length()").value(2));
		mvc.perform(post("/api/v1/stays/" + stayId + "/guests")
				.header("Authorization", bearer(deskA)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("guestId", guestA2, "isPrimaryGuest", true))))
			.andExpect(status().isOk()).andExpect(jsonPath("$.guestId").value(guestA2));
		mvc.perform(post("/api/v1/stays/" + stayId + "/guests")
				.header("Authorization", bearer(deskA)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("guestId", guestB, "isPrimaryGuest", false))))
			.andExpect(status().isUnprocessableEntity());

		MvcResult reservedAvailability = mvc.perform(post("/api/v1/reservations/check-availability")
				.header("Authorization", bearer(deskA)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyA, "roomTypeId", roomTypeId,
						"checkInDate", today.plusDays(1).toString(), "checkOutDate", checkOutDate.toString(),
						"numberOfRooms", 2))))
			.andExpect(status().isOk()).andReturn();
		assertFalse(mapper.readTree(reservedAvailability.getResponse().getContentAsString()).get("available").asBoolean());

		MvcResult preview = mvc.perform(get("/api/v1/check-outs/" + stayId + "/preview")
				.header("Authorization", bearer(deskA)))
			.andExpect(status().isOk()).andReturn();
		assertEquals(300.00, mapper.readTree(preview.getResponse().getContentAsString())
				.get("accommodationEstimate").decimalValue().doubleValue(), 0.01);
		mvc.perform(post("/api/v1/check-outs/" + stayId).header("Authorization", bearer(deskA))
				.contentType(MediaType.APPLICATION_JSON).content("{\"notes\":\"Key returned\"}"))
			.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CHECKED_OUT"))
			.andExpect(jsonPath("$.checkoutNotes").value("Key returned"));
		mvc.perform(get("/api/v1/rooms/" + roomA1).header("Authorization", bearer(deskA)))
			.andExpect(status().isOk()).andExpect(jsonPath("$.currentStatus").value("DIRTY"));
		mvc.perform(get("/api/v1/reservations/" + reservationId).header("Authorization", bearer(deskA)))
			.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"));
		mvc.perform(get("/api/v1/stays/" + stayId + "/history").header("Authorization", bearer(deskA)))
			.andExpect(status().isOk()).andExpect(jsonPath("$[1].newStatus").value("CHECKED_OUT"));
		mvc.perform(post("/api/v1/check-outs/" + stayId).header("Authorization", bearer(deskA)))
			.andExpect(status().isUnprocessableEntity());
		mvc.perform(patch("/api/v1/rooms/" + roomA1 + "/status").header("Authorization", bearer(rootToken))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"AVAILABLE\"}"))
			.andExpect(status().isOk());

		Map<String, Object> groupReservationRequest = new HashMap<>();
		groupReservationRequest.put("propertyId", propertyA);
		groupReservationRequest.put("guestId", guestA);
		groupReservationRequest.put("roomTypeId", roomTypeId);
		groupReservationRequest.put("checkInDate", today.plusDays(1).toString());
		groupReservationRequest.put("checkOutDate", today.plusDays(4).toString());
		groupReservationRequest.put("adults", 2);
		groupReservationRequest.put("children", 0);
		groupReservationRequest.put("numberOfRooms", 2);
		groupReservationRequest.put("nightlyRate", 150.00);
		MvcResult groupReservationResult = mvc.perform(post("/api/v1/reservations")
				.header("Authorization", bearer(rootToken)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(groupReservationRequest)))
			.andExpect(status().isCreated()).andReturn();
		String groupReservationId = mapper.readTree(groupReservationResult.getResponse().getContentAsString()).get("id").asText();
		mvc.perform(patch("/api/v1/reservations/" + groupReservationId + "/confirm")
				.header("Authorization", bearer(rootToken))).andExpect(status().isOk());
		MvcResult firstRoomCheckIn = mvc.perform(post("/api/v1/check-ins")
				.header("Authorization", bearer(deskA)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("reservationId", groupReservationId, "roomId", roomA1))))
			.andExpect(status().isCreated()).andReturn();
		MvcResult secondRoomCheckIn = mvc.perform(post("/api/v1/check-ins")
				.header("Authorization", bearer(deskA)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("reservationId", groupReservationId, "roomId", roomA2))))
			.andExpect(status().isCreated()).andReturn();
		String firstGroupStayId = mapper.readTree(firstRoomCheckIn.getResponse().getContentAsString()).get("id").asText();
		String secondGroupStayId = mapper.readTree(secondRoomCheckIn.getResponse().getContentAsString()).get("id").asText();
		mvc.perform(post("/api/v1/check-outs/" + firstGroupStayId).header("Authorization", bearer(deskA)))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/reservations/" + groupReservationId).header("Authorization", bearer(deskA)))
			.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CHECKED_IN"));
		mvc.perform(post("/api/v1/check-outs/" + secondGroupStayId).header("Authorization", bearer(deskA)))
			.andExpect(status().isOk());
		mvc.perform(get("/api/v1/reservations/" + groupReservationId).header("Authorization", bearer(deskA)))
			.andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"));
		mvc.perform(patch("/api/v1/rooms/" + roomA2 + "/status").header("Authorization", bearer(rootToken))
				.contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"AVAILABLE\"}"))
			.andExpect(status().isOk());

		Map<String, Object> walkInRequest = new HashMap<>();
		walkInRequest.put("propertyId", propertyA);
		walkInRequest.put("guestId", guestA2);
		walkInRequest.put("roomId", roomA2);
		walkInRequest.put("expectedCheckOutDate", today.plusDays(3).toString());
		walkInRequest.put("adults", 1);
		walkInRequest.put("children", 0);
		walkInRequest.put("nightlyRate", 80.00);
		MvcResult walkInResult = mvc.perform(post("/api/v1/check-ins")
				.header("Authorization", bearer(deskA)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(walkInRequest)))
			.andExpect(status().isCreated()).andReturn();
		var walkInJson = mapper.readTree(walkInResult.getResponse().getContentAsString());
		assertTrue(walkInJson.get("reservationId").isNull());
		String walkInStayId = walkInJson.get("id").asText();
		MvcResult walkInAvailability = mvc.perform(post("/api/v1/reservations/check-availability")
				.header("Authorization", bearer(deskA)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("propertyId", propertyA, "roomTypeId", roomTypeId,
						"checkInDate", today.plusDays(1).toString(), "checkOutDate", today.plusDays(2).toString(),
						"numberOfRooms", 2))))
			.andExpect(status().isOk()).andReturn();
		assertEquals(1, mapper.readTree(walkInAvailability.getResponse().getContentAsString()).get("availableRooms").asInt());
		mvc.perform(post("/api/v1/check-ins").header("Authorization", bearer(deskA))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(walkInRequest)))
			.andExpect(status().isUnprocessableEntity());
		mvc.perform(get("/api/v1/stays/active").header("Authorization", bearer(deskA)).param("propertyId", propertyA))
			.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1));
		mvc.perform(get("/api/v1/stays/" + walkInStayId).header("Authorization", bearer(deskB)))
			.andExpect(status().isForbidden());
	}

	private Session bootstrap() throws Exception {
		MvcResult result = mvc.perform(post("/api/v1/auth/bootstrap").header("X-Auth-Request", "true")
				.contentType(MediaType.APPLICATION_JSON).content(bootstrapBody()))
			.andExpect(status().isCreated()).andReturn();
		String access = mapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
		String cookie = result.getResponse().getHeader("Set-Cookie");
		return new Session(access, cookieValue(cookie));
	}

	private String bootstrapBody() {
		return "{\"bootstrapSecret\":\"test-only-one-time-bootstrap-secret\",\"email\":\"root@example.test\",\"password\":\"Strong-HMS-Password-2026\",\"firstName\":\"System\",\"lastName\":\"Owner\"}";
	}

	private String login(String email) throws Exception {
		MvcResult result = mvc.perform(post("/api/v1/auth/login").header("X-Auth-Request", "true").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("email", email, "password", "Strong-HMS-Password-2026"))))
			.andExpect(status().isOk()).andReturn();
		return mapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
	}

	private String createOrganization(String token, String name) throws Exception {
		MvcResult result = mvc.perform(post("/api/v1/organizations").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("name", name))))
			.andExpect(status().isCreated()).andReturn();
		return mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
	}

	private String createBranch(String token, String organizationId, String code) throws Exception {
		MvcResult result = mvc.perform(post("/api/v1/branches").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("organizationId", organizationId, "name", code + " Branch", "code", code))))
			.andExpect(status().isCreated()).andReturn();
		return mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
	}

	private String createProperty(String token, String organizationId, String branchId, String code) throws Exception {
		MvcResult result = mvc.perform(post("/api/v1/properties").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("organizationId", organizationId, "branchId", branchId, "name", code + " Hotel", "code", code))))
			.andExpect(status().isCreated()).andReturn();
		return mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
	}

	private String createGuest(String token, String propertyId, String firstName, String identificationNumber) throws Exception {
		Map<String, Object> request = new HashMap<>();
		request.put("propertyId", propertyId);
		request.put("firstName", firstName);
		request.put("lastName", "Guest");
		request.put("identificationType", "NATIONAL_ID");
		request.put("identificationNumber", identificationNumber);
		MvcResult result = mvc.perform(post("/api/v1/guests").header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
			.andExpect(status().isCreated()).andReturn();
		return mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
	}

	private String createRoomType(String token, String propertyId, String name, String code) throws Exception {
		MvcResult result = mvc.perform(post("/api/v1/room-types").header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of(
						"propertyId", propertyId, "name", name, "code", code, "basePrice", 99.50, "maxOccupancy", 2))))
			.andExpect(status().isCreated()).andReturn();
		return mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
	}

	private String createAmenity(String token, String propertyId, String name) throws Exception {
		MvcResult result = mvc.perform(post("/api/v1/amenities").header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of(
						"propertyId", propertyId, "name", name))))
			.andExpect(status().isCreated()).andReturn();
		return mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
	}

	private String createRoom(String token, String propertyId, String roomTypeId, String roomNumber, int capacity) throws Exception {
		MvcResult result = mvc.perform(post("/api/v1/rooms").header("Authorization", bearer(token))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of(
						"propertyId", propertyId, "roomTypeId", roomTypeId, "roomNumber", roomNumber, "capacity", capacity))))
			.andExpect(status().isCreated()).andReturn();
		return mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
	}

	private String createDepartment(String token, String organizationId, String branchId, String propertyId, String code) throws Exception {
		MvcResult result = mvc.perform(post("/api/v1/departments").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("organizationId", organizationId, "branchId", branchId,
						"propertyId", propertyId, "name", code + " Department", "code", code))))
			.andExpect(status().isCreated()).andReturn();
		return mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
	}

	private void createUser(String token, String email, String role, String scope, String organizationId, String branchId) throws Exception {
		createScopedUser(token, email, role, scope, organizationId, branchId, null, null);
	}

	private void createScopedUser(String token, String email, String role, String scope, String organizationId,
								  String branchId, String propertyId, String departmentId) throws Exception {
		Map<String, Object> grant = new HashMap<>();
		grant.put("roleKey", role); grant.put("scopeType", scope); grant.put("organizationId", organizationId);
		if (branchId != null) grant.put("branchId", branchId);
		if (propertyId != null) grant.put("propertyId", propertyId);
		if (departmentId != null) grant.put("departmentId", departmentId);
		mvc.perform(post("/api/v1/users").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(Map.of("email", email, "password", "Strong-HMS-Password-2026", "firstName", "Scoped", "lastName", "User", "roleScopes", List.of(grant)))))
			.andExpect(status().isCreated());
	}

	private String cookieValue(String header) {
		assertNotNull(header);
		assertTrue(header.contains("HttpOnly"));
		assertTrue(header.contains("SameSite=Strict"));
		return header.substring("hms_refresh=".length(), header.indexOf(';'));
	}

	private String bearer(String token) { return "Bearer " + token; }

	private String sha256(String value) throws Exception {
		return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
	}

	private record Session(String accessToken, String refreshToken) { }
}
