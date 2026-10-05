# Progress Tracker

## Current Phase

Restaurant/POS is in progress. Restaurant and table management is the current feature on branch `codex/restaurants`.

## Current Goal

Continue the backend module sequence with independently reviewable features on separate Git branches. Restaurant and restaurant-table management are implemented in the current feature branch.

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
- Guest profiles are owned by one Property. Guest create/list/get/update, filtering, sorting, paging, duplicate checks, and live ROLE + SCOPE authorization are implemented; guests are not shared across properties.
- Flyway V006 adds property-linked guest storage, a property-local guest number, property-local case-insensitive identity uniqueness, and the guest capabilities for FRONT_DESK, RESERVATIONS_AGENT, and SUPER_ADMIN.
- Guest integration coverage verifies property isolation, duplicate identity behavior, validation errors, not-found handling, and page-size limits.
- Room Types are property-owned, with CRUD, active/inactive status, filters, pagination, case-insensitive property-local code uniqueness, and live capability/scope authorization.
- Amenities are property-owned, with CRUD, filters, pagination, case-insensitive property-local name uniqueness, and live capability/scope authorization. Room-to-amenity assignment is implemented in V008.
- Flyway V007 adds the property-owned Room Type and Amenity tables, checks, indexes, case-insensitive unique indexes, and initial catalog capabilities. The migration source is present; it has not been applied to the remote database in this session.
- The catalog integration test checks CRUD, property isolation, cross-tenant denial, duplicate handling, validation, pagination limits, and not-found responses.
- Rooms are property-owned, reference active Room Types from the same property, enforce property-local case-insensitive room-number uniqueness, and support filters, pagination, CRUD, status changes, and amenity assignment.
- Housekeeping and maintenance status changes use dedicated capabilities and limited status groups. Room and room-amenity property consistency is also constrained by composite database foreign keys.
- Flyway V008 adds Rooms, composite scope constraints, the property-scoped room-amenity join, indexes, and initial room capabilities. The migration source is present; it has not been applied to the remote database in this session.
- Reservations are property-owned and validate their Guest, Room Type, and optional Room against the same Property. Creation and update check room-type capacity across date overlaps; exact Room allocations reject conflicting reservations. Status changes and their history are recorded, and authorization is enforced against live capability and property scope.
- Flyway V009 adds reservations, reservation status history, property-scoped composite foreign keys, uniqueness/indexes, checks, and the initial reservation capability mapping. The migration is additive and has not been applied to the remote database in this session.
- Stays are property-owned, linked to same-property guests/rooms and optionally a reservation. A confirmed reservation or a scoped walk-in can be checked in; active stays lock their room, and check-in/check-out are audited.
- Stay guest assignments are property-constrained, support one primary guest per stay, and prevent a guest list from exceeding the room capacity.
- Housekeeping supports property-scoped tasks, active-user assignment, status transitions, pagination and filtering. Completing cleaning/deep-cleaning tasks returns DIRTY/CLEANING rooms to AVAILABLE. Flyway V011 adds the task table with same-property room constraints.
- Maintenance supports property-scoped issue reporting, technician assignment, start/resolve/close/cancel transitions, room blocking/restoration, filtered pagination, and status history. Flyway V012 adds requests/history with a same-property room FK and one active room-blocking request per room.
- Check-out marks the room DIRTY for housekeeping and can complete its reservation after the associated rooms have checked out. Checkout preview returns an accommodation and deposit estimate; payment processing is not implemented in this module.
- Flyway V010 adds property-scoped stays, guest assignments, stay status history, an active-room uniqueness constraint, indexes, checks, and initial Stay/front-desk capabilities. The migration is additive and has not been applied to the remote database in this session.
- Flyway validated and applied through V005 against PostgreSQL 17.11. The application started with Hibernate `ddl-auto=validate`; live health returned 200 and an unauthenticated organization request returned 401.
- Supabase PostgreSQL 17.11 is connected through its IPv4 Session pooler. Flyway applied V001–V005 in the dedicated `hms` schema; Hibernate `ddl-auto=validate` succeeded, `/actuator/health` returned `UP`, and unauthenticated `GET /api/v1/organizations` returned 401. Existing Supabase `public` objects were left untouched; baseline-on-migrate remains disabled.
- End-to-end H2 PostgreSQL-mode tests cover context loading, hierarchy foreign keys, bootstrap, BCrypt hashing, refresh rotation/reuse, expiry, 19 roles, organization/branch/property/department scope boundaries, property-scoped guests/catalogs, Rooms, Reservations, Stays, guest assignment, check-in, walk-ins, checkout, and inventory availability. The latest Maven suite passed with 27 tests, 0 failures, 0 errors, and 0 skipped.
- Requirements, UX, API, architecture, Spring backend, and planned Next.js frontend documentation exist.

## In Progress / Repository Notes

