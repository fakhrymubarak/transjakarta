package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleFilters
import com.fakhry.transjakarta.feature.vehicles.domain.repository.RouteRepository
import com.fakhry.transjakarta.feature.vehicles.domain.repository.TripRepository
import com.fakhry.transjakarta.feature.vehicles.presentation.mapper.toFilterOptionUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.FilterOptionUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleFilterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class FilterViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val tripRepository: TripRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleFilterUiState(isRoutesLoading = true))
    val uiState: StateFlow<VehicleFilterUiState> = _uiState.asStateFlow()

    private val _appliedFilters = MutableStateFlow(VehicleFilters())
    val appliedFilters: StateFlow<VehicleFilters> = _appliedFilters.asStateFlow()

    private val tripFiltersFlow = combine(
        uiState.map { it.selectedRouteIds }.distinctUntilChanged(),
        uiState.map { it.tripSearchQuery }.distinctUntilChanged().debounce(SEARCH_DEBOUNCE_MS),
    ) { routeIds, query ->
        TripFilters(routeIds = routeIds, nameQuery = query)
    }.distinctUntilChanged()

    val tripsPagingFlow: Flow<PagingData<FilterOptionUiModel>> =
        tripFiltersFlow
            .flatMapLatest { filters ->
                tripRepository.getTripsPagingFlow(filters)
            }.map { pagingData ->
                pagingData.map { trip -> trip.toFilterOptionUiModel() }
            }.cachedIn(viewModelScope)

    init {
        loadRoutes()
    }

    fun retryRoutes() {
        loadRoutes()
    }

    fun toggleRouteSelection(routeId: String) {
        _uiState.update { state ->
            val selected = state.selectedRouteIds.toMutableSet()
            val wasSelected = selected.remove(routeId)
            if (!wasSelected) {
                selected.add(routeId)
            }
            state.copy(
                selectedRouteIds = selected,
                selectedTripIds = emptySet(),
            )
        }
    }

    fun toggleTripSelection(tripId: String) {
        _uiState.update { state ->
            val selected = state.selectedTripIds.toMutableSet()
            if (selected.contains(tripId)) {
                selected.remove(tripId)
            } else {
                selected.add(tripId)
            }
            state.copy(selectedTripIds = selected)
        }
    }

    fun updateRouteSearchQuery(query: String) {
        _uiState.update { it.copy(routeSearchQuery = query) }
    }

    fun updateTripSearchQuery(query: String) {
        _uiState.update { it.copy(tripSearchQuery = query) }
    }

    fun applyFilters() {
        _appliedFilters.value = VehicleFilters(
            routeIds = uiState.value.selectedRouteIds,
            tripIds = uiState.value.selectedTripIds,
        )
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                selectedRouteIds = emptySet(),
                selectedTripIds = emptySet(),
            )
        }
        _appliedFilters.value = VehicleFilters()
    }

    fun clearRoutes() {
        _uiState.update { it.copy(selectedRouteIds = emptySet()) }
    }

    fun clearTrips() {
        _uiState.update { it.copy(selectedTripIds = emptySet()) }
    }

    private fun loadRoutes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRoutesLoading = true, routesError = null) }
            try {
                when (val result = routeRepository.getRoutes()) {
                    is DomainResult.Success -> {
                        val routes = result.data.map { it.toFilterOptionUiModel() }
                        _uiState.update { state ->
                            state.copy(
                                routes = routes,
                                isRoutesLoading = false,
                                routesError = null,
                            )
                        }
                    }
                    is DomainResult.Empty -> {
                        _uiState.update { state ->
                            state.copy(
                                routes = emptyList(),
                                isRoutesLoading = false,
                                routesError = null,
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        val message = result.message.ifBlank { "Failed to load routes" }
                        val friendly = if (result.isNetworkError) {
                            "Network error. Check your connection and try again."
                        } else {
                            message
                        }
                        _uiState.update { state ->
                            state.copy(
                                isRoutesLoading = false,
                                routesError = friendly,
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                val friendly = if (e is java.io.IOException) {
                    "Network error. Check your connection and try again."
                } else {
                    e.message ?: "Failed to load routes"
                }
                _uiState.update { state ->
                    state.copy(
                        isRoutesLoading = false,
                        routesError = friendly,
                    )
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }
}
