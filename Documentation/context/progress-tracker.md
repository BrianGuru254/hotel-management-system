# Progress Tracker

## Current Phase

Foundation / organization, branch, and property setup.

## Current Goal

Complete and verify the Organization → Branch → Property foundation, including database enforcement that a property and its branch belong to the same organization.

## Completed (Observed)

- Spring Boot 3.5.6 Maven project configured for Java 21.
- PostgreSQL datasource, JPA schema validation, and Flyway are configured.
- Spring Web, Security, Validation, Actuator, and test dependencies are present.
- Organization, Branch, and Property entities, DTOs, repositories, services, REST controllers, validation, and exception handling are implemented.
- Branch creation and update require an existing organization.
- Property creation and update require an existing organization and branch, then verify that the branch belongs to the supplied organization.
- Flyway V003 adds Property, its direct Organization and Branch foreign keys, and a composite ownership foreign key. V004 aligns existing currency columns to `CHAR(3)`; V001/V002 remain unchanged.
- The backend started successfully against PostgreSQL 17.11 with Hibernate validation enabled. Flyway validated all four migrations, applied V003 and V004, and left the database at version 004.
- Fifteen tests pass, including context loading with Hibernate `validate`, rejection of a cross-organization property insert by the database, service behavior, and REST status/validation responses.
- Requirements, UX, API, architecture, Spring backend, and planned Next.js frontend documentation exist.

## In Progress / Repository Notes

- The working tree had pre-existing edits to the Organization and Branch entities. Those changes were preserved, and currency mappings now use Hibernate's CHAR JDBC type to match PostgreSQL `CHAR(3)`.
- There are migration files in both `backend/src/main/resources/db/migration/` and `backend/main/resources/db/migration/`. Only the former matches the configured Flyway classpath location. Reconcile before relying on both sets.
- The checkout contains one physical Organization and Branch entity file each, with filenames matching the public classes. Git records the historical lowercase paths; check path casing in a non-Windows checkout before release.
- The root `src/` directory was empty at inspection. The planned Next.js frontend is not yet available as code.

## Next Up

1. Wait for the next backend module instruction; do not begin Users/Auth or frontend work yet.

## Open Decisions

- Is the intended frontend definitely Next.js, and where should it live in the repository?
- Which authentication model and role/permission scheme will be implemented first?
- Which modules are in the first usable release?
- What visual brand, component library, and deployment target are approved?

## Architecture Decisions / Confirmed Configuration

- Current backend implementation is Spring Boot + Java 21 + Maven + PostgreSQL + JPA + Flyway.
- The master plan’s NestJS/Prisma backend text is an older/conflicting proposal; do not use it as the current implementation description.
- KES and Africa/Nairobi are current defaults in configuration/domain specifications; branch-level formatting and configuration behavior needs confirmation in the full UI/API.
- Migrations should be managed in the configured `backend/src/main/resources/db/migration/` path.

## Session Notes

- Context files are stored under `Documentation/context/` and describe the HMS repository rather than the portfolio starter templates.
- `mvn test` passed: 15 tests, 0 failures, 0 errors. The context test uses H2 in PostgreSQL mode with Hibernate schema validation enabled. Live PostgreSQL startup, Flyway migration, and GET list endpoints were also verified.
