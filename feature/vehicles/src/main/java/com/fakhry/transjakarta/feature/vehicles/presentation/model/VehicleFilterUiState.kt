package com.fakhry.transjakarta.feature.vehicles.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class VehicleFilterUiState(
    val routes: List<FilterOptionUiModel> = emptyList(),
    val selectedRouteIds: Set<String> = emptySet(),
    val selectedTripIds: Set<String> = emptySet(),
    val routeSearchQuery: String = "",
    val tripSearchQuery: String = "",
    val isRoutesLoading: Boolean = false,
    val routesError: String? = null,
)
