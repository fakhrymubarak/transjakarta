package com.fakhry.transjakarta.feature.vehicles.data.repository

import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.RouteAttributesDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.RouteDataDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.RouteResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.RoutesResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.RouteMbtaApiService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class RouteRepositoryImplTest {

    @Test
    fun `fetches all pages sorts by short name and caches`() = runTest {
        val pageSize = 100
        val firstPage = (1..pageSize).map { index ->
            RouteDataDto(
                id = "id$index",
                attributes = RouteAttributesDto(shortName = "route${pageSize - index + 1}"),
            )
        }
        val secondPage = listOf(
            RouteDataDto(
                id = "id101",
                attributes = RouteAttributesDto(shortName = "route0"),
            ),
        )
        val api = RecordingRouteService(
            responses = mapOf(
                0 to RoutesResponse(firstPage),
                pageSize to RoutesResponse(secondPage),
            ),
        )
        val repository = RouteRepositoryImpl(api)

        val result = repository.getRoutes()
        val routes = (result as DomainResult.Success).data

        // Combined and sorted (route0 should come first)
        assertEquals(pageSize + secondPage.size, routes.size)
        assertEquals("id101", routes.first().id)
        assertEquals("route0", routes.first().shortName)

        // Pagination calls
        assertEquals(listOf(0, pageSize), api.callsOffsets)
        assertEquals(listOf(pageSize, pageSize), api.callsLimits)

        // Cached result on second call
        val cachedResult = repository.getRoutes()
        val cached = (cachedResult as DomainResult.Success).data
        assertSame(routes, cached)
        assertEquals(listOf(0, pageSize), api.callsOffsets)
    }

    @Test
    fun `getRouteById returns mapped route`() = runTest {
        val dto = RouteDataDto(
            id = "r1",
            attributes = RouteAttributesDto(shortName = "sn", longName = "ln"),
        )
        val api = RecordingRouteService(mapOf())
        api.singleResponse = RouteResponse(data = dto)

        val repository = RouteRepositoryImpl(api)
        val result = repository.getRouteById("r1")

        val route = (result as DomainResult.Success).data
        assertEquals("r1", route.id)
        assertEquals("sn", route.shortName)
    }

    private class RecordingRouteService(
        private val responses: Map<Int, RoutesResponse>,
    ) : RouteMbtaApiService {
        val callsOffsets = mutableListOf<Int>()
        val callsLimits = mutableListOf<Int>()
        var singleResponse: RouteResponse? = null

        override suspend fun getRoutes(offset: Int, limit: Int, fields: String): RoutesResponse {
            callsOffsets += offset
            callsLimits += limit
            return responses[offset] ?: RoutesResponse(emptyList())
        }

        override suspend fun getRoute(id: String, fields: String) =
            singleResponse ?: error("not used in test")
    }
}
