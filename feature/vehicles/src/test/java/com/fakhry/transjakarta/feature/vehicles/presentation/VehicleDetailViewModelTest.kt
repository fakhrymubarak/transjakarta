package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.lifecycle.SavedStateHandle
import com.fakhry.transjakarta.core.designsystem.state.UiState
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetail
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleFilters
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads detail success`() = scope.runTest {
        val repo = FakeVehicleRepository(
            DomainResult.Success(
                VehicleDetail(
                    id = "v1",
                    label = "Bus 1",
                    currentStatus = VehicleStatus.STOPPED_AT,
                    latitude = 1.0,
                    longitude = 2.0,
                    updatedAt = "2024-01-01",
                    routeId = "route-1",
                    tripId = "trip-1",
                    stopId = "stop-1",
                ),
            ),
        )
        val vm = VehicleDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("vehicleId" to "v1")),
            repository = repo,
        )

        advanceUntilIdle()

        val state = vm.uiState.value
        val success = state as UiState.Success
        assertEquals("v1", success.data.id)
        assertEquals("route-1", success.data.routeId)
    }

    @Test
    fun `network error sets ui error with flag`() = scope.runTest {
        val repo = FakeVehicleRepository(
            DomainResult.Error(
                message = "Network error",
                isNetworkError = true,
            ),
        )
        val vm = VehicleDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("vehicleId" to "v1")),
            repository = repo,
        )

        advanceUntilIdle()

        val state = vm.uiState.value as UiState.Error
        assertTrue(state.isNetworkError)
    }

    private class FakeVehicleRepository(
        private val result: DomainResult<VehicleDetail>,
    ) : VehicleRepository {
        override fun getVehiclesPagingFlow(
            filters: VehicleFilters,
        ) = error("not used")

        override suspend fun getVehicleDetail(id: String): DomainResult<VehicleDetail> = result
    }
}
