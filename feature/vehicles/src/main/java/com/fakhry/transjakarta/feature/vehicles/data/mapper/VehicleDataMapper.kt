package com.fakhry.transjakarta.feature.vehicles.data.mapper

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleDataDto
import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetail
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus

fun VehicleDataDto.toVehicle() = Vehicle(
    id = id,
    label = attributes?.label ?: "Vehicle",
    currentStatus = VehicleStatus.from(attributes?.currentStatus.orEmpty()),
    latitude = attributes?.latitude ?: 0.0,
    longitude = attributes?.longitude ?: 0.0,
    updatedAt = attributes?.updatedAt.orEmpty(),
)

fun List<VehicleDataDto>.toVehicles(): List<Vehicle> = map { it.toVehicle() }

fun VehicleDataDto.toVehicleDetail(): VehicleDetail = VehicleDetail(
    id = id,
    label = attributes?.label ?: "Vehicle",
    currentStatus = VehicleStatus.from(attributes?.currentStatus.orEmpty()),
    latitude = attributes?.latitude ?: 0.0,
    longitude = attributes?.longitude ?: 0.0,
    updatedAt = attributes?.updatedAt.orEmpty(),
    routeId = relationships?.route?.data?.id,
    tripId = relationships?.trip?.data?.id,
    stopId = relationships?.stop?.data?.id,
    bearing = attributes?.bearing,
    directionId = attributes?.directionId,
)
