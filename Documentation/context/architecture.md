# Architecture Context

## Current Implemented Stack

| Layer | Technology | Evidence / Role |
| --- | --- | --- |
| Backend runtime | Java 21 | `backend/pom.xml`; Spring Boot runtime |
| Backend framework | Spring Boot 3.5.6 | Maven parent in `backend/pom.xml` |
| Build | Maven / Maven Wrapper | `backend/pom.xml`, `backend/mvnw`, `backend/mvnw.cmd` |
| HTTP | Spring Web | `spring-boot-starter-web` dependency |
| Persistence | Spring Data JPA / Hibernate | JPA dependency; `ddl-auto: validate` |
| Database | PostgreSQL | JDBC URL and PostgreSQL runtime driver |
| Schema migrations | Flyway | Flyway dependencies; configured classpath location |
| Security | Spring Security dependency | Present; authentication/authorization implementation not yet observed |
| Validation | Jakarta Bean Validation starter | Present |
| Monitoring | Spring Boot Actuator | Health/info exposure configured |
| Frontend | Not implemented in the inspected root `src/` | Next.js frontend is specified in Documentation, not present as code |

## Repository Boundaries

- `backend/src/main/java/com/hrms/backend/` — Spring Boot application, entities, DTOs, repositories, and future feature packages.
- `backend/src/main/resources/application.yml` — application, database, JPA, Flyway, server, and actuator configuration. Keep secrets out of source control.
- `backend/src/main/resources/db/migration/` — configured Flyway migration location; V003 adds Properties and ownership constraints, and V004 aligns the live currency columns with the `CHAR(3)` schema contract.
- `backend/main/resources/db/migration/` — an additional migration-looking directory exists outside the configured runtime path; do not assume Flyway executes it. Resolve duplication deliberately before adding migrations.
- `Documentation/` — requirements, architecture, API, UX, and implementation plans.
- Root `src/` — empty at inspection; no frontend architecture is established in source yet.

## Current Domain Model

Observed Java entities are `Organization`, `Branch`, and `Property`. The enforced hierarchy is Organization → Branch → Property. A branch has a required organization FK; a property stores both organization and branch FKs, with a composite FK ensuring the branch belongs to that organization. Branch codes are organization-scoped; property codes are branch-scoped. The entities carry contact/location, currency, timezone, and active-state fields.

## Planned System Shape

The project documents a modular monolith with a REST API and a separate management web frontend. The implementation specifications propose Next.js + TypeScript for the frontend and Spring Boot for the API. PostgreSQL is the system of record. Redis, queues, external integrations, and public booking website are future architecture items, not current repository services.

## Invariants

1. Extend the observed Spring Boot backend; do not replace it with the older NestJS/Prisma proposal without an explicit architecture decision.
2. Keep database changes in the configured Flyway migration directory and align migrations with JPA mappings.
3. Preserve organization/branch/property ownership and ensure a property's organization owns its branch.
4. Preserve the uniqueness of branch codes within an organization and property codes within a branch.
5. Store credentials and secrets in environment-specific configuration, never committed source.
6. Do not claim a module, security behavior, frontend, or integration is complete until it exists in code and has been checked.
7. Keep financial and operational changes auditable and transaction-safe as those modules are implemented.
8. Keep user data and access scoped to the correct organization/branch as authorization is introduced.
