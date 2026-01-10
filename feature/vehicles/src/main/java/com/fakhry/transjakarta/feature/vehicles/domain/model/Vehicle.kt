package com.fakhry.transjakarta.feature.vehicles.domain.model

data class Vehicle(
    val id: String,
    val label: String,
    val currentStatus: VehicleStatus,
    val latitude: Double,
    val longitude: Double,
    val updatedAt: String,
)


enum class VehicleStatus {
    INCOMING_AT,
    STOPPED_AT,
    IN_TRANSIT_TO,
    UNKNOWN,
    ;

    companion object {
        fun from(raw: String) = when (raw) {
            "INCOMING_AT" -> INCOMING_AT
            "STOPPED_AT" -> STOPPED_AT
            "IN_TRANSIT_TO" -> IN_TRANSIT_TO
            else -> UNKNOWN
        }
    }
}
