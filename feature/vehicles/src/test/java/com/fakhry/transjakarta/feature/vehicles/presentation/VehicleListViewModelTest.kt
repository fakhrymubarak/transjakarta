package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleListViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: VehicleListViewModel
    private lateinit var repository: VehicleRepository
    private lateinit var previousLocale: Locale

    @BeforeEach
    fun setup() {
        previousLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
        Dispatchers.setMain(testDispatcher)
        repository = FakeVehicleRepository(createVehicles())
        viewModel = VehicleListViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        Locale.setDefault(previousLocale)
    }

    @Test
    fun `vehiclesPagingFlow maps vehicles into ui models`() = runTest(testDispatcher) {
        val snapshotDeferred = async { viewModel.vehiclesPagingFlow.asSnapshot() }

        testScheduler.advanceUntilIdle()

        val snapshot = snapshotDeferred.await()

        assertEquals(1, snapshot.size)
        val item = snapshot.first()
        assertEquals("v1", item.id)
        assertEquals("Bus 123", item.label)
        assertEquals(VehicleStatus.IN_TRANSIT_TO, item.currentStatus)
        assertEquals("In Transit", item.statusLabel)
        assertEquals("10.123456, 20.654321", item.coordinatesLabel)
        assertEquals("Jan 15, 14:30:00", item.updatedAtLabel)
    }
}

private class FakeVehicleRepository(
    private val vehicles: List<Vehicle>,
) : VehicleRepository {
    override fun getVehiclesPagingFlow(): Flow<PagingData<Vehicle>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = vehicles.size,
                    initialLoadSize = vehicles.size,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = { FakePagingSource(vehicles) },
        ).flow
}

private class FakePagingSource(
    private val vehicles: List<Vehicle>,
) : PagingSource<Int, Vehicle>() {
    override fun getRefreshKey(state: PagingState<Int, Vehicle>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Vehicle> =
        LoadResult.Page(
            data = vehicles,
            prevKey = null,
            nextKey = null,
        )
}

private fun createVehicles(): List<Vehicle> =
    listOf(
        Vehicle(
            id = "v1",
            label = "Bus 123",
            currentStatus = VehicleStatus.IN_TRANSIT_TO,
            latitude = 10.123456,
            longitude = 20.654321,
            updatedAt = "2024-01-15T14:30:00-05:00",
        ),
    )
