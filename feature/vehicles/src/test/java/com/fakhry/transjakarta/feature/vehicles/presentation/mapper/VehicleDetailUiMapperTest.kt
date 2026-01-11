package com.fakhry.transjakarta.feature.vehicles.presentation.mapper

import com.fakhry.transjakarta.feature.vehicles.domain.model.Route
import com.fakhry.transjakarta.feature.vehicles.domain.model.Stop
import com.fakhry.transjakarta.feature.vehicles.domain.model.Trip
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetail
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetailWithRelations
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Locale
import java.util.TimeZone

class VehicleDetailUiMapperTest {

    @Test
    fun `maps full detail with relations correctly`() {
        val vehicle = createVehicle("v1", "Bus 1", "route-1", "trip-1", "stop-1")
        val route = Route("route-1", "10", "Main Route", listOf("Dest A", "Dest B"))
        val trip = Trip("trip-1", "Downtown", "Morning Trip", "b1", "shape-1")
        val stop = Stop("stop-1", "Central Station", 0.0, 0.0, "City", "1")

        val detail = VehicleDetailWithRelations(vehicle, route, trip, stop, null)

        // Set locale/timezone for deterministic date formatting
        Locale.setDefault(Locale.US)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        val ui = detail.toUiModel()

        assertEquals("v1", ui.id)
        assertEquals("Bus 1", ui.label)
        assertEquals("10 - Main Route", ui.routeLabel)
        assertEquals("Morning Trip", ui.tripLabel) // Headsign preferred
        assertEquals("Central Station", ui.stopLabel)
    }

    @Test
    fun `maps missing relations to ids`() {
        val vehicle = createVehicle("v1", "Bus 1", "route-1", "trip-1", "stop-1")
        val detail = VehicleDetailWithRelations(vehicle, null, null, null, null)

        val ui = detail.toUiModel()

        assertEquals("route-1", ui.routeLabel)
        assertEquals("trip-1", ui.tripLabel)
        assertEquals("stop-1", ui.stopLabel)
    }

    @Test
    fun `maps missing relations and ids to Unknown placeholders`() {
        val vehicle = createVehicle("v1", "Bus 1", null, null, null)
        val detail = VehicleDetailWithRelations(vehicle, null, null, null, null)

        val ui = detail.toUiModel()

        assertEquals("Unknown Route", ui.routeLabel)
        assertEquals("Unknown Trip", ui.tripLabel)
        assertEquals("Unknown Stop", ui.stopLabel)
    }

    @Test
    fun `maps route with only short name`() {
        val vehicle = createVehicle("v1", "Bus 1", "route-1", null, null)
        val route = Route("route-1", "10", "", emptyList())
        val detail = VehicleDetailWithRelations(vehicle, route, null, null, null)

        val ui = detail.toUiModel()
        assertEquals("10", ui.routeLabel)
    }

    @Test
    fun `maps route with only long name`() {
        val vehicle = createVehicle("v1", "Bus 1", "route-1", null, null)
        val route = Route("route-1", "", "Long Name", emptyList())
        val detail = VehicleDetailWithRelations(vehicle, route, null, null, null)

        val ui = detail.toUiModel()
        assertEquals("Long Name", ui.routeLabel)
    }

    @Test
    fun `maps trip with name fallback`() {
        val vehicle = createVehicle("v1", "Bus 1", null, "trip-1", null)
        val trip = Trip("trip-1", "Trip Name", "", "b1", null) // Empty headsign
        val detail = VehicleDetailWithRelations(vehicle, null, trip, null, null)

        val ui = detail.toUiModel()
        assertEquals("Trip Name", ui.tripLabel)
    }

    @Test
    fun `maps trip with unscheduled fallback`() {
        val vehicle = createVehicle("v1", "Bus 1", null, "trip-1", null)
        val trip = Trip("trip-1", "", "", "b1", null) // Empty name and headsign
        val detail = VehicleDetailWithRelations(vehicle, null, trip, null, null)

        val ui = detail.toUiModel()
        assertEquals("Unscheduled", ui.tripLabel)
    }

    private fun createVehicle(
        id: String,
        label: String,
        routeId: String?,
        tripId: String?,
        stopId: String?,
    ) = VehicleDetail(
        id = id,
        label = label,
        currentStatus = VehicleStatus.STOPPED_AT,
        latitude = 0.0,
        longitude = 0.0,
        updatedAt = "2024-01-01T12:00:00Z",
        routeId = routeId,
        tripId = tripId,
        stopId = stopId,
        bearing = 0,
        directionId = 0,
    )
}
