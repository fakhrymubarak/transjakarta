package com.fakhry.transjakarta.feature.vehicles.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun VehicleMapCard(
    latitude: Double,
    longitude: Double,
    label: String,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        ) {
            val vehicleLocation = LatLng(latitude, longitude)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(vehicleLocation, 15f)
            }

            LaunchedEffect(vehicleLocation) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLng(vehicleLocation),
                    durationMs = 1000
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
            ) {
                Marker(
                    state = rememberMarkerState(position = vehicleLocation),
                    title = label,
                )
            }
        }
    }
}
