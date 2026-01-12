package com.fakhry.transjakarta.feature.vehicles.data.repository

import androidx.paging.testing.asSnapshot
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.data.paging.TripPagingSource
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripAttributesDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripDataDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripsResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.TripMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters
import com.fakhry.transjakarta.utils.coroutines.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripRepositoryImplTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testDispatchers = DispatcherProvider(testDispatcher)

    @Test
    fun `empty filters returns empty paging data and does not call api`() =
        runTest(testDispatcher) {
            val api = RecordingTripService()
            val repository = TripRepositoryImpl(api, testDispatchers)

            val items = repository.getTripsPagingFlow(TripFilters()).asSnapshot()

            assertTrue(items.isEmpty())
            assertTrue(api.calls.isEmpty())
        }

    @Test
    fun `non-empty filters calls api and returns trips`() = runTest(testDispatcher) {
        val api = RecordingTripService(
            response = TripsResponse(
                data = listOf(
                    TripDataDto(
                        id = "trip-1",
                        attributes = TripAttributesDto(
                            name = "Trip 1",
                            headsign = "Headsign 1",
                            blockId = "block-1",
                        ),
                    ),
                ),
            ),
        )
        val repository = TripRepositoryImpl(api, testDispatchers)

        val filters = TripFilters(routeIds = setOf("route-1"))
        val items = repository.getTripsPagingFlow(filters).asSnapshot()

        assertEquals(1, items.size)
        val trip = items.first()
        assertEquals("trip-1", trip.id)
        assertEquals("Trip 1", trip.name)
        assertEquals("Headsign 1", trip.headsign)
        assertEquals("block-1", trip.blockId)

        val call = api.calls.single()
        assertEquals(mapOf("filter[route]" to "route-1"), call.filters)
        assertEquals(0, call.offset)
        assertEquals(TripPagingSource.PAGE_SIZE, call.limit)
    }

    @Test
    fun `getTripById returns trip`() = runTest(testDispatcher) {
        val dto = TripDataDto(
            id = "t1",
            attributes = TripAttributesDto(name = "T1", headsign = "H1", blockId = "b1"),
        )
        val api = RecordingTripService()
        api.singleResponse = TripResponse(data = dto)

        val repository = TripRepositoryImpl(api, testDispatchers)
        val result = repository.getTripById("t1")

        assertTrue(result is DomainResult.Success)
        assertEquals("t1", (result as DomainResult.Success).data.id)
    }

    private class RecordingTripService(
        private val response: TripsResponse = TripsResponse(emptyList()),
    ) : TripMbtaApiService {
        data class Call(
            val filters: Map<String, String>,
            val offset: Int,
            val limit: Int,
            val fields: String,
        )

        val calls = mutableListOf<Call>()
        var singleResponse: TripResponse? = null

        override suspend fun getTrips(
            filters: Map<String, String>,
            offset: Int,
            limit: Int,
            fields: String,
        ): TripsResponse {
            calls += Call(filters, offset, limit, fields)
            return response
        }

        override suspend fun getTrip(id: String, fields: String): TripResponse =
            singleResponse ?: error("not used in test")
    }
}
