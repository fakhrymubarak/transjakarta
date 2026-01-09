# Live Map Tracking

- Status: Good to go
- Owner: Fakhry
- Last updated: 09/01/2026

## Overview
Provide a live map screen in the Maps tab that tracks a selected vehicle by polling the MBTA API every 10 seconds and updating the marker position. The UI should show the latest position timestamp and handle temporary network failures gracefully.

## Goals
- Update vehicle position on the map every 10 seconds while the screen is visible.
- Keep the last known position visible between polls.
- Surface staleness when updates are delayed.

## Non-goals
- WebSocket or push-based real-time streaming.
- Background tracking when the app is not in the foreground.

## User flow
1) User opens the dashboard and selects the Maps tab.
2) User selects a vehicle to track.
3) App fetches the current vehicle position and renders the marker.
4) App polls every 10 seconds to refresh location and timestamp.
5) User leaves the screen; polling stops automatically.

## Screens
- Maps Tab: full map with a vehicle marker, last-updated time, status chips, and a vehicle selector.

## Data sources
- Remote: `GET /vehicles/{id}` with `fields[vehicle]=latitude,longitude,updated_at,current_status` and `include=route,trip` on initial load.
- Local: in-memory cache of last known position and timestamp.

## Architecture
- Presentation: MVVM screen using Compose + Google Maps SDK (Maps Compose or MapView interop).
- Domain: `TrackVehicle` use case with polling loop.
- Data: `VehicleRepository` via Retrofit + OkHttp and Kotlinx Serialization.

## Dependencies
- Google Maps SDK for Android.
- Retrofit + OkHttp.
- Kotlinx Serialization.
- Kotlin coroutines for polling.

## State management
- `LiveMapViewModel` exposes `StateFlow<LiveMapUiState>` with position, status, timestamp, and error flags.
- Polling uses `viewModelScope` with `repeatOnLifecycle` to start/stop on visibility.

## Error handling
- On failure, keep the last known marker and show a non-blocking error state.
- If no successful update for 30 seconds, show a "stale" indicator.

## Performance considerations
- Use a single polling job; cancel on screen exit.
- Avoid redundant map re-renders when data is unchanged.

## Security/Privacy
- Public MBTA data only; no user data stored.

## Analytics/telemetry
- `live_map_opened`
- `live_map_poll_success`
- `live_map_poll_failed`

## Testing plan
- Unit: polling interval logic and staleness detection.
- UI: map UI renders marker and timestamp.
- Integration: mock API to verify 10s refresh cycle.

## Rollout/flags
- None.

## Open questions
- None.
