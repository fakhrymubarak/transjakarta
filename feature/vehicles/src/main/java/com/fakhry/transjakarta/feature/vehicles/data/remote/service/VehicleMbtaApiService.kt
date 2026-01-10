package com.fakhry.transjakarta.feature.vehicles.data.remote.service

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleResponse
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehiclesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VehicleMbtaApiService {
    @GET("vehicles")
    suspend fun getVehicles(
        @Query("page[offset]") offset: Int,
        @Query("page[limit]") limit: Int,
        @Query(
            "fields[vehicle]",
        ) fields: String = "label,current_status,latitude,longitude,updated_at",
    ): VehiclesResponse

    @GET("vehicles/{id}")
    suspend fun getVehicle(@Path("id") id: String): VehicleResponse
}
