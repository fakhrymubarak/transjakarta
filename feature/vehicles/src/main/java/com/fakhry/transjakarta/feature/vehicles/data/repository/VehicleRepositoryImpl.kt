package com.fakhry.transjakarta.feature.vehicles.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.fakhry.transjakarta.feature.vehicles.data.paging.VehiclePagingSource
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.VehicleMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val api: VehicleMbtaApiService,
) : VehicleRepository {
    override fun getVehiclesPagingFlow(): Flow<PagingData<Vehicle>> = Pager(
        config = PagingConfig(
            pageSize = VehiclePagingSource.PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = VehiclePagingSource.PAGE_SIZE / 2,
        ),
        pagingSourceFactory = { VehiclePagingSource(api) },
    ).flow
}
