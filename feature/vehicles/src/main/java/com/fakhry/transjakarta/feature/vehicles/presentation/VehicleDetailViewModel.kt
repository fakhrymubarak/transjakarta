package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhry.transjakarta.core.designsystem.state.UiState
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetailWithRelations
import com.fakhry.transjakarta.feature.vehicles.domain.usecase.GetVehicleDetailWithRelationsUseCase
import com.fakhry.transjakarta.feature.vehicles.presentation.mapper.toUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getVehicleDetailWithRelations: GetVehicleDetailWithRelationsUseCase,
) : ViewModel() {
    private val vehicleId: String = checkNotNull(savedStateHandle["vehicleId"])

    private val _uiState: MutableStateFlow<UiState<VehicleDetailUiModel>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<VehicleDetailUiModel>> = _uiState.asStateFlow()

    @VisibleForTesting
    internal var pollJob: Job? = null
    private var currentDetail: VehicleDetailWithRelations? = null

    init {
        loadDetail()
    }

    fun retry() {
        loadDetail()
    }

    private fun loadDetail() {
        pollJob?.cancel()
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = getVehicleDetailWithRelations(vehicleId)) {
                is DomainResult.Success -> {
                    currentDetail = result.data
                    _uiState.value = UiState.Success(result.data.toUiModel())
                    startPolling()
                }
                is DomainResult.Error -> {
                    _uiState.value =
                        UiState.Error(
                            message = result.message,
                            code = result.code,
                            data = result.data?.toUiModel(),
                            isNetworkError = result.isNetworkError,
                        )
                }

                is DomainResult.Empty -> _uiState.value = UiState.Empty
            }
        }
    }

    private fun startPolling() {
        pollJob = viewModelScope.launch {
            while (isActive) {
                delay(POLLING_INTERVAL_MS)
                when (val result = getVehicleDetailWithRelations(vehicleId)) {
                    is DomainResult.Success -> {
                        currentDetail = result.data
                        _uiState.value = UiState.Success(result.data.toUiModel())
                    }
                    else -> Unit // Ignore errors during polling.
                }
            }
        }
    }

    private companion object {
        const val POLLING_INTERVAL_MS = 5_000L
    }
}
