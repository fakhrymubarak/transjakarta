package com.fakhry.transjakarta.feature.vehicles.data.remote.service

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripsResponse
import retrofit2.http.GET
import retrofit2.http.Path
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

    @GET("trips/{id}")
    suspend fun getTrip(
        @Path("id") id: String,
        @Query("fields[trip]") fields: String = "name,headsign,block_id,direction_id",
    ): TripResponse
}
