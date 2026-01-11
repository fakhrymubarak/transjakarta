package com.fakhry.transjakarta.feature.vehicles.data.mapper

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripDataDto
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip

fun TripDataDto.toTrip() = Trip(
    id = id,
    name = attributes?.name.orEmpty(),
    headsign = attributes?.headsign.orEmpty(),
    blockId = attributes?.blockId.orEmpty(),
)

fun List<TripDataDto>.toTrips(): List<Trip> = map { it.toTrip() }
