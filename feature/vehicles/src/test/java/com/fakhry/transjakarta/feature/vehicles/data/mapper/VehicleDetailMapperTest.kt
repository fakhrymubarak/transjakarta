package com.fakhry.transjakarta.feature.vehicles.data.mapper

import com.fakhry.transjakarta.feature.vehicles.data.remote.response.Data
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.RelationshipDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleAttributesDto
import com.fakhry.transjakarta.feature.vehicles.data.remote.response.VehicleDataDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VehicleDetailMapperTest {

    @Test
    fun `maps vehicle dto to detail with relationships`() {
        val dto = VehicleDataDto(
            id = "v1",
            attributes = VehicleAttributesDto(
                label = "Bus 1",
                currentStatus = "IN_TRANSIT_TO",
                latitude = -6.2,
                longitude = 106.8,
                updatedAt = "2024-01-01T12:00:00Z",
            ),
            relationships = RelationshipDto(
                route = com.fakhry.transjakarta.feature.vehicles.data.remote.response.RouteDto(
                    data = Data(id = "route-1"),
                ),
                trip = com.fakhry.transjakarta.feature.vehicles.data.remote.response.TripDto(
                    data = Data(id = "trip-1"),
                ),
                stop = com.fakhry.transjakarta.feature.vehicles.data.remote.response.StopDto(
                    data = Data(id = "stop-1"),
                ),
            ),
        )

        val detail = dto.toVehicleDetail()

        assertEquals("v1", detail.id)
        assertEquals("Bus 1", detail.label)
        assertEquals(-6.2, detail.latitude)
        assertEquals(106.8, detail.longitude)
        assertEquals("route-1", detail.routeId)
        assertEquals("trip-1", detail.tripId)
        assertEquals("stop-1", detail.stopId)
        assertEquals("IN_TRANSIT_TO", detail.currentStatus.name)
    }
}
