# Architecture Context

## Current Implemented Stack

| Layer | Technology | Evidence / Role |
| --- | --- | --- |
| Backend runtime | Java 21 | `backend/pom.xml`; Spring Boot runtime |
| Backend framework | Spring Boot 3.5.6 | Maven parent in `backend/pom.xml` |
| Build | Maven / Maven Wrapper | `backend/pom.xml`, `backend/mvnw`, `backend/mvnw.cmd` |
| HTTP | Spring Web | `spring-boot-starter-web` dependency |
| Persistence | Spring Data JPA / Hibernate | JPA dependency; `ddl-auto: validate` |
| Database | PostgreSQL | JDBC URL and PostgreSQL runtime driver; Supabase connection defaults to SSL and supports environment overrides |
| Schema migrations | Flyway | Flyway dependencies; configured classpath location |
| Security | Spring Security resource server + HS256 JWT | Stateless bearer authentication, BCrypt passwords, database-backed role/scope authorization, and refresh-token rotation |
| Validation | Jakarta Bean Validation starter | Present |
| Monitoring | Spring Boot Actuator | Health/info exposure configured |
| Frontend | Not implemented in the inspected root `src/` | Next.js frontend is specified in Documentation, not present as code |

## Repository Boundaries

- `backend/src/main/java/com/hrms/backend/` — Spring Boot application, entities, DTOs, repositories, and future feature packages.
- `backend/src/main/resources/application.yml` — application, database, JPA, Flyway, server, and actuator configuration. The default database target is the supplied Supabase PostgreSQL endpoint with `sslmode=require`; override `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, and `DATABASE_SCHEMA` through the environment. Include `currentSchema` in an overridden JDBC URL. Supabase can use an isolated `hms` schema without baselining its pre-existing `public` objects.
- `backend/src/main/resources/db/migration/` — configured Flyway migration location; V003 adds Properties and ownership constraints, V004 aligns currency columns with `CHAR(3)`, V005 adds departments/users/roles and authentication foundations, V006 adds property-owned guests, V007 adds property-owned room types and amenities, and V008 adds property-owned rooms and same-property amenity assignments.
- `backend/main/resources/db/migration/` — an additional migration-looking directory exists outside the configured runtime path; do not assume Flyway executes it. Resolve duplication deliberately before adding migrations.
- `Documentation/` — requirements, architecture, API, UX, and implementation plans.
- Root `src/` — empty at inspection; no frontend architecture is established in source yet.

## Current Domain Model

The enforced business hierarchy is Organization → Branch → Property. A branch has a required organization FK; a property stores both organization and branch FKs, with a composite FK ensuring the branch belongs to that organization. Departments are scoped to a property within the same branch and organization. Organization names and branch/property/department codes are unique case-insensitively within their respective scopes.

Users receive one or more role-and-scope assignments. Roles reference capabilities; each authorization check requires the capability and a matching scope on the same assignment. SYSTEM scope is restricted to SUPER_ADMIN. Organization, branch, property, department, and self boundaries are enforced by backend services and repository-scoped list queries; JWT role/scope claims do not replace live database checks. Other initial roles are always granted a non-system scope.

Access JWTs are HS256-signed and expire after 15 minutes. They contain user ID, jti, role keys, and scope identifiers, with no password or email. Refresh tokens are random, rotated, and stored only as SHA-256 hashes. Reuse of a revoked token invalidates the family. The refresh token is returned in an HttpOnly, Secure, SameSite=Strict cookie. Initial system-admin creation is one-time and requires an environment-provided bootstrap secret.

## Planned System Shape

The project documents a modular monolith with a REST API and a separate management web frontend. The implementation specifications propose Next.js + TypeScript for the frontend and Spring Boot for the API. PostgreSQL is the system of record. Redis, queues, external integrations, and public booking website are future architecture items, not current repository services.

## Guest Data Scope

Guest profiles are property-owned. The guest's property determines its organization and branch, and all guest list/detail/create/update operations require the matching live capability and property scope. Guest numbers and identification numbers are unique within a property; a guest may have separate profiles at different properties.

## Property Catalog Scope

Room types, amenities, and rooms are property-owned. Catalog and room list queries first resolve properties visible through the user's current live ROLE + SCOPE assignments, and detail/create/update/delete operations authorize against the owning property's organization, branch, and property IDs. Room type codes, amenity names, and room numbers are case-insensitively unique within one property.

Room inventory is property-owned and references a room type from that same property. The database uses composite property-scoped foreign keys for rooms and room-amenity assignments in addition to service validation. Housekeeping and maintenance receive status capabilities limited to their operational status group; reservation-aware availability belongs to the Reservations module.

Housekeeping tasks are property-owned and reference a room in that same property. Assignments reference active employees in the same branch and, when department-bound, the same property. Completing room-cleaning/deep-cleaning work returns a DIRTY or CLEANING room to AVAILABLE under the housekeeping room-status capability.

Maintenance requests are property-owned and may reference a same-property room. A room-linked active request locks the room and keeps it out of reservation availability; occupied, reserved, already-maintenance, and out-of-order rooms cannot receive a second maintenance lock. Assignments require an active employee with an account that has scoped maintenance-update permission. Status changes are recorded in a property-constrained history table. Resolve/cancel restores the room to its pre-maintenance cleanable state.

Employees belong to one organization and branch, with an optional department constrained to that same branch and a one-to-one optional user account. HR list/detail/create/update/archive operations enforce live employee capabilities and scope; employee numbers and email addresses are unique case-insensitively within an organization. Housekeeping and Maintenance assignments reference employees after V014.

Reservations and Stays are also Property-scoped. Reservations reference a guest, room type, and optional room from that same property, and the database reinforces this with composite foreign keys. Check-in requires a confirmed reservation or an explicitly scoped walk-in; each active Stay locks one physical room. Guest assignments on a Stay are property-bound. Check-out moves the room to DIRTY and updates the reservation lifecycle. Checkout preview is an estimate from the saved nightly rate and deposit; payments, folios, and invoicing remain later Finance work. Stay checkout is stored as a local date to align with reservation dates, while actual check-in/out timestamps remain timezone-aware.

## Invariants

1. Extend the observed Spring Boot backend; do not replace it with the older NestJS/Prisma proposal without an explicit architecture decision.
2. Keep database changes in the configured Flyway migration directory and align migrations with JPA mappings.
3. Preserve organization/branch/property ownership and ensure a property's organization owns its branch.
4. Preserve the uniqueness of branch codes within an organization and property codes within a branch.
5. Store credentials and secrets in environment-specific configuration, never committed source. Database connections must use TLS; configure `DATABASE_PASSWORD` outside source control.
6. Do not claim a module, security behavior, frontend, or integration is complete until it exists in code and has been checked.
7. Keep financial and operational changes auditable and transaction-safe as those modules are implemented.
8. Keep user data and access scoped to the correct organization/branch as authorization is introduced.
9. Keep REST APIs authenticated by default; grant access only when capability and database-backed resource scope both match.
10. Keep JWT signing/bootstrap secrets in environment variables (`HMS_JWT_SECRET`, `HMS_BOOTSTRAP_SECRET`); configure browser origins explicitly with `app.security.cors.allowed-origins` before cross-origin use.
11. Implement each new feature on its own `codex/` Git branch, as requested by the project owner.
