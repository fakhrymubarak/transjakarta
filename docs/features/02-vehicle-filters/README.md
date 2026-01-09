# Vehicle Filters (Route and Trip)

- Status: Draft
- Owner: TBD
- Last updated: 09/01/2026

## Overview
Enable multi-select filtering by route and trip so users can narrow the vehicle list to relevant services.

## Goals
- Allow selecting multiple routes and trips.
- Apply filters to the vehicle list query.
- Show active filter state and allow clear/reset.

## Non-goals
- Free-text search or fuzzy matching.
- Persisting filters across user accounts.

## User flow
1) User opens filter UI from the vehicle list.
2) App loads available routes and trips.
3) User selects one or more routes and/or trips.
4) User applies filters; list refreshes with filtered results.
5) User clears filters to return to default list.

## Screens
- Filter Sheet: route and trip multi-select controls.

## Data sources
- Remote: `GET /routes`, `GET /trips` for filter options.
- Remote: `GET /vehicles` with `filter[route]` and `filter[trip]` (comma-separated IDs).
- Local: selected filter IDs stored in ViewModel; optional persistence in DataStore.

## Architecture
- Presentation: Compose filter sheet with multi-select chips or checkboxes.
- Domain: `FetchRoutes`, `FetchTrips`, and `ApplyVehicleFilters` use cases.
- Data: `RouteRepository`, `TripRepository`, and `VehicleRepository`.

## Dependencies
- Optional DataStore for persisting selections.
- Reuse existing networking stack (Retrofit/OkHttp).

## State management
- `FilterViewModel` holds selected routeIds/tripIds and loading/error states for options.
- Applied filters are exposed to the list screen via shared state or navigation result.

## Error handling
- If routes or trips fail to load, show error with retry and keep filters disabled.
- If applying filters fails, show list error state with retry.

## Performance considerations
- Cache route and trip lists in memory to avoid repeated fetches.
- Avoid blocking list updates; apply filters via fresh list request.

## Security/Privacy
- No personal data; optional API key handled by API client.

## Analytics/telemetry
- `filters_opened`
- `filters_applied`
- `filters_cleared`

## Testing plan
- Unit: build query params for `filter[route]` and `filter[trip]`.
- Widget: multi-select UI selection and clear.
- Integration: filtered `GET /vehicles` results.

## Rollout/flags
- None.

## Open questions
- Should we support search or pagination for large route/trip lists?
- Should filter selections persist across app restarts?
