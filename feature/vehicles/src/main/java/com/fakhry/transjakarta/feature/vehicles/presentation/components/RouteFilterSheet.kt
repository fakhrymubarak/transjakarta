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
import androidx.compose.foundation.lazy.items
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
import com.fakhry.transjakarta.feature.vehicles.presentation.model.FilterOptionUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleFilterUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteFilterSheet(
    state: VehicleFilterUiState,
    onDismissRequest: () -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onRetryRoutes: () -> Unit,
    onRouteSearchChange: (String) -> Unit,
    onRouteToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredRoutes = state.routes

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onClear) { Text(text = "Clear") }
                    Text(
                        text = "Route Filters",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                    FilledTonalButton(onClick = onApply) { Text(text = "Apply") }
                }
            }

            item {
                Text(
                    text = "Routes",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            item {
                OutlinedTextField(
                    value = state.routeSearchQuery,
                    onValueChange = onRouteSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Search routes") },
                    singleLine = true,
                )
            }

            when {
                state.isRoutesLoading -> {
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
                state.routesError != null -> {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = state.routesError,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                            TextButton(onClick = onRetryRoutes) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
                filteredRoutes.isEmpty() -> {
                    item {
                        Text(
                            text = "No routes found",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                else -> {
                    items(filteredRoutes) { route ->
                        FilterOptionRow(
                            option = route,
                            selected = state.selectedRouteIds.contains(route.id),
                            onToggle = { onRouteToggle(route.id) },
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
internal fun FilterOptionRow(option: FilterOptionUiModel, selected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.Checkbox(
            checked = selected,
            onCheckedChange = { onToggle() },
        )
        Text(
            text = option.label,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
