package com.fakhry.transjakarta.feature.vehicles.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fakhry.transjakarta.feature.vehicles.data.mapper.toVehicles
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.VehicleMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle

class VehiclePagingSource(
    private val api: VehicleMbtaApiService,
) : PagingSource<Int, Vehicle>() {

    override fun getRefreshKey(state: PagingState<Int, Vehicle>): Int? {
        // Return the offset closest to the anchor position
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            val prevKeySize = anchorPage?.prevKey?.plus(PAGE_SIZE)
            val nextKeySize = anchorPage?.nextKey?.minus(PAGE_SIZE)
            prevKeySize ?: nextKeySize
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Vehicle> {
        val offset = params.key ?: INITIAL_OFFSET

        return try {
            val response = api.getVehicles(
                offset = offset,
                limit = PAGE_SIZE,
            )

            val vehicles = response.data.toVehicles()

            // Determine if there's a next page
            val nextKey = if (vehicles.size < PAGE_SIZE) {
                null // No more pages
            } else {
                offset + PAGE_SIZE
            }

            val prevKey = if (offset == INITIAL_OFFSET) {
                null
            } else {
                (offset - PAGE_SIZE).coerceAtLeast(INITIAL_OFFSET)
            }

            LoadResult.Page(
                data = vehicles,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        private const val INITIAL_OFFSET = 0
    }
}
