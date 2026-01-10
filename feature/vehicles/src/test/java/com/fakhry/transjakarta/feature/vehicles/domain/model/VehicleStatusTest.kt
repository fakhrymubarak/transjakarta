package com.fakhry.transjakarta.feature.vehicles.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VehicleStatusTest {
    @Test
    fun `from maps known statuses`() {
        assertEquals(VehicleStatus.IN_TRANSIT_TO, VehicleStatus.from("IN_TRANSIT_TO"))
        assertEquals(VehicleStatus.STOPPED_AT, VehicleStatus.from("STOPPED_AT"))
        assertEquals(VehicleStatus.INCOMING_AT, VehicleStatus.from("INCOMING_AT"))
    }

    @Test
    fun `from maps unknown status to UNKNOWN`() {
        assertEquals(VehicleStatus.UNKNOWN, VehicleStatus.from("SOMETHING_ELSE"))
    }
}
