package com.fakhry.transjakarta.feature.vehicles.presentation.mapper

import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetailWithRelations
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleDetailUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleUiModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun Vehicle.toUiModel(): VehicleUiModel = VehicleUiModel(
    id = id,
    label = label,
    currentStatus = currentStatus,
    statusLabel = formatStatus(currentStatus),
    latitude = latitude,
    longitude = longitude,
    coordinatesLabel = formatCoordinates(latitude, longitude),
    updatedAtLabel = formatUpdatedAt(updatedAt),
)

private fun formatStatus(status: VehicleStatus) = when (status) {
    VehicleStatus.INCOMING_AT -> "Incoming"
    VehicleStatus.STOPPED_AT -> "Stopped"
    VehicleStatus.IN_TRANSIT_TO -> "In Transit"
    VehicleStatus.UNKNOWN -> "Unknown"
}

private fun formatCoordinates(latitude: Double, longitude: Double): String =
    "%.6f, %.6f".format(latitude, longitude)

private fun formatUpdatedAt(updatedAt: String): String {
    if (updatedAt.isBlank()) return "Unknown"

    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ssX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
    )

    for (pattern in patterns) {
        try {
            val parser = SimpleDateFormat(pattern, Locale.getDefault())
            val date = parser.parse(updatedAt) ?: continue
            val formatter = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
            formatter.timeZone = extractTimeZone(updatedAt) ?: TimeZone.getDefault()
            return formatter.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            continue
        }
    }

    return updatedAt
}

private fun extractTimeZone(updatedAt: String): TimeZone? {
    val match = timeZoneRegex.find(updatedAt) ?: return null
    val value = match.value
    return if (value == "Z") {
        TimeZone.getTimeZone("UTC")
    } else {
        TimeZone.getTimeZone("GMT$value")
    }
}

private val timeZoneRegex = Regex("([+-]\\d{2}:\\d{2}|Z)$")

fun VehicleDetailWithRelations.toUiModel(): VehicleDetailUiModel {
    val vehicle = this.vehicle

    val routeLabel = this.route?.let { route ->
        listOfNotNull(
            route.shortName.takeUnless { it.isBlank() },
            route.longName.takeUnless { it.isBlank() },
        ).distinct().joinToString(" - ")
    } ?: vehicle.routeId.orEmpty()

    val tripLabel = this.trip?.let { trip ->
        trip.headsign.ifBlank { trip.name.ifBlank { "Unscheduled" } }
    } ?: vehicle.tripId.orEmpty()

    val stopLabel = this.stop?.let { stop ->
        stop.name
    } ?: vehicle.stopId.orEmpty()

    return VehicleDetailUiModel(
        id = vehicle.id,
        label = vehicle.label,
        currentStatus = vehicle.currentStatus,
        statusLabel = formatStatus(vehicle.currentStatus),
        updatedAtLabel = formatUpdatedAt(vehicle.updatedAt),
        routeLabel = routeLabel.ifBlank { "Unknown Route" },
        tripLabel = tripLabel.ifBlank { "Unknown Trip" },
        stopLabel = stopLabel.ifBlank { "Unknown Stop" },
        latitude = vehicle.latitude,
        longitude = vehicle.longitude,
        coordinatesLabel = formatCoordinates(vehicle.latitude, vehicle.longitude),
    )
}
