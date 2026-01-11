package com.fakhry.transjakarta.feature.vehicles.domain.repository

import androidx.paging.PagingData
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getTripsPagingFlow(filters: TripFilters): Flow<PagingData<Trip>>
}
