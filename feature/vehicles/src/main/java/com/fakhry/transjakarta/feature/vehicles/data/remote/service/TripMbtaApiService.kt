package com.fakhry.transjakarta.feature.vehicles.data.remote.service

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripsResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface TripMbtaApiService {
    @GET("trips")
    suspend fun getTrips(
        @QueryMap filters: Map<String, String> = emptyMap(),
        @Query("page[offset]") offset: Int,
        @Query("page[limit]") limit: Int,
        @Query("fields[trip]") fields: String = "name,headsign,block_id",
    ): TripsResponse
}
