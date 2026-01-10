package com.fakhry.transjakarta.feature.vehicles.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehiclesResponse(
    val data: List<VehicleDataDto>,
)

@Serializable
data class VehicleResponse(
    val data: VehicleDataDto,
)

@Serializable
data class VehicleDataDto(
    val id: String,
    val type: String? = null,
    val attributes: VehicleAttributesDto? = null,
    val relationships: RelationshipDto ? = null,
)

@Serializable
data class VehicleAttributesDto(
    val label: String? = null,
    @SerialName("current_status")
    val currentStatus: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
)

@Serializable
data class RelationshipDto(
    val route: RouteDto? = null,
    val trip: TripDto? = null,
    val stop: StopDto? = null,
)

@Serializable
data class TripDto(
    val data: Data? = null,
)

@Serializable
data class RouteDto(
    val data: Data? = null,
)

@Serializable
data class StopDto(
    val data: Data? = null,
)

@Serializable
data class Data(
    val id: String? = null,
    val type: String? = null,
)

