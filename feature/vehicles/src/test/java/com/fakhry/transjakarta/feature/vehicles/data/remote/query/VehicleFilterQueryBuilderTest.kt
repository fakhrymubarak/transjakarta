package com.fakhry.transjakarta.feature.vehicles.data.remote.query

import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleFilters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class VehicleFilterQueryBuilderTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("queryCases")
    fun `build returns expected filter query`(
        description: String,
        routeIds: Set<String>,
        tripIds: Set<String>,
        expected: Map<String, String>,
    ) {
        val filters = VehicleFilters(routeIds = routeIds, tripIds = tripIds)

        val actual = VehicleFilterQueryBuilder.build(filters)

        assertEquals(expected, actual, description)
    }

    companion object {
        @JvmStatic
        fun queryCases(): List<Arguments> = listOf(
            Arguments.of(
                "empty filters",
                emptySet<String>(),
                emptySet<String>(),
                emptyMap<String, String>(),
            ),
            Arguments.of(
                "route only",
                setOf("route-1"),
                emptySet<String>(),
                mapOf("filter[route]" to "route-1"),
            ),
            Arguments.of(
                "trip only",
                emptySet<String>(),
                setOf("trip-1"),
                mapOf("filter[trip]" to "trip-1"),
            ),
            Arguments.of(
                "route and trip",
                setOf("route-2", "route-1"),
                setOf("trip-2", "trip-1"),
                mapOf(
                    "filter[route]" to "route-1,route-2",
                    "filter[trip]" to "trip-1,trip-2",
                ),
            ),
        )
    }
}
