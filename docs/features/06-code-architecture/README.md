# Code Architecture

- Status: Good to go
- Owner: Fakhry
- Last updated: 09/01/2026

## Overview
Define the project-wide architecture for a scalable Android codebase using clean architecture, multi-module structure, and quality gates enforced by CI.

## Goals
- Standardize module boundaries and ownership.
- Use clean architecture (presentation, domain, data) per module.
- Enforce code style and coverage via tooling and CI.

## Non-goals
- Migrating existing legacy modules.
- Supporting non-Android platforms.

## User flow
1) Developer adds a new feature module under `:feature:<name>`.
2) Module follows clean architecture layers and depends on `:core:*`.
3) CI validates formatting, tests, and coverage before merge.

## Screens
- N/A (architecture-only).

## Data sources
- Remote: defined in `:core:networking` and consumed by data layers.
- Local: shared analytics in `:core:analytics` and focused utilities in `:core:utils`.

## Architecture
- Build logic: use `buildLogic` for custom Gradle plugins and shared conventions.
- Modules:
  - Feature modules: `:feature:<name>` (e.g., `:feature:vehicles`, `:feature:maps`).
  - Core modules: `:core:networking`, `:core:analytics`, `:core:utils`.
- Clean architecture per module:
  - Presentation: UI + ViewModel.
  - Domain: use cases and entities.
  - Data: repositories, DTOs, and data sources.
## Core module scope
- `:core:utils` is intentionally small and only contains:
  - Lightweight, generic helpers (e.g., `Result` wrappers, date/time formatting, string formatting).
  - Constants and shared types that are not feature-specific.
  - Coroutine dispatchers or threading helpers.
- Avoid turning `:core:utils` into a catch-all "god module"; if a utility is feature-specific, keep it inside the feature module.

## Project structure
```text
.
├── app/                      # Android app module
├── buildLogic/               # Custom Gradle plugins and conventions
├── core/
│   ├── analytics/            # :core:analytics
│   ├── networking/           # :core:networking
│   └── utils/                # :core:utils
├── feature/
│   ├── vehicles/             # :feature:vehicles
│   ├── maps/                 # :feature:maps
│   └── filters/              # :feature:filters
├── docs/
│   ├── features/
│   └── adrs/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradle/
```

## Dependencies
- `org.jlleitschuh.gradle.ktlint` for formatting enforcement.
- `org.jetbrains.kotlinx.kover` with minimum test coverage of 90%.

## State management
- MVVM with `StateFlow` in presentation layers.

## Error handling
- Centralize common error models in `:core:utils`.
- Map network errors in `:core:networking` to UI-friendly states.

## Performance considerations
- Keep feature modules lean; avoid cross-feature dependencies.
- Use dependency inversion to keep domain pure.

## Security/Privacy
- Centralize secrets handling in build config and CI secrets.
- Avoid committing API keys or tokens to VCS.

## Analytics/telemetry
- Provide analytics interfaces in `:core:analytics` and implement per feature.

## Testing plan
- Unit: domain use cases and repository logic per module.
- UI: Compose screens with ViewModel-driven state.
- Integration: feature modules with mocked networking.
- Coverage: enforce 90% minimum via Kover.

## Rollout/flags
- CI quality gates via GitHub Actions on push and pull request to `master`:
  - ktlint check
  - Unit tests
  - Kover coverage threshold

## Git hooks
- Use local git hooks for fast feedback:
  - `pre-commit`: run ktlint and unit tests.
  - `pre-push`: run unit tests and Kover coverage check.

## Open questions
- None.
