package com.fakhry.transjakarta.feature.vehicles.presentation.model

import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus

data class VehicleUiModel(
    val id: String,
    val label: String,
    val currentStatus: VehicleStatus,
    val statusLabel: String,
    val latitude: Double,
    val longitude: Double,
    val coordinatesLabel: String,
    val updatedAtLabel: String,
)
