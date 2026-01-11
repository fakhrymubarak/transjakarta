package com.fakhry.transjakarta.feature.vehicles.domain.model

data class VehicleDetail(
    val id: String,
    val label: String,
    val currentStatus: VehicleStatus,
    val latitude: Double,
    val longitude: Double,
    val updatedAt: String,
    val routeId: String?,
    val tripId: String?,
    val stopId: String?,
    val bearing: Int?,
    val directionId: Int?,
)
