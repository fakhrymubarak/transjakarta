package com.fakhry.transjakarta.feature.vehicles.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.fakhry.transjakarta.core.domain.DomainResult
import com.fakhry.transjakarta.core.networking.util.mapNetworkCall
import com.fakhry.transjakarta.feature.vehicles.data.mapper.toTrip
import com.fakhry.transjakarta.feature.vehicles.data.paging.TripPagingSource
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.TripMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters
import com.fakhry.transjakarta.feature.vehicles.domain.repository.TripRepository
import com.fakhry.transjakarta.utils.coroutines.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val api: TripMbtaApiService,
    private val dispatchers: DispatcherProvider,
) : TripRepository {
    override fun getTripsPagingFlow(filters: TripFilters): Flow<PagingData<Trip>> {
        if (filters.isEmpty) {
            return flowOf(PagingData.empty())
        }
        return Pager(
            config = PagingConfig(
                pageSize = TripPagingSource.PAGE_SIZE,
                initialLoadSize = TripPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { TripPagingSource(api, filters) },
        ).flow
    }

    override suspend fun getTripById(id: String): DomainResult<Trip> = withContext(dispatchers.io) {
        mapNetworkCall { api.getTrip(id).data.toTrip() }
    }
}
