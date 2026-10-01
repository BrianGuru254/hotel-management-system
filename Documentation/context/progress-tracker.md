# Progress Tracker

## Current Phase

Supabase PostgreSQL connection verified after Organization → Branch → Property and Users/Roles/Authentication.

## Current Goal

Connect the backend to Supabase, apply the existing Flyway migrations safely, and verify Hibernate schema validation and protected API startup.

## Completed (Observed)

- Spring Boot 3.5.6 Maven project configured for Java 21.
- PostgreSQL datasource, JPA schema validation, and Flyway are configured.
- Spring Web, Security, Validation, Actuator, and test dependencies are present.
- Organization, Branch, and Property entities, DTOs, repositories, services, REST controllers, validation, and exception handling are implemented.
- Branch creation and update require an existing organization.
- Property creation and update require an existing organization and branch, then verify that the branch belongs to the supplied organization.
- Flyway V003 adds Property, its direct Organization and Branch foreign keys, and a composite ownership foreign key. V004 aligns existing currency columns to `CHAR(3)`; V001/V002 remain unchanged.
- Flyway V005 adds case-insensitive unique indexes, Department hierarchy, the 19 requested roles and capability catalog, scoped user-role assignments, one-time bootstrap state, and hash-only refresh-token storage. It is additive; existing migrations were preserved.
- Spring Security now requires JWT authentication for protected APIs. Resource services check live role/capability/scope assignments; organization, branch, property, and department collection queries use scoped repository lookups.
- Authentication includes one-time SUPER_ADMIN bootstrap, BCrypt password hashing, 15-minute JWTs, HttpOnly/Secure/SameSite=Strict refresh cookies, token rotation, and refresh-family invalidation on replay.
- User and role APIs support scoped user creation, updates, activation, role-scope assignment management, profile/password endpoints, and role/capability catalog access.
- Property/department APIs support hierarchy CRUD and prevent cross-organization or out-of-scope access. Department assignments are constrained to the supplied property hierarchy.
- Flyway validated and applied through V005 against PostgreSQL 17.11. The application started with Hibernate `ddl-auto=validate`; live health returned 200 and an unauthenticated organization request returned 401.
- Supabase PostgreSQL 17.11 is connected through its IPv4 Session pooler. Flyway applied V001–V005 in the dedicated `hms` schema; Hibernate `ddl-auto=validate` succeeded, `/actuator/health` returned `UP`, and unauthenticated `GET /api/v1/organizations` returned 401. Existing Supabase `public` objects were left untouched; baseline-on-migrate remains disabled.
- End-to-end H2 PostgreSQL-mode tests cover context loading, hierarchy foreign keys, bootstrap, BCrypt hashing, refresh rotation/reuse, expiry, 19 roles, and organization/branch/property/department scope boundaries. The full Maven suite passed with 19 tests, 0 failures, 0 errors, and 0 skipped.
- Requirements, UX, API, architecture, Spring backend, and planned Next.js frontend documentation exist.

## In Progress / Repository Notes

- The working tree had pre-existing edits to the Organization and Branch entities. Those changes were preserved, and currency mappings now use Hibernate's CHAR JDBC type to match PostgreSQL `CHAR(3)`.
- There are migration files in both `backend/src/main/resources/db/migration/` and `backend/main/resources/db/migration/`. Only the former matches the configured Flyway classpath location. Reconcile before relying on both sets.
- The checkout contains one physical Organization and Branch entity file each, with filenames matching the public classes. Git records the historical lowercase paths; check path casing in a non-Windows checkout before release.
- The root `src/` directory was empty at inspection. The planned Next.js frontend is not yet available as code.
- On `codex/supabase-connection`, `application.yml` defaults to the supplied Supabase direct endpoint with SSL; the Windows user environment overrides it with the Session pooler URL, username, password, and `DATABASE_SCHEMA=hms`. Random `HMS_JWT_SECRET` and `HMS_BOOTSTRAP_SECRET` values were also generated locally and saved at user scope. Restart the IDE/terminal to pick up environment changes. Supabase's `public` schema has no app tables or Flyway history but Flyway considers it non-empty, so HMS migrations use the isolated `hms` schema.

## Next Up

1. Await the owner's next instruction. Do not begin reservations, rooms, billing, other operational modules, or frontend.

## Open Decisions

- Configure deployment-specific `DATABASE_PASSWORD`, `HMS_JWT_SECRET`, `HMS_BOOTSTRAP_SECRET`, and allowed CORS origins outside source control. The current local secrets are Windows user environment values, not repository configuration.
- The role-to-capability seed is an initial least-privilege mapping based on the requested role responsibilities. Review it before implementing operational APIs that consume those capabilities.
- Is the intended frontend definitely Next.js, and where should it live in the repository?

## Architecture Decisions / Confirmed Configuration

- Current backend implementation is Spring Boot + Java 21 + Maven + PostgreSQL + JPA + Flyway.
- The master plan’s NestJS/Prisma backend text is an older/conflicting proposal; do not use it as the current implementation description.
- Authorization is ROLE + SCOPE, and a role name alone never authorizes access. SUPER_ADMIN is the only role allowed SYSTEM scope.
- The owner requested each new feature use a new Git branch. The active branch for Supabase connection configuration is `codex/supabase-connection`.
- KES and Africa/Nairobi are current defaults in configuration/domain specifications; branch-level formatting and configuration behavior needs confirmation in the full UI/API.
- Migrations should be managed in the configured `backend/src/main/resources/db/migration/` path.

## Session Notes

- Context files are stored under `Documentation/context/` and describe the HMS repository rather than the portfolio starter templates.
- Full `mvn test` passed after the schema configuration change: 19 tests, 0 failures, 0 errors, 0 skipped. Tests use H2 in PostgreSQL mode with Hibernate schema validation enabled. The backend started against Supabase with Flyway at V005, Hibernate `ddl-auto=validate`, health `UP`, and an unauthenticated protected endpoint returning 401. The temporary verification server was stopped after these checks.
