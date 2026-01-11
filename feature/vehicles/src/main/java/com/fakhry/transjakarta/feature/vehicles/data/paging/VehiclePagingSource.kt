package com.fakhry.transjakarta.feature.vehicles.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fakhry.transjakarta.feature.vehicles.data.mapper.toVehicles
import com.fakhry.transjakarta.feature.vehicles.data.remote.query.VehicleFilterQueryBuilder
import com.fakhry.transjakarta.feature.vehicles.data.remote.service.VehicleMbtaApiService
import com.fakhry.transjakarta.feature.vehicles.domain.error.RateLimitException
import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleFilters
import retrofit2.HttpException

class VehiclePagingSource(
    private val api: VehicleMbtaApiService,
    private val filters: VehicleFilters,
) : PagingSource<Int, Vehicle>() {

    override fun getRefreshKey(state: PagingState<Int, Vehicle>): Int? {
        val pageSize = state.config.pageSize
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            val prevKeySize = anchorPage?.prevKey?.plus(pageSize)
            val nextKeySize = anchorPage?.nextKey?.minus(pageSize)
            prevKeySize ?: nextKeySize
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Vehicle> {
        val offset = params.key ?: INITIAL_OFFSET

        return try {
            val filterParams = VehicleFilterQueryBuilder.build(filters)
            val vehicles = api.getVehicles(
                filters = filterParams,
                offset = offset,
                limit = PAGE_SIZE,
            ).data.toVehicles()

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
        } catch (e: HttpException) {
            if (e.code() == HTTP_TOO_MANY_REQUESTS) {
                val resetAt = e.response()?.headers()?.get(RATE_LIMIT_RESET_HEADER)?.toLongOrNull()
                val message = buildRateLimitMessage(resetAt)
                return LoadResult.Error(RateLimitException(resetAt, message))
            }
            LoadResult.Error(e)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    private fun buildRateLimitMessage(resetAt: Long?): String {
        if (resetAt == null) {
            return "Rate limit exceeded. Please try again later."
        }

        val now = currentEpochSeconds()
        return if ((resetAt - now) <= 0) {
            "Rate limit exceeded. Please try again soon."
        } else {
            "Rate limit has been reset. Please try again."
        }
    }

    private fun currentEpochSeconds(): Long = System.currentTimeMillis() / 1_000

    companion object {
        private const val HTTP_TOO_MANY_REQUESTS = 429
        private const val RATE_LIMIT_RESET_HEADER = "x-ratelimit-reset"
        const val PAGE_SIZE = 10
        private const val INITIAL_OFFSET = 0
    }
}
