package com.fakhry.transjakarta.feature.vehicles.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StopResponse(
    val data: StopDataDto,
)

@Serializable
data class StopDataDto(
    val id: String,
    val type: String? = null,
    val attributes: StopAttributesDto? = null,
)

@Serializable
data class StopAttributesDto(
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val municipality: String? = null,
    @SerialName("platform_code")
    val platformCode: String? = null,
)
