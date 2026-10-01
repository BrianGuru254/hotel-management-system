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
		jdbc.update("DELETE FROM refresh_tokens");
		jdbc.update("DELETE FROM users");
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
