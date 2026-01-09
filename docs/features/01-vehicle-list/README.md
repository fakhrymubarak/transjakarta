# Vehicle List and Pagination

- Status: Draft
- Owner: TBD
- Last updated: 09/01/2026

## Overview
Provide the main vehicle list in a card-based layout with pagination and pull-to-refresh so users can browse live fleet positions efficiently.

## Goals
- Show key vehicle attributes in a scannable card list.
- Support incremental loading with a fixed page size of 10.
- Allow refresh to reload the latest vehicle data.

## Non-goals
- Offline-first caching or background sync.
- Editing or mutating vehicle data.

## User flow
1) User opens the app and sees the vehicle list.
2) Initial page loads and renders cards.
3) User scrolls to the end and the next page loads.
4) User pulls to refresh to reload the list.
5) User taps a vehicle to open the detail screen.

## Screens
- Vehicle List: scrollable list of vehicle cards with pagination and refresh.

## Data sources
- Remote: `GET /vehicles` with `page[limit]=10`, `page[offset]`, optional `fields[vehicle]=label,current_status,latitude,longitude,updated_at`, and optional `include=route,trip`.
- Local: in-memory list cache and paging state (offset, hasNext).

## Architecture
- Presentation: Jetpack Compose `LazyColumn`, `VehicleCard`, and pull-to-refresh indicator.
- Domain: `FetchVehiclesPage` use case.
- Data: `VehicleRepository` backed by MBTA API client (Retrofit/OkHttp) and DTO mapping.

## Dependencies
- Retrofit + OkHttp for REST calls.
- Kotlinx Serialization or Moshi for JSON parsing.
- Jetpack Compose Material3 for UI components.

## State management
- `VehicleListViewModel` owns list state, pagination offset, loading flags, and errors.
- Expose a single `UiState` to the screen to drive loading/empty/error/content states.

## Error handling
- Initial load failure: show full-screen error with retry.
- Pagination failure: show inline error and allow retry for the next page.
- Handle `429` with a user-friendly message and optional backoff.

## Performance considerations
- Prevent duplicate fetches with an `isFetching` guard.
- Prefetch next page when user nears end of list.
- Keep page size fixed at 10 to align with requirements.

## Security/Privacy
- API key optional; no user data stored or transmitted.

## Analytics/telemetry
- `vehicle_list_viewed`
- `vehicle_list_refreshed`
- `vehicle_page_loaded`
- `vehicle_page_load_failed`

## Testing plan
- Unit: pagination logic, repository mapping, and error states.
- Widget: list renders cards with correct fields.
- Integration: `GET /vehicles` pagination and refresh flow.

## Rollout/flags
- None.

## Open questions
- Use Android Paging 3 or custom pagination?
- Include `route` and `trip` relationships on list calls or fetch on detail only?
