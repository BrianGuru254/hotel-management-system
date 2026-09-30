# Code Standards

## General

- Read these context files and the relevant implementation before changing code.
- Make focused changes that fit the current Spring Boot/Maven codebase; preserve unrelated edits already in the working tree.
- Keep implementation and documentation claims aligned. Distinguish implemented behavior from plans.
- Avoid new dependencies unless the requested capability needs them and existing dependencies cannot provide it.

## Java and Spring

- Use Java 21 and the existing Spring Boot 3.5.6 Maven setup.
- Follow the existing `com.hrms.backend` package structure and feature-oriented organization as the codebase grows.
- Use clear DTOs at API boundaries; validate incoming data and avoid exposing persistence entities directly from controllers.
- Keep business rules in services/domain logic rather than controllers or repositories.
- Use explicit transaction boundaries for multi-step writes, especially future reservations, inventory, billing, and payments.
- Preserve UUID identifiers and organization/branch relationships where consistent with the existing domain.
- Avoid broad Lombok or framework changes without a concrete need; match local conventions.

## Persistence and Configuration

- Use PostgreSQL and Spring Data JPA as configured.
- Hibernate schema mode is `validate`; schema changes belong in Flyway migrations.
- Add migrations under `backend/src/main/resources/db/migration/`, the configured classpath location. Check for duplicate historical scripts under `backend/main/resources/db/migration/` before changing schema history.
- Keep application secrets outside committed files; use environment variables or deployment secrets.
- Avoid logging passwords, tokens, payment credentials, or sensitive guest identity data.

## API and Security

- Keep REST endpoints, request/response DTOs, validation, error handling, and API documentation consistent with the project specifications as they are implemented.
- Treat the Spring Security dependency as a capability only; do not assume JWT, login, roles, or permission checks exist until verified.
- Enforce access control on the server and scope data queries to the caller’s authorized organization/branch.

## Frontend (When Added)

- The documented target is Next.js + TypeScript, but no frontend code was present at inspection. Confirm the actual setup before adding or changing it.
- Use shared locale formatting, design tokens, accessible semantic elements, responsive layouts, and explicit loading/error/empty states.
- Do not introduce the portfolio template’s React/Vite assumptions into this project.

## Documentation

- Update the relevant context file when scope, architecture, UI direction, conventions, or project progress changes.
- Keep `progress-tracker.md` factual and list next work in a small, actionable unit.
