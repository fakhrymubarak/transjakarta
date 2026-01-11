package com.fakhry.transjakarta.feature.vehicles.presentation.mapper

import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetailWithRelations
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleDetailUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleUiModel
import com.fakhry.transjakarta.utils.date.DateUtils

fun Vehicle.toUiModel(): VehicleUiModel = VehicleUiModel(
    id = id,
    label = label,
    currentStatus = currentStatus,
    statusLabel = formatStatus(currentStatus),
    latitude = latitude,
    longitude = longitude,
    coordinatesLabel = formatCoordinates(latitude, longitude),
    updatedAtLabel = DateUtils.formatUpdatedAt(updatedAt),
)

private fun formatStatus(status: VehicleStatus) = when (status) {
    VehicleStatus.INCOMING_AT -> "Incoming"
    VehicleStatus.STOPPED_AT -> "Stopped"
    VehicleStatus.IN_TRANSIT_TO -> "In Transit"
    VehicleStatus.UNKNOWN -> "Unknown"
}

private fun formatCoordinates(latitude: Double, longitude: Double): String =
    "%.6f, %.6f".format(latitude, longitude)

fun VehicleDetailWithRelations.toUiModel(): VehicleDetailUiModel {
    val vehicle = this.vehicle

    val routeDirection = this.route?.let { route ->
        val destinations = route.directionDestinations
        val directionId = vehicle.directionId ?: return@let null

        if (destinations.size >= 2) {
            if (directionId == 0) {
                "${destinations[1]} -> ${destinations[0]}"
            } else {
                "${destinations[0]} -> ${destinations[1]}"
            }
        } else {
            null
        }
    }

    val routeLabel = this.route?.let { route ->
        listOfNotNull(
            route.shortName.takeUnless { it.isBlank() },
            route.longName.takeUnless { it.isBlank() },
        ).distinct().joinToString(" - ")
    } ?: vehicle.routeId.orEmpty()

    val tripLabel = this.trip?.let { trip ->
        trip.headsign.ifBlank { trip.name.ifBlank { "Unscheduled" } }
    } ?: vehicle.tripId.orEmpty()

    val stopLabel = this.stop?.name ?: vehicle.stopId.orEmpty()

    return VehicleDetailUiModel(
        id = vehicle.id,
        label = vehicle.label,
        currentStatus = vehicle.currentStatus,
        statusLabel = formatStatus(vehicle.currentStatus),
        updatedAtLabel = DateUtils.formatUpdatedAt(vehicle.updatedAt),
        routeDirection = routeDirection.orEmpty(),
        routeLabel = routeLabel.ifBlank { "Unknown Route" },
        tripLabel = tripLabel.ifBlank { "Unknown Trip" },
        stopLabel = stopLabel.ifBlank { "Unknown Stop" },
        latitude = vehicle.latitude,
        longitude = vehicle.longitude,
        coordinatesLabel = formatCoordinates(vehicle.latitude, vehicle.longitude),
        bearing = vehicle.bearing?.toFloat() ?: 0f,
        encodedPolyline = this.shape?.polyline.orEmpty(),
    )
}
