# UI Context

## Product Feel

Design a practical, calm, professional operations interface for hotel and restaurant staff. Optimize for quick scanning and frequent work: arrivals, reservations, room status, orders, payments, and alerts should be easy to find. Keep workflows consistent and avoid unnecessary steps. The UI specification calls for desktop-optimized work areas with responsive behavior for smaller screens.

## Current State

No frontend source was present in the inspected root `src/` directory. The UI guidance below is a design brief from the project UX and Next.js specifications, not an implemented design system. Final visual branding, palette, typography, and component library remain undecided.

## Locale and Formatting

- Initial defaults documented by the project: KES currency and Africa/Nairobi timezone.
- Format amounts, dates, and times consistently with the active property/branch configuration when that capability is added.
- Do not hard-code Kenya defaults into every UI component; use shared configuration/domain formatting.

## Design Principles

- Prioritize fast operational tasks and clear status feedback.
- Use role- and permission-aware navigation when authorization is implemented; hiding a control is not a substitute for backend authorization.
- Present high-priority operational information without excessive navigation.
- Prevent invalid actions with clear validation and confirmation for consequential changes.
- Provide visible loading, empty, success, and error states.
- Use semantic structure, keyboard access, readable contrast, and responsive layouts.
- Keep dense tables and dashboards scannable; provide filtering and pagination for large datasets.

## Information Architecture

The documented management app groups work into Dashboard, Front Office, Reservations, Guests, Rooms, Housekeeping, Maintenance, Restaurant/POS, Kitchen, Inventory, Procurement, Finance, Employees/Admin, Reports, Notifications, Audit, and Settings. Implement only the areas supported by current API capabilities; do not present a planned screen as live functionality.

## Visual Tokens

No approved palette, font, spacing scale, radius, or shadow tokens were found. Centralize tokens when the frontend is established, then select values as a coherent system. Do not carry over the portfolio template’s blue palette or personal-site layout.

## Components and Interaction

Use shared buttons, form controls, tables, status badges, dialogs, navigation, and feedback patterns once a frontend framework and component approach are selected. Make status meaning available through text/icon as well as color. Keep actions labeled clearly, show focus indicators, and respect reduced-motion preferences.

## Imagery and Icons

This is an internal operational application. Prefer functional icons with accessible labels and avoid decorative imagery that competes with work data. Use a consistent icon set if one is adopted; none was confirmed in the codebase.
