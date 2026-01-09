# Repository Guidelines

## Project Structure & Module Organization
- `app/`: Android application module.
- `app/src/main/java/com/fakhry/transjakarta`: Kotlin source code and Jetpack Compose UI.
- `app/src/main/res`: Android resources (drawables, strings, themes).
- `app/src/test`: Local JVM unit tests.
- `app/src/androidTest`: Instrumentation/UI tests.
- `docs/features`: Feature tech docs (use the template at `docs/features/templates/README.md`).
- `docs/adrs`: Architecture decision records.
- `docs/api/swagger.json`: MBTA API Swagger spec.

## Build, Test, and Development Commands
- `./gradlew assembleDebug`: Build the debug APK.
- `./gradlew installDebug`: Install debug APK to a connected device/emulator.
- `./gradlew test`: Run JVM unit tests.
- `./gradlew connectedAndroidTest`: Run instrumentation tests on a device/emulator.
- `./gradlew lint`: Run Android Lint checks.

## Coding Style & Naming Conventions
- Kotlin + Jetpack Compose; 4-space indentation.
- Classes/objects: `PascalCase`; functions/variables: `camelCase`; constants: `SCREAMING_SNAKE_CASE`.
- Package names are lowercase and follow `com.fakhry.transjakarta`.
- `@Composable` functions should be `PascalCase` and placed near their UI-related helpers.

## Testing Guidelines
- Unit tests use JUnit (`app/src/test`).
- Instrumentation tests use AndroidX Test/Espresso (`app/src/androidTest`).
- Name tests with `*Test` and mirror the production package structure.
- Cover pagination, filter logic, and detail rendering whenever adding features.

## Commit & Pull Request Guidelines
- Use conventional commits: `feat:`, `fix:`, `docs:`, `chore:`, `refactor:`, `test:`.
- Make small, task-scoped commits; avoid bundling unrelated changes.
- PRs should include a short summary, testing notes (commands run), and screenshots/GIFs for UI changes.
- Update or add feature docs when behavior or scope changes.

## Configuration Tips
- Ensure `local.properties` points to your Android SDK path when building locally.
- Keep API keys out of source; prefer Gradle properties or CI secrets.

## Git Hooks
- Shared hooks live in `.githooks/`; enable them with:
  - `git config core.hooksPath .githooks`
- Hooks enforce commit message prefixes and run:
  - `pre-commit`: `./gradlew ktlintCheck test`
  - `pre-push`: `./gradlew test koverVerify`

## Git Hooks
- Shared hooks live in `.githooks/`; enable them with:
  - `git config core.hooksPath .githooks`
- Hooks enforce commit message prefixes and run:
  - `pre-commit`: `./gradlew ktlintCheck test`
  - `pre-push`: `./gradlew test koverVerify`
