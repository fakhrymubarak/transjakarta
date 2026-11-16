# 🚌 Transjakarta Fleet Tracker

[![CI](https://github.com/fakhrymubarak/transjakarta/actions/workflows/ci.yaml/badge.svg?branch=main)](https://github.com/fakhrymubarak/transjakarta/actions/workflows/ci.yaml)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg?style=flat&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue.svg?style=flat&logo=android)
![Android](https://img.shields.io/badge/Android-SDK%2024%2B-green.svg?style=flat&logo=android)
![Coverage](https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/fakhrymubarak/ef8d5550c463be55913f8799450a0a3b/raw/kover-badge-transjakarta.json)

A modern, native Android application built to track the Transjakarta fleet in real-time. Experience seamless navigation, powerful filtering, and live vehicle tracking, all powered by the robust MBTA v3 API.

<video src="docs/assets/showcase.mp4" width="300" controls></video>

## ✨ Key Features

*   **📍 Live Vehicle Tracking**: Visualize vehicles on an interactive map with real-time bearing and route polylines.
*   **🔍 Powerful Filtering**: Filter vehicles by **Route** and **Trip** with sticky headers and instant search.
*   **📄 Smart Pagination**: Infinite scrolling with efficient data loading and pull-to-refresh support.
*   **🎨 Modern UI**: Built 100% with **Jetpack Compose** and **Material 3**, featuring smooth animations and a premium look.
*   **🛣️ detailed Insights**: View vehicle status, upcoming stops, and "From -> To" route directions at a glance.

## 🛠️ Tech Stack

Built with modern Android development best practices:

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
*   **Architecture**: MVVM + Clean Architecture (Domain, Data, Presentation layers)
*   **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
*   **Networking**: [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/)
*   **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
*   **Maps**: [Google Maps Compose](https://github.com/googlemaps/android-maps-compose)
*   **Pagination**: [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3)
*   **Testing**: JUnit 5, Mockito, Coroutines Test
*   **Quality**: Ktlint, Kover (Coverage)

## 🚀 Setup & Installation

### Prerequisites
*   Android Studio Giraffe or newer (JDK 17 bundled)
*   Android SDK 24+
*   Google Maps API Key
*   MBTA API Key

### Configuration
1.  Copy the template and fill in your keys:
    ```bash
    cp secret.properties.example secret.properties
    ```
    ```properties
    MBTA_API_KEY=your_mbta_api_key_here   # Get one: https://api-v3.mbta.com/portal
    MAPS_API_KEY=your_maps_api_key_here   # Get one: https://developers.google.com/maps/documentation/android-sdk/get-api-key
    ```
    `secret.properties` is git-ignored; never commit it.
2.  Sync the project with Gradle.

### Build & Run
*   **Debug APK**: `./gradlew :app:assembleDebug`
*   **Install**: `./gradlew :app:installDebug`
*   **Run Tests**: `./gradlew :feature:vehicles:testDebugUnitTest`

## 🧪 Quality Assurance

This project maintains high code quality standards efficiently:

*   **CI/CD**: GitHub Actions pipeline runs Lint, Unit Tests, and Coverage checks on every push to `main`.
*   **Linting**: Enforced via `ktlint` to ensure consistent code style.
*   **Commands**:
    *   Run all checks: `./gradlew ktlintCheck lint testDebugUnitTest koverVerify`

## 📂 Project Structure

```text
├── app/                      # Android app module
├── build-logic/              # Custom Gradle plugins and conventions
├── core/
│   ├── designsystem/         # :core:designsystem (UI components, theme)
│   ├── domain/               # :core:domain (Use cases, models)
│   ├── networking/           # :core:networking (Retrofit, OkHttp)
│   └── utils/                # :core:utils (Extensions, helpers)
├── feature/
│   └── vehicles/             # :feature:vehicles
│       ├── data/             # Repository impl, Data sources, Mappers
│       ├── di/               # Dependency Injection (Hilt modules)
│       ├── domain/           # Use cases, Repository interfaces, Entities
│       └── presentation/     # UI (Composables), ViewModels, State
├── docs/
│   ├── adrs/                 # Architecture Decision Records
│   ├── api/                  # API Specifications (Swagger)
│   └── features/             # Feature documentation
├── build.gradle.kts          # Root build file
├── settings.gradle.kts       # Project settings
└── gradle.properties         # Project properties
```

---
*Built with ❤️ by Fakhry*
