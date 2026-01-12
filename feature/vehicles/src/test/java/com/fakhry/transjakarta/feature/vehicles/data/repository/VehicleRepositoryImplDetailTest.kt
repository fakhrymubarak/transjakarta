package com.fakhry.transjakarta.feature.vehicles.data.repository

import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleAttributesDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleDataDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.VehicleMbtaApiService
import com.fakhry.transjakarta.utils.coroutines.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleRepositoryImplDetailTest {

    private val testDispatchers = DispatcherProvider(UnconfinedTestDispatcher())

    @Test
    fun `getVehicleDetail returns mapped success`() = runTest {
        val api = RecordingVehicleApi(
            detailResponse = VehicleResponse(
                data = VehicleDataDto(
                    id = "v1",
                    attributes = VehicleAttributesDto(
                        label = "Bus 1",
                        currentStatus = "STOPPED_AT",
                        latitude = 1.0,
                        longitude = 2.0,
                        updatedAt = "2024-01-01T00:00:00Z",
                    ),
                ),
            ),
        )
        val repository = VehicleRepositoryImpl(api, testDispatchers)

        val result = repository.getVehicleDetail("v1")

        val detail = (result as DomainResult.Success).data
        assertEquals("v1", detail.id)
        assertEquals("Bus 1", detail.label)
        assertEquals(1.0, detail.latitude)
        assertEquals(2.0, detail.longitude)
    }

    @Test
    fun `getVehicleDetail wraps network error`() = runTest {
        val api = object : VehicleMbtaApiService {
            override suspend fun getVehicles(
                filters: Map<String, String>,
                offset: Int,
                limit: Int,
                fields: String,
            ) = error("not used")

            override suspend fun getVehicle(id: String, include: String, fields: String) =
                throw IOException("boom")
        }
        val repository = VehicleRepositoryImpl(api, testDispatchers)

        val result = repository.getVehicleDetail("v1")

        val error = result as DomainResult.Error
        assertTrue(error.isNetworkError)
        assertEquals("Network error", error.message)
    }

    private class RecordingVehicleApi(
        private val detailResponse: VehicleResponse,
    ) : VehicleMbtaApiService {
        override suspend fun getVehicles(
            filters: Map<String, String>,
            offset: Int,
            limit: Int,
            fields: String,
        ) = error("not used")

        override suspend fun getVehicle(
            id: String,
            include: String,
            fields: String,
        ): VehicleResponse = detailResponse
    }
}
