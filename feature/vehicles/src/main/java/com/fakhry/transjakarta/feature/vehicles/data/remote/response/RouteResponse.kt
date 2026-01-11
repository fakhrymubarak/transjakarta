package com.fakhry.transjakarta.feature.vehicles.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoutesResponse(
    val data: List<RouteDataDto>,
)

@Serializable
data class RouteDataDto(
    val id: String,
    val type: String? = null,
    val attributes: RouteAttributesDto? = null,
)

@Serializable
data class RouteAttributesDto(
    @SerialName("short_name")
    val shortName: String? = null,
    @SerialName("long_name")
    val longName: String? = null,
    val description: String? = null,
)
