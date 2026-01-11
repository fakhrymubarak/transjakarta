package com.fakhry.transjakarta.feature.vehicles.domain.model

data class Trip(
    val id: String,
    val name: String,
    val headsign: String,
    val blockId: String,
    val shapeId: String?,
)
