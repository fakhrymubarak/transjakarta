package com.fakhry.transjakarta.feature.vehicles.domain.model

data class Route(
    val id: String,
    val shortName: String,
    val longName: String,
    val directionDestinations: List<String>,
)
