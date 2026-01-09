# ADR: Choosing map library for Android native (MapLibre vs Google Maps)

- Status: `Accepted`
- Date: `09/01/2026`
- Deciders: `Frontend team`, `Product team`
- Drivers: `Licensing and cost`, `Data source flexibility`, `Offline readiness`, `Android-native performance`, `APK size`, `Time to implement`

## Table of contents
- [Context](#context)
- [Decision](#decision)
- [Options considered](#options-considered)
- [Pros and cons](#pros-and-cons)
- [Comparison](#comparison) *(optional)*
- [Performance and size](#performance-and-size) *(optional)*
- [Consequences and guidelines](#consequences-and-guidelines)
- [References](#references)

## Context
We need a map library for the Transjakarta Android native app (Kotlin) to display routes, stops, and live vehicle positions. The choice must balance cost, licensing risk, visual customization, performance on mid and low-end devices, APK size, and engineering effort. We also want flexibility in tile providers and the ability to add custom overlays and interactions over time.

## Decision
We will use the Google Maps SDK for Android for the initial release because it provides the most complete native feature set (routing-related services, POI data, and reliable device performance) with the lowest engineering risk for our timeline.

## Options considered
1) `MapLibre Native (Android SDK)`
2) `Google Maps SDK for Android`

## Pros and cons

### `MapLibre Native (Android SDK)`
- Pros:
  - Open source native SDK with modern vector styling and smooth interactions.
  - Strong control over styling and custom layers.
  - No mandatory vendor lock-in; can pair with self-hosted or third-party vector tiles.
- Cons:
  - Heavier dependency and higher GPU usage than Leaflet-in-WebView.
  - Requires a vector tile pipeline or a paid provider (MapTiler, Stadia, etc.).
  - Some Android-specific tooling and community support are smaller than Google Maps.

### `Google Maps SDK for Android`
- Pros:
  - High-quality basemap and POI data with familiar UX.
  - Built-in services (Places, Directions, Traffic) with strong documentation.
  - Strong performance and cross-device support with native SDK.
- Cons:
  - Usage-based billing and stricter licensing terms.
  - Vendor lock-in and limited control over base map styling compared to self-hosted solutions.
  - Restricted offline use and caching.

## Comparison
| Criteria           | `MapLibre Native`               | `Google Maps Android SDK` |
|--------------------|---------------------------------|---------------------------|
| Licensing cost     | Low to medium (tiles)           | Medium to high (usage)    |
| Vector tiles       | Native                          | Native                    |
| Styling control    | High                            | Medium                    |
| Offline capability | Possible with self-hosted tiles | Limited by terms          |
| Ecosystem          | Medium                          | Large                     |
| Vendor lock-in     | Low                             | High                      |

## Performance and size
- MapLibre Native has a larger dependency footprint and uses GPU resources for vector rendering.
- Google Maps SDK is heavier but optimized; performance is good with dependency on Play Services and network/quota.

## Consequences and guidelines
- Run a proof-of-concept with identical UX requirements in Kotlin to measure load time, FPS with dense markers, memory usage, and battery impact.
- If choosing MapLibre, plan the vector tile source and hosting strategy; budget for tile costs and cache strategy.
- If choosing Google Maps, enforce API key restrictions, monitor quotas, and document billing assumptions and Play Services dependency.

## References
- https://maplibre.org/maplibre-native/docs/book/android/
- https://developers.google.com/maps/documentation/android-sdk
