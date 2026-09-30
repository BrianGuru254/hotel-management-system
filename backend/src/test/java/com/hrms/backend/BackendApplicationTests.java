package com.hrms.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:hrms-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.flyway.enabled=false",
		"spring.sql.init.mode=always",
		"spring.sql.init.schema-locations=classpath:schema-test.sql",
		"spring.jpa.hibernate.ddl-auto=validate"
})
class BackendApplicationTests {
	@Autowired JdbcTemplate jdbcTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void databaseRejectsPropertyWithBranchOwnedByAnotherOrganization() {
		UUID organizationA = UUID.randomUUID();
		UUID organizationB = UUID.randomUUID();
		UUID branchA = UUID.randomUUID();
		jdbcTemplate.update("INSERT INTO organizations (id, name, country, currency_code, timezone, active, created_at, updated_at) VALUES (?, ?, 'Kenya', 'KES', 'Africa/Nairobi', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", organizationA, "Group A");
		jdbcTemplate.update("INSERT INTO organizations (id, name, country, currency_code, timezone, active, created_at, updated_at) VALUES (?, ?, 'Kenya', 'KES', 'Africa/Nairobi', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", organizationB, "Group B");
		jdbcTemplate.update("INSERT INTO branches (id, organization_id, name, code, country, currency_code, timezone, active, created_at, updated_at) VALUES (?, ?, 'Branch A', 'A', 'Kenya', 'KES', 'Africa/Nairobi', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", branchA, organizationA);

		assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
				"INSERT INTO properties (id, organization_id, branch_id, name, code, country, currency_code, timezone, active, created_at, updated_at) VALUES (?, ?, ?, 'Hotel', 'H1', 'Kenya', 'KES', 'Africa/Nairobi', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
				UUID.randomUUID(), organizationB, branchA));
	}

}
