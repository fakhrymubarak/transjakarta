package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.lifecycle.SavedStateHandle
import com.fakhry.transjakarta.core.designsystem.state.UiState
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.Route
import com.fakhry.transjakarta.feature.vehicles.domain.model.Stop
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetail
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleFilters
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import com.fakhry.transjakarta.feature.vehicles.domain.repository.RouteRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.StopRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.TripRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import com.fakhry.transjakarta.feature.vehicles.domain.usecase.GetVehicleDetailWithRelationsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val vehicleDetail =
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
            )
        val routeResult =
            DomainResult.Success(
                Route(
                    id = "route-1",
                    shortName = "10",
                    longName = "Route 10",
                ),
            )
        val tripResult =
            DomainResult.Success(
                Trip(
                    id = "trip-1",
                    name = "Trip 1",
                    headsign = "HS",
                    blockId = "b1",
                ),
            )
        val stopResult =
            DomainResult.Success(
                Stop(
                    id = "stop-1",
                    name = "Main Stop",
                    latitude = 0.0,
                    longitude = 0.0,
                    municipality = "City",
                    platformCode = "1",
                ),
            )

        val vehicleRepository = createVehicleRepository(DomainResult.Success(vehicleDetail))
        val useCase =
            buildUseCase(
                vehicleRepository = vehicleRepository,
                routeResult = routeResult,
                tripResult = tripResult,
                stopResult = stopResult,
            )
        val vm = VehicleDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("vehicleId" to "v1")),
            getVehicleDetailWithRelations = useCase,
            vehicleRepository = vehicleRepository,
        )

        advanceTimeBy(1000)

        val state = vm.uiState.value
        val success = state as UiState.Success
        assertEquals("v1", success.data.id)
        assertEquals("Bus 1", success.data.label)

        vm.pollJob?.cancel()
    }

    @Test
    fun `network error sets ui error with flag`() = scope.runTest {
        val vehicleRepository = createVehicleRepository(
            DomainResult.Error(
                message = "Network error",
                isNetworkError = true,
            ),
        )
        val useCase =
            buildUseCase(
                vehicleRepository = vehicleRepository,
            )
        val vm = VehicleDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("vehicleId" to "v1")),
            getVehicleDetailWithRelations = useCase,
            vehicleRepository = vehicleRepository,
        )

        advanceTimeBy(1000)

        val state = vm.uiState.value as UiState.Error
        assertTrue(state.isNetworkError)

        vm.pollJob?.cancel()
    }

    private fun createVehicleRepository(
        vehicleResult: DomainResult<VehicleDetail>,
    ): VehicleRepository = object : VehicleRepository {
        override fun getVehiclesPagingFlow(filters: VehicleFilters) = error("not used")

        override suspend fun getVehicleDetail(id: String): DomainResult<VehicleDetail> =
            vehicleResult
    }

    private fun buildUseCase(
        vehicleRepository: VehicleRepository,
        routeResult: DomainResult<Route> = DomainResult.Empty as DomainResult<Route>,
        tripResult: DomainResult<Trip> = DomainResult.Empty as DomainResult<Trip>,
        stopResult: DomainResult<Stop> = DomainResult.Empty as DomainResult<Stop>,
        ioContext: kotlin.coroutines.CoroutineContext = dispatcher,
    ): GetVehicleDetailWithRelationsUseCase = GetVehicleDetailWithRelationsUseCase(
        vehicleRepository = vehicleRepository,
        routeRepository = object : RouteRepository {
            override suspend fun getRoutes(): DomainResult<List<Route>> = DomainResult.Empty

            override suspend fun getRouteById(id: String): DomainResult<Route> = routeResult
        },
        tripRepository = object : TripRepository {
            override fun getTripsPagingFlow(filters: TripFilters) = error("not used")

            override suspend fun getTripById(id: String): DomainResult<Trip> = tripResult
        },
        stopRepository = object : StopRepository {
            override suspend fun getStop(id: String): DomainResult<Stop> = stopResult
        },
        ioContext = ioContext,
    )
}
