package com.fakhry.transjakarta.feature.vehicles.data.mapper

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.RouteDataDto
import com.fakhry.transjakarta.feature.vehicles.domain.model.Route

fun RouteDataDto.toRoute() = Route(
    id = id,
    shortName = attributes?.shortName.orEmpty(),
    longName = attributes?.longName.orEmpty(),
)

fun List<RouteDataDto>.toRoutes(): List<Route> = map { it.toRoute() }
