package com.fakhry.transjakarta.feature.vehicles.domain.repository

import androidx.paging.PagingData
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetail
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleFilters
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehiclesPagingFlow(
        filters: VehicleFilters = VehicleFilters(),
    ): Flow<PagingData<Vehicle>>

    suspend fun getVehicleDetail(id: String): DomainResult<VehicleDetail>
}
