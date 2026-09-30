# AI Workflow Rules

## Approach

1. Read all six files in `Documentation/context/` before starting project work, then inspect the relevant source and specification.
2. Work in one cohesive backend or UX unit at a time.
3. Confirm whether a statement is implemented, configured, or merely planned before relying on it.
4. Preserve existing working-tree changes; inspect diffs before editing files that already have local changes.
5. After meaningful implementation work, update the affected context file and progress tracker.

## Scope and Architecture

- Treat the Maven project, active Java source, `application.yml`, and configured Flyway path as the evidence for current behavior.
- Follow the current Spring Boot backend. Do not switch to the older NestJS/Prisma proposal unless an explicit architecture decision changes the project.
- Treat Next.js and other frontend descriptions as planned until source code is present.
- Prefer the smallest change that fulfills the current request. Avoid unrelated refactors and dependency changes.
- Do not implement every module listed in the master plan in one pass; choose a small independently reviewable feature.

## Handling Uncertainty

- Do not invent business rules, permissions, public-facing content, or visual branding.
- If a missing decision blocks work, record it in `progress-tracker.md` and ask a focused question while continuing independent work.
- Treat conflicting documents as proposals; surface conflicts and use checked code/config as current reality.

## Protected Areas

- Preserve pre-existing uncommitted changes. Do not reset, overwrite, or stage them without a request.
- Avoid editing Maven dependencies, database migration history, security configuration, or environment secrets without a concrete feature need.
- Never commit credentials or log sensitive guest, identity, authentication, or payment information.

## Verification

- Follow the user's verification request. For implementation changes, use the narrowest relevant checks and report what was run.
- Do not claim runtime behavior works based only on documentation or dependency presence.
- Update `progress-tracker.md` with completed work, current blockers, and the next concrete task.
