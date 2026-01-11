package com.fakhry.transjakarta.feature.vehicles.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class TripRelationshipsDto(
    val shape: ShapeRelationshipDto? = null,
)

@Serializable
data class ShapeRelationshipDto(
    val data: Data? = null,
)
