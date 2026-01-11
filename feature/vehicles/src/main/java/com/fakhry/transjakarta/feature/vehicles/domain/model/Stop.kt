package com.fakhry.transjakarta.feature.vehicles.domain.model

data class Stop(
    val id: String,
    val name: String,
    val latitude: Double?,
    val longitude: Double?,
    val municipality: String?,
    val platformCode: String?,
)
