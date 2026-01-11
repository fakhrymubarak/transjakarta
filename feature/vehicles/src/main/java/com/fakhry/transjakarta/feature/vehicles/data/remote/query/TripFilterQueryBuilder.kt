package com.fakhry.transjakarta.feature.vehicles.data.remote.query

import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters

object TripFilterQueryBuilder {
    fun build(filters: TripFilters): Map<String, String> {
        if (filters.isEmpty) return emptyMap()

        val query = mutableMapOf<String, String>()

        if (filters.routeIds.isNotEmpty()) {
            query["filter[route]"] = filters.routeIds.sorted().joinToString(",")
        }

        val trimmedName = filters.nameQuery.trim()
        if (trimmedName.isNotEmpty()) {
            query["filter[name]"] = trimmedName
        }

        return query
    }
}
