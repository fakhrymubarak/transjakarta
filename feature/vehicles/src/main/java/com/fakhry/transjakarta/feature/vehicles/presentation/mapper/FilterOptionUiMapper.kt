package com.fakhry.transjakarta.feature.vehicles.presentation.mapper

import com.fakhry.transjakarta.feature.vehicles.domain.model.Route
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.presentation.model.FilterOptionUiModel

fun Route.toFilterOptionUiModel(): FilterOptionUiModel = FilterOptionUiModel(
    id = id,
    label = shortName.ifBlank { longName.ifBlank { id } },
)

fun Trip.toFilterOptionUiModel(): FilterOptionUiModel = FilterOptionUiModel(
    id = id,
    label = run {
        val primary = name.ifBlank { headsign.ifBlank { id } }
        if (primary == id) id else "$primary • $id"
    },
)
