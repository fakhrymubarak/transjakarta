# Loading and Error States

- Status: Good to go
- Owner: Fakhry
- Last updated: 09/01/2026

## Overview
Define consistent loading and error behaviors across all screens to meet UX requirements for data fetching.

## Goals
- Show a clear loading indicator on every data fetch.
- Provide user-friendly error messages with recovery actions.

## Non-goals
- Full offline mode or background sync.
- Custom per-screen error designs beyond shared patterns.

## User flow
1) User opens a screen that requires data.
2) Loading indicator appears until data is available.
3) On failure, an error message with retry is shown.

## Screens
- Vehicle List: full-screen loading and pagination loading.
- Filter Sheet: inline loading for route/trip options.
- Vehicle Detail: full-screen loading with cached summary fallback.

## Data sources
- Remote: all feature endpoints (vehicles, routes, trips).
- Local: cached summaries where available.

## Architecture
- Presentation: shared Compose components for loading and error UI in MVVM screens.
- Domain: unified `UiState` model for loading, content, empty, and error.
- Data: no additional data layers.

## Dependencies
- Jetpack Compose Material3 for shared loading/error components.

## State management
- Each screen ViewModel exposes a `UiState` via `StateFlow` with `isLoading`, `errorMessage`, and `content`.

## Error handling
- Use concise, user-friendly messages; include retry CTA.
- Surface rate limit errors with a clear wait/retry suggestion.
- Use full-screen errors for blocking failures, inline errors for pagination, and snackbars for non-blocking issues.

## Performance considerations
- Avoid blocking UI thread; use coroutines for fetch operations.
- Debounce retries to avoid rapid repeated calls.

## Security/Privacy
- Do not expose sensitive error details in UI.

## Analytics/telemetry
- `error_shown`
- `retry_tapped`

## Testing plan
- Unit: UI state transitions for loading and error.
- UI: error and loading components render correctly.
- Integration: simulate network failures and rate limits.

## Rollout/flags
- None.

## Decisions
- Standard copy examples:
  - Network: "Unable to load data. Check your connection and try again."
  - Rate limit: "Too many requests. Please wait a moment and retry."
  - Unknown: "Something went wrong. Please try again."
- Error presentation: full-screen for blocking errors, inline for partial errors, snackbars for transient issues (no dialogs).
