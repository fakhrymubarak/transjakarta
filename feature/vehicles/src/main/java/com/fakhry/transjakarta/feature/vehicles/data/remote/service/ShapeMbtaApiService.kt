package com.fakhry.transjakarta.feature.vehicles.data.remote.service

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.ShapeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ShapeMbtaApiService {
    @GET("shapes/{id}")
    suspend fun getShape(@Path("id") id: String): ShapeResponse
}
