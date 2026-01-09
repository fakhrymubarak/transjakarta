# Client Requirements (Technical)

## Project Description
Build a native android application for fleet management that integrates with the MBTA v3 REST API and runs on Android. The app must retrieve vehicle data, list it with pagination, support filtering by route and trip, and provide a vehicle detail view.

## Target Platforms
- Android API level 24 or later

## Technology Stack
- Android Native Kotlin
- REST API integration
- Atomic Components using Jetpack Compose
- MVVM with Jetpack ViewModel and StateFlow
- Retrofit + OkHttp for networking
- Kotlinx Serialization for JSON
- Google Maps SDK for Android

## API Integrations
Primary API specification: `docs/api/swagger.json` (Swagger 2.0, MBTA API v3.0)

Authentication and rate limiting:
- Optional API key for higher rate limits via `x-api-key` header or `api_key` query param.
- Requests without a key are tracked by IP and have stricter rate limits.

Additional APIs for filters (see `docs/api/swagger.json`):
- Routes: `/routes`
- Trips: `/trips`

## Functional Requirements

### 1) Vehicle Data Retrieval
- Fetch vehicle data from the MBTA Vehicle API.
- Implement client-side pagination with a page size of 10 vehicles per fetch.

### 2) Vehicle List (Card View)
- Display vehicles in a card list with the following fields:
  - Vehicle label attribute
  - Current status (e.g., `IN_TRANSIT_TO`, `STOPPED_AT`)
  - Latitude and longitude
  - Last updated timestamp
- Pagination behavior:
  - Load 10 items per fetch.
  - Provide pull-to-refresh for reloading data.
  - Show a loading indicator while fetching more data.

### 3) Filters (Route and Trip)
- Provide filters for Route and Trip based on the corresponding APIs.
- Allow multi-select for both Route and Trip filters.
- Apply selected filters to the vehicle list query.

### 4) Vehicle Detail View
- Show detailed vehicle information, including:
  - Vehicle label attribute
  - Current status
  - Latitude and longitude
  - Last updated timestamp
  - Route data
- Trip data
- Any additional relevant fields
- Show vehicle position on a map using Google Maps SDK for Android.

## Non-Functional Requirements
- All screens must display a loading indicator during data fetches.
- Errors must show a clear, user-friendly message.
