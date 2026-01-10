package com.fakhry.transjakarta.feature.vehicles.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fakhry.transjakarta.core.designsystem.theme.TransjakartaTheme
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleUiModel

@Composable
fun VehicleCard(vehicle: VehicleUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Header: Label and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Vehicle ${vehicle.label}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusChip(
                    status = vehicle.currentStatus,
                    label = vehicle.statusLabel,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "📍",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = vehicle.coordinatesLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Updated time
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "🕐",
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = vehicle.updatedAtLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: VehicleStatus, label: String, modifier: Modifier = Modifier) {
    val (backgroundColor, contentColor) =
        when (status) {
            VehicleStatus.STOPPED_AT ->
                MaterialTheme.colorScheme.primaryContainer to
                    MaterialTheme.colorScheme.onPrimaryContainer
            VehicleStatus.IN_TRANSIT_TO ->
                MaterialTheme.colorScheme.tertiaryContainer to
                    MaterialTheme.colorScheme.onTertiaryContainer
            VehicleStatus.INCOMING_AT ->
                MaterialTheme.colorScheme.secondaryContainer to
                    MaterialTheme.colorScheme.onSecondaryContainer
            else ->
                MaterialTheme.colorScheme.surfaceVariant to
                    MaterialTheme.colorScheme.onSurfaceVariant
        }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VehicleCardPreview() {
    MaterialTheme {
        VehicleCard(
            vehicle = VehicleUiModel(
                id = "1",
                label = "1817",
                currentStatus = VehicleStatus.IN_TRANSIT_TO,
                statusLabel = "In Transit To",
                latitude = 42.32941818237305,
                longitude = -71.27239990234375,
                coordinatesLabel = "42.329418, -71.272400",
                updatedAtLabel = "Jan 15, 14:30:00",
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VehicleCardStoppedPreview() {
    TransjakartaTheme {
        VehicleCard(
            vehicle = VehicleUiModel(
                id = "2",
                label = "1234",
                currentStatus = VehicleStatus.STOPPED_AT,
                statusLabel = "Stopped At",
                latitude = 42.3601,
                longitude = -71.0589,
                coordinatesLabel = "42.360100, -71.058900",
                updatedAtLabel = "Jan 15, 14:32:15",
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
