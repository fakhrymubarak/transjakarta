package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import com.fakhry.transjakarta.feature.vehicles.presentation.mapper.toUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    repository: VehicleRepository,
) : ViewModel() {
    val vehiclesPagingFlow: Flow<PagingData<VehicleUiModel>> =
        repository
            .getVehiclesPagingFlow()
            .map { pagingData -> pagingData.map { it.toUiModel() } }
            .cachedIn(viewModelScope)
}
