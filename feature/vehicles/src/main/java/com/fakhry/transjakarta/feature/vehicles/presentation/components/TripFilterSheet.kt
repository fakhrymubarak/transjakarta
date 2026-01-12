package com.fakhry.transjakarta.feature.vehicles.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.fakhry.transjakarta.feature.vehicles.presentation.model.FilterOptionUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleFilterUiState
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripFilterSheet(
    state: VehicleFilterUiState,
    trips: LazyPagingItems<FilterOptionUiModel>,
    onDismissRequest: () -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onTripSearchChange: (String) -> Unit,
    onTripToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tripItems = trips.itemSnapshotList.items

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onClear) { Text(text = "Clear") }
            Text(
                text = "Trip Filters",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            FilledTonalButton(onClick = onApply) { Text(text = "Apply") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.tripSearchQuery,
            onValueChange = onTripSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            label = { Text(text = "Search trips") },
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val needsFilter = state.selectedRouteIds.isEmpty() && state.tripSearchQuery.isBlank()

            if (needsFilter) {
                item {
                    Text(
                        text = "Select at least one route or enter a trip name to load trips.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else if (trips.loadState.refresh is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            } else if (trips.loadState.refresh is LoadState.Error) {
                item {
                    val error = (trips.loadState.refresh as LoadState.Error).error
                    val message =
                        if (error is IOException) {
                            "Network error. Check your connection and try again."
                        } else {
                            "Failed to load trips"
                        }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        TextButton(onClick = { trips.retry() }) {
                            Text(text = "Retry")
                        }
                    }
                }
            } else if (trips.itemCount == 0) {
                item {
                    Text(
                        text = "No trips found",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                items(count = tripItems.size) { index ->
                    val trip = trips[index]
                    if (trip != null) {
                        FilterOptionRow(
                            option = trip,
                            selected = state.selectedTripIds.contains(trip.id),
                            onToggle = { onTripToggle(trip.id) },
                        )
                    }
                }
            }

            if (trips.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            if (trips.loadState.append is LoadState.Error) {
                item {
                    val error = (trips.loadState.append as LoadState.Error).error
                    val message =
                        if (error is IOException) {
                            "Network error. Check your connection and try again."
                        } else {
                            "Failed to load more trips"
                        }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        TextButton(onClick = { trips.retry() }) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
