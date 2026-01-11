package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fakhry.transjakarta.core.designsystem.state.UiState
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.usecase.GetVehicleDetailWithRelationsUseCase
import com.fakhry.transjakarta.feature.vehicles.presentation.mapper.toUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        loadDetail()
    }

    fun retry() {
        loadDetail()
    }

    private fun loadDetail() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = getVehicleDetailWithRelations(vehicleId)) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(result.data.toUiModel())
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
}
