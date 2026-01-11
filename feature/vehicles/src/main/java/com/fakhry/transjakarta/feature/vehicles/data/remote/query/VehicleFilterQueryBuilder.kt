package com.fakhry.transjakarta.feature.vehicles.data.remote.query

import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleFilters

internal object VehicleFilterQueryBuilder {
    fun build(filters: VehicleFilters): Map<String, String> {
        if (filters.isEmpty) return emptyMap()

        val params = linkedMapOf<String, String>()
        if (filters.routeIds.isNotEmpty()) {
            params[FILTER_ROUTE] = filters.routeIds.sorted().joinToString(SEPARATOR)
        }
        if (filters.tripIds.isNotEmpty()) {
            params[FILTER_TRIP] = filters.tripIds.sorted().joinToString(SEPARATOR)
        }
        return params
    }

    private const val FILTER_ROUTE = "filter[route]"
    private const val FILTER_TRIP = "filter[trip]"
    private const val SEPARATOR = ","
}
