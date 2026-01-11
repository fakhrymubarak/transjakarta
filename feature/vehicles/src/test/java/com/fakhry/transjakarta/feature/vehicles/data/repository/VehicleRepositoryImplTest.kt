package com.fakhry.transjakarta.feature.vehicles.data.repository

import androidx.paging.testing.asSnapshot
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleAttributesDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleDataDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehiclesResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.VehicleMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleFilters
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VehicleRepositoryImplTest {
    @Test
    fun `getVehiclesPagingFlow loads first page from api`() = runTest {
        val response = VehiclesResponse(data = createTestVehicles(10))
        val fakeApi = RecordingVehicleMbtaApiService(response)
        val repository = VehicleRepositoryImpl(fakeApi)

        val snapshot = repository.getVehiclesPagingFlow(
            VehicleFilters(
                routeIds = setOf("route-1"),
                tripIds = setOf("trip-1"),
            ),
        ).asSnapshot()

        assertEquals(20, snapshot.size)
        assertEquals("v1", snapshot.first().id)
        assertEquals(
            listOf(
                VehicleRequest(
                    filters = mapOf(
                        "filter[route]" to "route-1",
                        "filter[trip]" to "trip-1",
                    ),
                    offset = 0,
                    limit = 10,
                    fields = "label,current_status,latitude,longitude,updated_at",
                ),
                VehicleRequest(
                    filters = mapOf(
                        "filter[route]" to "route-1",
                        "filter[trip]" to "trip-1",
                    ),
                    offset = 10,
                    limit = 10,
                    fields = "label,current_status,latitude,longitude,updated_at",
                ),
            ),
            fakeApi.requests,
        )
    }
}

private data class VehicleRequest(
    val filters: Map<String, String>,
    val offset: Int,
    val limit: Int,
    val fields: String,
)

private class RecordingVehicleMbtaApiService(
    private val response: VehiclesResponse,
) : VehicleMbtaApiService {
    val requests = mutableListOf<VehicleRequest>()

    override suspend fun getVehicles(
        filters: Map<String, String>,
        offset: Int,
        limit: Int,
        fields: String,
    ): VehiclesResponse {
        requests.add(
            VehicleRequest(
                filters = filters,
                offset = offset,
                limit = limit,
                fields = fields,
            ),
        )
        return response
    }

    override suspend fun getVehicle(id: String, include: String, fields: String): VehicleResponse {
        error("Not used in VehicleRepositoryImplTest")
    }
}

private fun createTestVehicles(count: Int): List<VehicleDataDto> = (1..count).map { i ->
    VehicleDataDto(
        id = "v$i",
        type = "vehicle",
        attributes = VehicleAttributesDto(
            label = "Vehicle $i",
            currentStatus = "IN_TRANSIT_TO",
            latitude = 42.0 + i * 0.01,
            longitude = -71.0 - i * 0.01,
            updatedAt = "2024-01-15T14:30:00-05:00",
        ),
    )
}
