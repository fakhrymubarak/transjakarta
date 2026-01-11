package com.fakhry.transjakarta.feature.vehicles.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripsResponse(
    val data: List<TripDataDto>,
)

@Serializable
data class TripResponse(
    val data: TripDataDto,
)

@Serializable
data class TripDataDto(
    val id: String,
    val type: String? = null,
    val attributes: TripAttributesDto? = null,
    val relationships: TripRelationshipsDto? = null,
)

@Serializable
data class TripAttributesDto(
    val name: String? = null,
    val headsign: String? = null,
    @SerialName("direction_id")
    val directionId: Int? = null,
    @SerialName("block_id")
    val blockId: String? = null,
)
