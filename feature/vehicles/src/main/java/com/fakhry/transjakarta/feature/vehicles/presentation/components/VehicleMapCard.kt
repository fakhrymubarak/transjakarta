package com.fakhry.transjakarta.feature.vehicles.presentation.components

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VehicleMapCard(
    latitude: Double,
    longitude: Double,
    label: String,
    modifier: Modifier = Modifier,
    bearing: Float = 0f,
    encodedPolyline: String = "",
    onMapInteraction: (Boolean) -> Unit = {},
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        val polyline = remember(encodedPolyline) {
            if (encodedPolyline.isNotEmpty()) {
                com.google.maps.android.PolyUtil.decode(encodedPolyline)
            } else {
                emptyList()
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        ) {
            val vehicleLocation = LatLng(latitude, longitude)
            val uiSettings = remember { MapUiSettings(zoomControlsEnabled = false) }
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(vehicleLocation, 15f)
            }
            val markerState = rememberMarkerState(position = vehicleLocation)

            LaunchedEffect(vehicleLocation) {
                markerState.position = vehicleLocation
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInteropFilter { event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                onMapInteraction(true)
                                false
                            }

                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                onMapInteraction(false)
                                false
                            }

                            else -> false
                        }
                    },
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
            ) {
                if (polyline.isNotEmpty()) {
                    Polyline(
                        points = polyline,
                        color = MaterialTheme.colorScheme.primary,
                        width = 8f,
                    )
                }

                Log.e("BEARING", "bearing -> $bearing")
                MarkerComposable(
                    state = markerState,
                    title = label,
                    rotation = bearing,
                    anchor = Offset(0.5f, 0.5f),
                ) {
                    Canvas(modifier = Modifier.size(32.dp)) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width / 2f, 0f) // Top center
                            lineTo(size.width, size.height) // Bottom right
                            lineTo(size.width / 2f, size.height * 0.7f) // Bottom center indent (arrow shape)
                            lineTo(0f, size.height) // Bottom left
                            close()
                        }
                        drawPath(
                            path = path,
                            color = Color.Red, // Using obvious color
                        )
                    }
                }
            }
        }
    }
}
