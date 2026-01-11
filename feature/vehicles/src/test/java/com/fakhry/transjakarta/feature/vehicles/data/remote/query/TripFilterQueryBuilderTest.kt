package com.fakhry.transjakarta.feature.vehicles.data.remote.query

import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TripFilterQueryBuilderTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("queryCases")
    fun `build returns expected filter query`(
        description: String,
        routeIds: Set<String>,
        nameQuery: String,
        expected: Map<String, String>,
    ) {
        val filters = TripFilters(routeIds = routeIds, nameQuery = nameQuery)

        val actual = TripFilterQueryBuilder.build(filters)

        assertEquals(expected, actual, description)
    }

    companion object {
        @JvmStatic
        fun queryCases(): List<Arguments> = listOf(
            Arguments.of(
                "empty filters",
                emptySet<String>(),
                "",
                emptyMap<String, String>(),
            ),
            Arguments.of(
                "route only",
                setOf("route-2", "route-1"),
                "",
                mapOf("filter[route]" to "route-1,route-2"),
            ),
            Arguments.of(
                "name only",
                emptySet<String>(),
                "Trip 42",
                mapOf("filter[name]" to "Trip 42"),
            ),
            Arguments.of(
                "route and name",
                setOf("route-1"),
                "Express",
                mapOf(
                    "filter[route]" to "route-1",
                    "filter[name]" to "Express",
                ),
            ),
            Arguments.of(
                "name trimmed",
                emptySet<String>(),
                "  Local ",
                mapOf("filter[name]" to "Local"),
            ),
        )
    }
}
