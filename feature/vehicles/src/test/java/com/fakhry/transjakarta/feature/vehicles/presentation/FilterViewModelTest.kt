package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.paging.PagingData
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.Route
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters
import com.fakhry.transjakarta.feature.vehicles.domain.repository.RouteRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.TripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FilterViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var routeRepository: FakeRouteRepository
    private lateinit var tripRepository: FakeTripRepository

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        routeRepository = FakeRouteRepository(
            result = DomainResult.Success(
                listOf(
                    Route(id = "route-1", shortName = "1", longName = "Route One"),
                    Route(id = "route-2", shortName = "2", longName = "Route Two"),
                ),
            ),
        )
        tripRepository = FakeTripRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads routes into ui state`() = runTest(testDispatcher) {
        val viewModel = FilterViewModel(routeRepository, tripRepository)

        advanceUntilIdle()

        val routes = viewModel.uiState.value.routes
        assertEquals(2, routes.size)
        assertEquals("route-1", routes.first().id)
    }

    @Test
    fun `toggleRouteSelection updates selected routes`() = runTest(testDispatcher) {
        val viewModel = FilterViewModel(routeRepository, tripRepository)

        viewModel.toggleRouteSelection("route-1")

        assertEquals(setOf("route-1"), viewModel.uiState.value.selectedRouteIds)

        viewModel.toggleRouteSelection("route-1")

        assertTrue(viewModel.uiState.value.selectedRouteIds.isEmpty())
    }

    @Test
    fun `toggleTripSelection updates selected trips`() = runTest(testDispatcher) {
        val viewModel = FilterViewModel(routeRepository, tripRepository)

        viewModel.toggleTripSelection("trip-1")

        assertEquals(setOf("trip-1"), viewModel.uiState.value.selectedTripIds)

        viewModel.toggleTripSelection("trip-1")

        assertTrue(viewModel.uiState.value.selectedTripIds.isEmpty())
    }

    @Test
    fun `route selection clears trip selections`() = runTest(testDispatcher) {
        val viewModel = FilterViewModel(routeRepository, tripRepository)

        viewModel.toggleTripSelection("trip-1")
        viewModel.toggleRouteSelection("route-1")

        assertTrue(viewModel.uiState.value.selectedTripIds.isEmpty())
    }

    @Test
    fun `applyFilters emits applied filters`() = runTest(testDispatcher) {
        val viewModel = FilterViewModel(routeRepository, tripRepository)

        viewModel.toggleRouteSelection("route-1")
        viewModel.toggleTripSelection("trip-2")
        viewModel.applyFilters()

        val filters = viewModel.appliedFilters.value
        assertEquals(setOf("route-1"), filters.routeIds)
        assertEquals(setOf("trip-2"), filters.tripIds)
    }

    @Test
    fun `clearFilters resets selections and applied filters`() = runTest(testDispatcher) {
        val viewModel = FilterViewModel(routeRepository, tripRepository)

        viewModel.toggleRouteSelection("route-1")
        viewModel.toggleTripSelection("trip-2")
        viewModel.applyFilters()

        viewModel.clearFilters()

        assertTrue(viewModel.uiState.value.selectedRouteIds.isEmpty())
        assertTrue(viewModel.uiState.value.selectedTripIds.isEmpty())
        assertTrue(viewModel.appliedFilters.value.isEmpty)
    }

    @Test
    fun `route errors update ui state`() = runTest(testDispatcher) {
        routeRepository = FakeRouteRepository(result = DomainResult.Error("Boom"))
        val viewModel = FilterViewModel(routeRepository, tripRepository)

        advanceUntilIdle()

        assertEquals("Boom", viewModel.uiState.value.routesError)
    }

    @Test
    fun `clearRoutes resets only route selections`() = runTest(testDispatcher) {
        val viewModel = FilterViewModel(routeRepository, tripRepository)

        viewModel.toggleRouteSelection("route-1")
        viewModel.toggleTripSelection("trip-1")
        viewModel.clearRoutes()

        assertTrue(viewModel.uiState.value.selectedRouteIds.isEmpty())
        assertEquals(setOf("trip-1"), viewModel.uiState.value.selectedTripIds)
    }

    @Test
    fun `clearTrips resets only trip selections`() = runTest(testDispatcher) {
        val viewModel = FilterViewModel(routeRepository, tripRepository)

        viewModel.toggleRouteSelection("route-1")
        viewModel.toggleTripSelection("trip-1")
        viewModel.clearTrips()

        assertEquals(setOf("route-1"), viewModel.uiState.value.selectedRouteIds)
        assertTrue(viewModel.uiState.value.selectedTripIds.isEmpty())
    }

    @Test
    fun `trips paging uses selected routes and search query`() = runTest(testDispatcher) {
        val recordingTripRepository = RecordingTripRepository()
        val viewModel = FilterViewModel(routeRepository, recordingTripRepository)

        viewModel.toggleRouteSelection("route-1")
        viewModel.updateTripSearchQuery("alpha")
        advanceTimeBy(400)

        viewModel.tripsPagingFlow.first()

        val lastFilters = recordingTripRepository.calls.last()
        assertEquals(setOf("route-1"), lastFilters.routeIds)
        assertEquals("alpha", lastFilters.nameQuery)
    }
}

private class FakeRouteRepository(
    private val result: DomainResult<List<Route>>,
) : RouteRepository {
    override suspend fun getRoutes(): DomainResult<List<Route>> = result
    override suspend fun getRouteById(id: String): DomainResult<Route> = DomainResult.Empty
}

private class FakeTripRepository : TripRepository {
    override fun getTripsPagingFlow(filters: TripFilters): Flow<PagingData<Trip>> =
        flowOf(PagingData.from(emptyList()))

    override suspend fun getTripById(id: String): DomainResult<Trip> = DomainResult.Empty
}

private class RecordingTripRepository : TripRepository {
    val calls = mutableListOf<TripFilters>()

    override fun getTripsPagingFlow(filters: TripFilters): Flow<PagingData<Trip>> {
        calls += filters
        return flowOf(PagingData.from(emptyList()))
    }

    override suspend fun getTripById(id: String): DomainResult<Trip> = DomainResult.Empty
}
