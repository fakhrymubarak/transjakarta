package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fakhry.transjakarta.core.designsystem.state.UiState
import com.fakhry.transjakarta.feature.vehicles.domain.model.VehicleDetail

@Composable
fun VehicleDetailScreen(
    uiState: UiState<VehicleDetail>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is UiState.Loading -> LoadingState(modifier)
        is UiState.Empty -> EmptyState(modifier, onRetry)
        is UiState.Error -> ErrorState(modifier, uiState.message, uiState.isNetworkError, onRetry)
        is UiState.Success -> SuccessState(modifier, uiState.data)
    }
}

@Composable
private fun LoadingState(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(modifier: Modifier, onRetry: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "No vehicle data")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier,
    message: String,
    isNetwork: Boolean,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val friendly =
            if (isNetwork) "Network error. Check your connection and try again." else message
        Text(
            text = friendly,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun SuccessState(modifier: Modifier, detail: VehicleDetail) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = detail.label,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(text = "Status: ${detail.currentStatus}")
        Text(text = "Updated at: ${detail.updatedAt}")
        Text(text = "Route: ${detail.routeId ?: "-"}")
        Text(text = "Trip: ${detail.tripId ?: "-"}")
        Text(text = "Stop: ${detail.stopId ?: "-"}")

        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(12.dp),
            ) {
                Text(
                    text = "Map placeholder\nLat: ${detail.latitude}, Lng: ${detail.longitude}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
