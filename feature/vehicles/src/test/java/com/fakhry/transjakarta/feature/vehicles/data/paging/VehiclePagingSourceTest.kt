package com.fakhry.transjakarta.feature.vehicles.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleAttributesDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleDataDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehiclesResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.VehicleMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

class VehiclePagingSourceTest {
    private lateinit var fakeApiService: FakeVehicleMbtaApiService

    @BeforeEach
    fun setup() {
        fakeApiService = FakeVehicleMbtaApiService()
    }

    @Test
    fun `load returns first page successfully`() = runTest {
        // Given
        val vehicles = createTestVehicles(10)
        fakeApiService.setResponse(VehiclesResponse(data = vehicles))

        val pagingSource = VehiclePagingSource(fakeApiService)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false,
            ),
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(10, page.data.size)
        assertNull(page.prevKey)
        assertEquals(10, page.nextKey)
    }

    @Test
    fun `load returns last page with null nextKey when less than page size`() = runTest {
        // Given
        val vehicles = createTestVehicles(5)
        fakeApiService.setResponse(VehiclesResponse(data = vehicles))

        val pagingSource = VehiclePagingSource(fakeApiService)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false,
            ),
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(5, page.data.size)
        assertNull(page.nextKey)
    }

    @Test
    fun `load returns error on network failure`() = runTest {
        // Given
        fakeApiService.setError(IOException("Network error"))

        val pagingSource = VehiclePagingSource(fakeApiService)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false,
            ),
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assertTrue(error.throwable is IOException)
    }

    @Test
    fun `load with offset returns correct prevKey`() = runTest {
        // Given
        val vehicles = createTestVehicles(10)
        fakeApiService.setResponse(VehiclesResponse(data = vehicles))

        val pagingSource = VehiclePagingSource(fakeApiService)

        // When - loading page 2 (offset 10)
        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 10,
                loadSize = 10,
                placeholdersEnabled = false,
            ),
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(0, page.prevKey)
        assertEquals(20, page.nextKey)
    }

    @Test
    fun `vehicles are correctly mapped from DTOs`() = runTest {
        // Given
        val vehicles = listOf(
            VehicleDataDto(
                id = "v1",
                type = "vehicle",
                attributes = VehicleAttributesDto(
                    label = "Bus 123",
                    currentStatus = "IN_TRANSIT_TO",
                    latitude = 42.3601,
                    longitude = -71.0589,
                    updatedAt = "2024-01-15T14:30:00-05:00",
                ),
            ),
        )
        fakeApiService.setResponse(VehiclesResponse(data = vehicles))

        val pagingSource = VehiclePagingSource(fakeApiService)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false,
            ),
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        val vehicle = page.data.first()
        assertEquals("v1", vehicle.id)
        assertEquals("Bus 123", vehicle.label)
        assertEquals(42.3601, vehicle.latitude, 0.0001)
        assertEquals(VehicleStatus.IN_TRANSIT_TO, vehicle.currentStatus)
        assertEquals(-71.0589, vehicle.longitude, 0.0001)
    }

    @Test
    fun `getRefreshKey returns closest offset from anchor position`() {
        val pagingSource = VehiclePagingSource(fakeApiService)
        val pageSize = VehiclePagingSource.PAGE_SIZE
        val page1 =
            PagingSource.LoadResult.Page(
                data = createDomainVehicles(startId = 1, count = pageSize),
                prevKey = null,
                nextKey = pageSize,
            )
        val page2 =
            PagingSource.LoadResult.Page(
                data = createDomainVehicles(startId = 11, count = pageSize),
                prevKey = 0,
                nextKey = pageSize * 2,
            )

        val state =
            PagingState(
                pages = listOf(page1, page2),
                anchorPosition = 15,
                config = PagingConfig(pageSize = pageSize),
                leadingPlaceholderCount = 0,
            )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(pageSize, refreshKey)
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

    private fun createDomainVehicles(startId: Int, count: Int): List<Vehicle> =
        (startId until startId + count).map { i ->
            Vehicle(
                id = "v$i",
                label = "Vehicle $i",
                currentStatus = VehicleStatus.IN_TRANSIT_TO,
                latitude = 42.0 + i * 0.01,
                longitude = -71.0 - i * 0.01,
                updatedAt = "2024-01-15T14:30:00-05:00",
            )
        }
}

private class FakeVehicleMbtaApiService : VehicleMbtaApiService {
    private var response: VehiclesResponse? = null
    private var error: Throwable? = null

    fun setResponse(response: VehiclesResponse) {
        this.response = response
        this.error = null
    }

    fun setError(error: Throwable) {
        this.error = error
        this.response = null
    }

    override suspend fun getVehicles(offset: Int, limit: Int, fields: String): VehiclesResponse {
        error?.let { throw it }
        return response ?: throw IllegalStateException("No response configured")
    }

    override suspend fun getVehicle(id: String): VehicleResponse {
        // Not used in VehiclePagingSourceTest
        TODO("Not yet implemented")
    }
}
