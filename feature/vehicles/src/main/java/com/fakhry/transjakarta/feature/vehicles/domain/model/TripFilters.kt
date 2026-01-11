package com.fakhry.transjakarta.feature.vehicles.domain.model

data class TripFilters(
    val routeIds: Set<String> = emptySet(),
    val nameQuery: String = "",
) {
    val isEmpty: Boolean
        get() = routeIds.isEmpty() && nameQuery.isBlank()
}
