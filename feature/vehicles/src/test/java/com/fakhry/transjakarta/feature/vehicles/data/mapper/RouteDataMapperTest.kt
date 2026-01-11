package com.fakhry.transjakarta.feature.vehicles.data.mapper

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.RouteAttributesDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.RouteDataDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RouteDataMapperTest {

    @Test
    fun `maps route dto to domain model`() {
        val dto = RouteDataDto(
            id = "route-1",
            attributes = RouteAttributesDto(
                shortName = "1",
                longName = "First Route",
            ),
        )

        val route = dto.toRoute()

        assertEquals("route-1", route.id)
        assertEquals("1", route.shortName)
        assertEquals("First Route", route.longName)
    }

    @Test
    fun `maps list of dto to list of domain models`() {
        val dtos = listOf(
            RouteDataDto(
                id = "route-1",
                attributes = RouteAttributesDto(shortName = "1", longName = "First"),
            ),
            RouteDataDto(
                id = "route-2",
                attributes = RouteAttributesDto(shortName = "2", longName = "Second"),
            ),
        )

        val routes = dtos.toRoutes()

        assertEquals(2, routes.size)
        assertEquals("route-1", routes[0].id)
        assertEquals("First", routes[0].longName)
        assertEquals("route-2", routes[1].id)
        assertEquals("Second", routes[1].longName)
    }
}
