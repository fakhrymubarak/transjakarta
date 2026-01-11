package com.fakhry.transjakarta.feature.vehicles.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.core.networking.util.mapNetworkCall
import com.fakhry.transjakarta.feature.vehicles.data.mapper.toVehicleDetail
import com.fakhry.transjakarta.feature.vehicles.data.paging.VehiclePagingSource
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.VehicleMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetail
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleFilters
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val api: VehicleMbtaApiService,
) : VehicleRepository {
    override fun getVehiclesPagingFlow(filters: VehicleFilters): Flow<PagingData<Vehicle>> = Pager(
        config = PagingConfig(
            pageSize = VehiclePagingSource.PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = VehiclePagingSource.PAGE_SIZE / 2,
        ),
        pagingSourceFactory = { VehiclePagingSource(api, filters) },
    ).flow

    override suspend fun getVehicleDetail(id: String): DomainResult<VehicleDetail> =
        mapNetworkCall { api.getVehicle(id).data.toVehicleDetail() }
}
