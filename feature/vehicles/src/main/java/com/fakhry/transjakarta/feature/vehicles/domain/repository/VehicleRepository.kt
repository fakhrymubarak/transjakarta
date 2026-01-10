package com.fakhry.transjakarta.feature.vehicles.domain.repository

import androidx.paging.PagingData
import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehiclesPagingFlow(): Flow<PagingData<Vehicle>>
}