- The working tree had pre-existing edits to the Organization and Branch entities. Those changes were preserved, and currency mappings now use Hibernate's CHAR JDBC type to match PostgreSQL `CHAR(3)`.
- There are migration files in both `backend/src/main/resources/db/migration/` and `backend/main/resources/db/migration/`. Only the former matches the configured Flyway classpath location. Reconcile before relying on both sets.
- The checkout contains one physical Organization and Branch entity file each, with filenames matching the public classes. Git records the historical lowercase paths; check path casing in a non-Windows checkout before release.
- The root `src/` directory was empty at inspection. The planned Next.js frontend is not yet available as code.
- The earlier Supabase setup used the Session pooler and isolated `hms` schema; V001–V005 were applied and Hibernate validation/startup were verified at that time. Connection and application-secret environment variables are unavailable in the current session, so V006–V010 and live startup need to be rechecked after those settings are restored. Supabase `public` objects were left untouched.

## Next Up

1. Implement POS/Orders and table-order assignment, then Kitchen, Inventory/Procurement, Finance, and remaining documented modules in dependency order.
2. Re-establish database and signing-secret environment values, then apply/verify V006–V016 against Supabase and confirm Hibernate schema validation and application startup.
3. Keep the frontend untouched while completing the documented backend modules.

## Open Decisions

- Configure deployment-specific `DATABASE_PASSWORD`, `HMS_JWT_SECRET`, `HMS_BOOTSTRAP_SECRET`, and allowed CORS origins outside source control. The current local secrets are Windows user environment values, not repository configuration.
- The role-to-capability seed is an initial least-privilege mapping based on the requested role responsibilities. Review it before implementing operational APIs that consume those capabilities.
- Is the intended frontend definitely Next.js, and where should it live in the repository?

## Architecture Decisions / Confirmed Configuration

- Current backend implementation is Spring Boot + Java 21 + Maven + PostgreSQL + JPA + Flyway.
- The master plan’s NestJS/Prisma backend text is an older/conflicting proposal; do not use it as the current implementation description.
- Authorization is ROLE + SCOPE, and a role name alone never authorizes access. SUPER_ADMIN is the only role allowed SYSTEM scope.
- The owner requested each new feature use a new Git branch. Supabase setup remains on codex/supabase-connection; current guest work is on codex/guest-management.
- Guest Management is on `codex/guest-management`; Room Type and Amenity catalogs are on `codex/room-type-amenity-catalog`.
- Room inventory is implemented on `codex/rooms`, based on the completed Room Type/Amenity catalog branch.
- Reservations are implemented in the current `Hotel-Core` line; Stay/front-desk work is isolated on `codex/stays`.
- Each additional backend feature continues on a dedicated `codex/` branch. The frontend remains untouched.
- Housekeeping is implemented on `codex/housekeeping`; its original user assignments are linked to employees by V014 where possible.
- Maintenance is implemented on `codex/maintenance`; V014 links assigned employees, whose accounts must hold the live maintenance-update capability within the request property.
- Employee management is implemented on `codex/employees`; records are branch-owned, optionally department- and user-linked, organization-unique, and soft-archived.
- Guest History is implemented on `codex/guest-history` for reservations and primary/additional guest stays. Orders, invoices, payments, and loyalty are explicitly returned as unavailable pending those modules.
- Branch-scoped Restaurants and Restaurant Tables are implemented on `codex/restaurants`; Menu, orders, and table-order assignment remain later modules.
- Restaurant Menu Categories and Items are implemented on `codex/menu` with branch visibility, scoped capabilities, same-restaurant category constraints, price/tax/preparation validation, and category soft-deactivation. Flyway V016 adds the menu tables. Maven compile passes; tests and live migration/startup checks have not been run.
- KES and Africa/Nairobi are current defaults in configuration/domain specifications; branch-level formatting and configuration behavior needs confirmation in the full UI/API.
- Migrations should be managed in the configured `backend/src/main/resources/db/migration/` path.

## Session Notes

- Context files are stored under `Documentation/context/` and describe the HMS repository rather than the portfolio starter templates.
- The original foundation milestone passed 19 tests and verified startup against Supabase through V005. The latest Maven suite passes 27 tests using H2 in PostgreSQL mode with Hibernate schema validation enabled.
- In the current verification session, database connection and application secret environment variables were not available. No Supabase migration or startup was attempted; remote V006–V010 status remains unverified.
- Housekeeping code and migration V011 are implemented on `codex/housekeeping`. Tests and live migration were not run. The Maven wrapper's PowerShell cache-path null handling was fixed on the Employee branch; compile now succeeds.
- Maintenance code and migration V012 are implemented on `codex/maintenance`. Tests and live migration have not been run.
- Employee management is implemented on `codex/employees` with migrations V013 and V014; tests and live migration have not been run. Maven compile passes.
- Guest History is implemented on `codex/guest-history`; Maven compile passes. Automated tests and live database checks have not been run.
