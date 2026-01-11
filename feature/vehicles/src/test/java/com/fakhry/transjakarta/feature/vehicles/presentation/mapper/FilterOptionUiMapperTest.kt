package com.fakhry.transjakarta.feature.vehicles.presentation.mapper

import com.fakhry.transjakarta.feature.vehicles.domain.model.Route
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FilterOptionUiMapperTest {

    @Test
    fun `route maps to label preferring short name then long name then id`() {
        val route = Route(id = "id-1", shortName = "short", longName = "long")
        assertEquals("short", route.toFilterOptionUiModel().label)

        val noShort = Route(id = "id-2", shortName = "", longName = "long")
        assertEquals("long", noShort.toFilterOptionUiModel().label)

        val fallback = Route(id = "id-3", shortName = "", longName = "")
        assertEquals("id-3", fallback.toFilterOptionUiModel().label)
    }

    @Test
    fun `trip maps to label with id suffix for uniqueness`() {
        val trip = Trip(id = "trip-1", name = "Name", headsign = "Head", blockId = "block")
        assertEquals("Name • trip-1", trip.toFilterOptionUiModel().label)

        val noName = Trip(id = "trip-2", name = "", headsign = "Head", blockId = "block")
        assertEquals("Head • trip-2", noName.toFilterOptionUiModel().label)

        val fallback = Trip(id = "trip-3", name = "", headsign = "", blockId = "block")
        assertEquals("trip-3", fallback.toFilterOptionUiModel().label)
    }
}
