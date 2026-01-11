package com.fakhry.transjakarta.feature.vehicles.data.remote.service

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.RoutesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RouteMbtaApiService {
    @GET("routes")
    suspend fun getRoutes(
        @Query("page[offset]") offset: Int,
        @Query("page[limit]") limit: Int,
        @Query("fields[route]") fields: String = "short_name,long_name",
    ): RoutesResponse
}
