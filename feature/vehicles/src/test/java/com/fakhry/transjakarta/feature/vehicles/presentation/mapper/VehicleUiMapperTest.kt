package com.fakhry.transjakarta.feature.vehicles.presentation.mapper

import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Locale
import java.util.TimeZone

class VehicleUiMapperTest {
    private val defaultLocale = Locale.getDefault()
    private val defaultTimeZone = TimeZone.getDefault()

    @AfterEach
    fun tearDown() {
        Locale.setDefault(defaultLocale)
        TimeZone.setDefault(defaultTimeZone)
    }

    @Test
    fun `maps vehicle to ui model with status and coordinates`() {
        val vehicle = Vehicle(
            id = "v1",
            label = "Bus 1",
            currentStatus = VehicleStatus.IN_TRANSIT_TO,
            latitude = -6.2001,
            longitude = 106.8167,
            updatedAt = "2024-01-01T12:34:56Z",
        )
        Locale.setDefault(Locale.US)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        val ui = vehicle.toUiModel()

        assertEquals("v1", ui.id)
        assertEquals("Bus 1", ui.label)
        assertEquals(VehicleStatus.IN_TRANSIT_TO, ui.currentStatus)
        assertEquals("In Transit", ui.statusLabel)
        assertEquals("-6.200100, 106.816700", ui.coordinatesLabel)
        assertEquals("Jan 01, 12:34:56", ui.updatedAtLabel)
    }

    @Test
    fun `formats updated at with offset timezone`() {
        val vehicle = Vehicle(
            id = "v1",
            label = "Bus 1",
            currentStatus = VehicleStatus.STOPPED_AT,
            latitude = 0.0,
            longitude = 0.0,
            updatedAt = "2024-01-01T15:45:30+07:00",
        )
        Locale.setDefault(Locale.US)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        val ui = vehicle.toUiModel()

        assertEquals("Jan 01, 15:45:30", ui.updatedAtLabel)
    }

    @Test
    fun `blank updated at returns Unknown label`() {
        val vehicle = Vehicle(
            id = "v1",
            label = "Bus 1",
            currentStatus = VehicleStatus.UNKNOWN,
            latitude = 0.0,
            longitude = 0.0,
            updatedAt = "",
        )

        val ui = vehicle.toUiModel()

        assertEquals("Unknown", ui.updatedAtLabel)
    }

    @Test
    fun `unparseable updated at falls back to raw string`() {
        val raw = "not-a-date"
        val vehicle = Vehicle(
            id = "v1",
            label = "Bus 1",
            currentStatus = VehicleStatus.UNKNOWN,
            latitude = 0.0,
            longitude = 0.0,
            updatedAt = raw,
        )

        val ui = vehicle.toUiModel()

        assertEquals(raw, ui.updatedAtLabel)
    }
}
