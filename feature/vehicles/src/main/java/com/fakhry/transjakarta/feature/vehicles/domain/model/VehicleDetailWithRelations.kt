package com.fakhry.transjakarta.feature.vehicles.domain.model

data class VehicleDetailWithRelations(
    val vehicle: VehicleDetail,
    val route: Route?,
    val trip: Trip?,
    val stop: Stop?,
)
