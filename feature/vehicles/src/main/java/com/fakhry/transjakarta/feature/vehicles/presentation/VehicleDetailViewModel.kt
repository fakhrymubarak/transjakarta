package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhry.transjakarta.core.designsystem.state.UiState
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetail
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VehicleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: VehicleRepository,
) : ViewModel() {
    private val vehicleId: String = checkNotNull(savedStateHandle["vehicleId"])

    private val _uiState: MutableStateFlow<UiState<VehicleDetail>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<VehicleDetail>> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun retry() {
        loadDetail()
    }

    private fun loadDetail() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getVehicleDetail(vehicleId)) {
                is DomainResult.Success -> _uiState.value = UiState.Success(result.data)
                is DomainResult.Error -> _uiState.value =
                    UiState.Error(
                        message = result.message,
                        code = result.code,
                        data = result.data,
                        isNetworkError = result.isNetworkError,
                    )

                is DomainResult.Empty -> _uiState.value = UiState.Empty
            }
        }
    }
}
