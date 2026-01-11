package com.fakhry.transjakarta.feature.vehicles.data.mapper

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.StopDataDto
import com.fakhry.transjakarta.feature.vehicles.domain.model.Stop

fun StopDataDto.toStop(): Stop = Stop(
    id = id,
    name = attributes?.name.orEmpty(),
    latitude = attributes?.latitude,
    longitude = attributes?.longitude,
    municipality = attributes?.municipality,
    platformCode = attributes?.platformCode,
)
