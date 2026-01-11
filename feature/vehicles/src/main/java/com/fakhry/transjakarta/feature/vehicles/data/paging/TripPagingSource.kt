package com.fakhry.transjakarta.feature.vehicles.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fakhry.transjakarta.feature.vehicles.data.mapper.toTrips
import com.fakhry.transjakarta.feature.vehicles.data.remote.query.TripFilterQueryBuilder
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.TripMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.domain.model.TripFilters

class TripPagingSource(
    private val api: TripMbtaApiService,
    private val filters: TripFilters,
) : PagingSource<Int, Trip>() {

    override fun getRefreshKey(state: PagingState<Int, Trip>): Int? {
        val pageSize = state.config.pageSize
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            val prevKeySize = anchorPage?.prevKey?.plus(pageSize)
            val nextKeySize = anchorPage?.nextKey?.minus(pageSize)
            prevKeySize ?: nextKeySize
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Trip> {
        val offset = params.key ?: INITIAL_OFFSET

        return try {
            val filterParams = TripFilterQueryBuilder.build(filters)
            val trips = api.getTrips(
                filters = filterParams,
                offset = offset,
                limit = PAGE_SIZE,
            ).data.toTrips()

            val nextKey = if (trips.size < PAGE_SIZE) {
                null
            } else {
                offset + PAGE_SIZE
            }

            val prevKey = if (offset == INITIAL_OFFSET) {
                null
            } else {
                (offset - PAGE_SIZE).coerceAtLeast(INITIAL_OFFSET)
            }

            LoadResult.Page(
                data = trips,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
        private const val INITIAL_OFFSET = 0
    }
}
