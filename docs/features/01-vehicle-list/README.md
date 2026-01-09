# Vehicle List and Pagination

- Status: Good to go
- Owner: Fakhry
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
1) User opens the app and lands on the dashboard.
2) User selects the List tab in bottom navigation.
3) Initial page loads and renders cards.
4) User scrolls to the end and the next page loads.
5) User pulls to refresh to reload the list.
6) User taps a vehicle to open the detail screen.

## Screens
- List Tab: scrollable list of vehicle cards with pagination and refresh.

## Data sources
- Remote: `GET /vehicles` with `page[limit]=10`, `page[offset]`, and optional `fields[vehicle]=label,current_status,latitude,longitude,updated_at`.
- Local: Paging 3 state (PagingData + cached paging stream in ViewModel).

## Architecture
- Presentation: MVVM screen using Jetpack Compose `LazyColumn`, `VehicleCard`, Paging 3 `LazyPagingItems`, and pull-to-refresh indicator.
- Domain: `FetchVehiclesPage` use case.
- Data: `VehicleRepository` backed by MBTA API client (Retrofit + OkHttp) and Kotlinx Serialization models.

## Dependencies
- Retrofit + OkHttp for REST calls.
- Kotlinx Serialization for JSON parsing.
- AndroidX Paging 3 with `paging-compose`.
- Jetpack Compose Material3 for UI components.

## State management
- `VehicleListViewModel` owns list state, paging stream, loading flags, and errors.
- Expose `PagingData` via `StateFlow` plus a lightweight `UiState` for non-paging UI states.

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
- UI: list renders cards with correct fields.
- Integration: `GET /vehicles` pagination and refresh flow.

## Rollout/flags
- None.

## Decisions
- Pagination: use AndroidX Paging 3 with `paging-compose`.
- Relationships: fetch `route` and `trip` only on the detail screen.

Pros and cons for list relationships:
- Include `route` and `trip` in list calls:
  - Pros: detail screen can render immediately with no extra request.
  - Cons: larger payloads slow paging and increase data usage/rate-limit pressure.
- Fetch `route` and `trip` on detail only:
  - Pros: faster list loads and smaller responses; reduces unused data.
  - Cons: extra network call on detail and a short loading state.
