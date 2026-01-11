package com.fakhry.transjakarta.feature.vehicles.data.mapper

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.ShapeDataDto
import com.fakhry.transjakarta.feature.vehicles.domain.model.Shape

fun ShapeDataDto.toShape() = Shape(
    id = id,
    polyline = attributes?.polyline.orEmpty(),
)
