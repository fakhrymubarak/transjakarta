# Vehicle Detail

- Status: Draft
- Owner: TBD
- Last updated: 09/01/2026

## Overview
Provide a detailed view for a selected vehicle, including status, location, route, trip, and last update time, and show the vehicle position on a map.

## Goals
- Display full vehicle details from the API.
- Show route and trip data associated with the vehicle.
- Render the current vehicle position on a map.

## Non-goals
- Real-time tracking with continuous polling.
- Editing or dispatch actions.

## User flow
1) User taps a vehicle in the list.
2) Detail screen opens with cached summary data.
3) App fetches full vehicle detail and updates the view.
4) User views details and map.

## Screens
- Vehicle Detail: detailed information and map view.

## Data sources
- Remote: `GET /vehicles/{id}` with `include=route,trip,stop`.
- Remote (if needed): `GET /routes/{id}`, `GET /trips/{id}` for expanded details.
- Local: pass summary data from list to render immediately.

## Architecture
- Presentation: MVVM screen using Compose detail layout and Google Maps composable.
- Domain: `FetchVehicleDetail` use case.
- Data: `VehicleRepository` with detail endpoint and Kotlinx Serialization models.

## Dependencies
- Retrofit + OkHttp for REST calls.
- Kotlinx Serialization for JSON parsing.
- Jetpack Compose Material3 for UI components.
- Google Maps SDK for Android (Maps Compose or MapView interop).

## State management
- `VehicleDetailViewModel` holds detail state, loading, and error status.
- Expose detail state via `StateFlow` and render cached list data before refresh.

## Error handling
- If detail fetch fails, show error with retry and keep cached data visible.
- If map fails to load, show an inline error and keep lat/long visible.

## Performance considerations
- Avoid duplicate detail fetch when returning to the screen.
- Use `fields[vehicle]` to limit payload if needed.

## Security/Privacy
- No user data; vehicle locations are public API data.

## Analytics/telemetry
- `vehicle_detail_viewed`
- `vehicle_detail_load_failed`

## Testing plan
- Unit: mapping of detail response to UI model.
- Widget: detail screen renders fields correctly.
- Integration: `GET /vehicles/{id}` with includes.

## Rollout/flags
- None.

## Open questions
- Do we need periodic refresh for detail data?
- What default zoom/tilt and interaction controls should the map use?
