package com.fakhry.transjakarta.feature.vehicles.presentation.model

import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus

data class VehicleDetailUiModel(
    val id: String,
    val label: String,
    val currentStatus: VehicleStatus,
    val statusLabel: String,
    val updatedAtLabel: String,
    val routeDirection: String,
    val routeLabel: String,
    val tripLabel: String,
    val stopLabel: String,
    val latitude: Double,
    val longitude: Double,
    val coordinatesLabel: String,
    val bearing: Float,
    val encodedPolyline: String,
)
