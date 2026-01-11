package com.fakhry.transjakarta.feature.vehicles.domain.model

data class VehicleFilters(
    val routeIds: Set<String> = emptySet(),
    val tripIds: Set<String> = emptySet(),
) {
    val isEmpty: Boolean
        get() = routeIds.isEmpty() && tripIds.isEmpty()
}
