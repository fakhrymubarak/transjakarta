package com.fakhry.transjakarta.feature.vehicles.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fakhry.transjakarta.core.designsystem.theme.TransjakartaTheme
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleStatus
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleUiModel

@Composable
fun VehicleCard(vehicle: VehicleUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Header: Icon + Label + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Vehicle Icon Container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Label
                Text(
                    text = vehicle.label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Status Indicator Chip
                SuggestionChip(
                    onClick = { },
                    label = { Text(vehicle.statusLabel) },
                    icon = { StatusIndicator(vehicle.currentStatus) },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Coordinates and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = vehicle.coordinatesLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }

                // Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Updated at",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = vehicle.updatedAtLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusIndicator(status: VehicleStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        VehicleStatus.STOPPED_AT -> MaterialTheme.colorScheme.error
        VehicleStatus.IN_TRANSIT_TO -> MaterialTheme.colorScheme.primary
        VehicleStatus.INCOMING_AT -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color),
    )
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
