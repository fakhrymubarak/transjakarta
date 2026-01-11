package com.fakhry.transjakarta.feature.vehicles.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ShapeResponse(
    val data: ShapeDataDto,
)

@Serializable
data class ShapeDataDto(
    val id: String,
    val type: String? = null,
    val attributes: ShapeAttributesDto? = null,
)

@Serializable
data class ShapeAttributesDto(
    val polyline: String? = null,
)
