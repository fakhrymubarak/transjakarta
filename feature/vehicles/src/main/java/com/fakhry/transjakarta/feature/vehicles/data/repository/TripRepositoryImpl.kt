package com.fakhry.transjakarta.feature.vehicles.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.fakhry.transjakarta.feature.vehicles.data.paging.TripPagingSource
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.TripMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters
import com.fakhry.transjakarta.feature.vehicles.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val api: TripMbtaApiService,
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
}
