# Transjakarta Android Fleet App

A native Android application that consumes the MBTA v3 API to list, filter, and inspect public transit vehicles with live mapping. The app targets Android API 24+ and uses Kotlin, Jetpack Compose, and MVVM with Retrofit + OkHttp and Kotlinx Serialization.

## Data Source
- Primary API: MBTA v3 (Swagger: `docs/api/swagger.json`)
- Key endpoints: `/vehicles` (listing), `/routes`, `/trips`, `/stops`, `/vehicles/{id}`
- Optional API key for higher rate limits: send via `x-api-key` header or `api_key` query param.

## Prerequisites
- Android Studio (Giraffe or newer) with bundled JDK 17
- Android SDK 24+
- Google Maps API key stored locally (never committed)

## Setup
1) Configure secrets in `secret.properties` (create if absent):
```
MBTA_API_KEY=your_api_key_here    # optional but recommended for rate limits
GOOGLE_MAPS_API_KEY=your_maps_key # required for map screens
```
2) Sync the project in Android Studio or from the CLI.

## Build & Run
- Build debug APK: `./gradlew :app:assembleDebug`
- Install on a connected device/emulator: `./gradlew :app:installDebug`
- Run tests for the vehicles feature: `./gradlew :feature:vehicles:testDebugUnitTest`
- Code style and coverage gates are applied via ktlint/kover in the build logic; CI runs on pushes/PRs to `master`.

## Project Layout
- `app/` — application entry point, navigation, and wiring
- `core/` — shared modules (`domain`, `networking`, `designsystem`)
- `feature/vehicles/` — vehicle list, filters, details, and map tracking flows
- `build-logic/` — custom Gradle plugins (ktlint, kover, Android defaults)
- `docs/` — requirements, ADRs, and API reference (`docs/api/swagger.json`)

## Notes
- Google Maps key must be injected via secrets; do not hardcode.
- Rate limiting is handled per MBTA guidelines; user-friendly messages surface when limits are hit.
