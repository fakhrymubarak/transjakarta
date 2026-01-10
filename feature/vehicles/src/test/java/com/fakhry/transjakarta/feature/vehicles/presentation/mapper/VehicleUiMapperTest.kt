package com.fakhry.transjakarta.feature.vehicles.presentation.mapper

import com.fakhry.transjakarta.feature.vehicles.domain.model.Vehicle
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Locale

class VehicleUiMapperTest {
    private lateinit var previousLocale: Locale

    @BeforeEach
    fun setup() {
        previousLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @AfterEach
    fun tearDown() {
        Locale.setDefault(previousLocale)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("statusCases")
    fun `toUiModel formats status coordinates and updatedAt`(
        status: VehicleStatus,
        expectedLabel: String,
    ) {
        val vehicle =
            Vehicle(
                id = "v1",
                label = "Bus 123",
                currentStatus = status,
                latitude = 10.123456,
                longitude = 20.654321,
                updatedAt = "2024-01-15T14:30:00-05:00",
            )

        val uiModel = vehicle.toUiModel()

        assertEquals("v1", uiModel.id)
        assertEquals("Bus 123", uiModel.label)
        assertEquals(status, uiModel.currentStatus)
        assertEquals(expectedLabel, uiModel.statusLabel)
        assertEquals("10.123456, 20.654321", uiModel.coordinatesLabel)
        assertEquals("Jan 15, 14:30:00", uiModel.updatedAtLabel)
    }

    @Test
    fun `toUiModel returns Unknown when updatedAt is blank`() {
        val vehicle =
            Vehicle(
                id = "v2",
                label = "Bus 456",
                currentStatus = VehicleStatus.UNKNOWN,
                latitude = 0.0,
                longitude = 0.0,
                updatedAt = "",
            )

        val uiModel = vehicle.toUiModel()

        assertEquals("Unknown", uiModel.updatedAtLabel)
        assertEquals("Unknown", uiModel.statusLabel)
    }

    companion object {
        @JvmStatic
        fun statusCases(): List<Arguments> = listOf(
            Arguments.of(VehicleStatus.IN_TRANSIT_TO, "In Transit"),
            Arguments.of(VehicleStatus.STOPPED_AT, "Stopped"),
            Arguments.of(VehicleStatus.INCOMING_AT, "Incoming"),
            Arguments.of(VehicleStatus.UNKNOWN, "Unknown"),
        )
    }
}
