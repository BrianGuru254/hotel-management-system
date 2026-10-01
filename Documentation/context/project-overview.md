# HMS Project Overview

## Project

Hotel & Restaurant Management System (HRMS), a hospitality operations platform intended to support hotel and restaurant workflows. The project plan describes a modular monolith, beginning with one property and allowing organization/branch growth. The repository currently contains the backend foundation only; the full planned feature set is not yet implemented.

## Goals

- Establish Organization → Branch → Property administration as the platform foundation.
- Build operational modules incrementally: identity and access, hotel operations, restaurant operations, inventory/procurement, finance, and reporting.
- Provide a responsive, role-aware management interface for staff workflows.
- Keep business data scoped to its organization and branch/property as the domain expands.
- Use Kenya defaults currently reflected in the implementation: KES and Africa/Nairobi.

## Intended Users

Hotel and restaurant administrators/managers, front-office and reception staff, housekeeping and maintenance teams, restaurant and kitchen staff, cashiers, storekeepers, procurement staff, and finance staff. The initial role catalog and scoped backend authorization are implemented; operational modules for those roles are still pending.

## Product Areas

- Organization → Branch → Property administration
- Guests, rooms, room types, rates, reservations, and front office
- Housekeeping and maintenance
- Restaurants, menus, POS, and kitchen operations
- Inventory, suppliers, and procurement
- Billing, invoices, payments, and finance
- Staff, administration, audit, notifications, and reports

## Current Scope and Status

The backend includes Organization, Branch, Property, Department, User, Role, and scoped authorization foundations. Authentication uses short-lived JWT access tokens, BCrypt passwords, and rotated refresh tokens stored as hashes. Protected API services enforce tenant boundaries server-side. No frontend source files were present in the root `src/` folder when inspected; reservations and other operational modules remain planned until implemented and verified.

## Source-of-Truth Notes

- For the code that exists today, use the Maven project and executable source under `backend/src/main` as the authority.
- The planning documents describe proposed scope and include older stack assumptions. In particular, the master plan’s NestJS/Prisma sections conflict with the actual Spring Boot backend. Do not treat planned features or stack choices as implemented facts.
- The repository also contains Spring Boot and Next.js implementation specifications. Treat these as design plans until corresponding code exists.
- Ask before making material product or UX decisions that are not specified. Record unresolved choices in `progress-tracker.md`.
