package com.fakhry.transjakarta.feature.vehicles.data.remote.service

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.StopResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StopMbtaApiService {
    @GET("stops/{id}")
    suspend fun getStop(
        @Path("id") id: String,
        @Query("fields[stop]")
        fields: String = "name,latitude,longitude,municipality,platform_code",
    ): StopResponse
}
