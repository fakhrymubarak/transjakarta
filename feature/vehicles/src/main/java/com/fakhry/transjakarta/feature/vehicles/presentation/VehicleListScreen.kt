package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.fakhry.transjakarta.feature.vehicles.domain.error.RateLimitException
import com.fakhry.transjakarta.feature.vehicles.presentation.components.EmptyContent
import com.fakhry.transjakarta.feature.vehicles.presentation.components.ErrorContent
import com.fakhry.transjakarta.feature.vehicles.presentation.components.LoadingContent
import com.fakhry.transjakarta.feature.vehicles.presentation.components.PaginationErrorItem
import com.fakhry.transjakarta.feature.vehicles.presentation.components.RouteFilterSheet
import com.fakhry.transjakarta.feature.vehicles.presentation.components.TripFilterSheet
import com.fakhry.transjakarta.feature.vehicles.presentation.components.VehicleCard
import com.fakhry.transjakarta.feature.vehicles.presentation.model.RateLimitUiState
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleUiModel

@Suppress("ParamsComparedByRef")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(
    modifier: Modifier = Modifier,
    onVehicleClick: (String) -> Unit,
    viewModel: VehicleListViewModel = hiltViewModel(),
    filterViewModel: FilterViewModel = hiltViewModel(),
) {
    val lazyPagingItems = viewModel.vehiclesPagingFlow.collectAsLazyPagingItems()
    val rateLimitState by viewModel.rateLimitState.collectAsState()
    val filterState by filterViewModel.uiState.collectAsState()
    val appliedFilters by filterViewModel.appliedFilters.collectAsState()
    val routeFilterCount = appliedFilters.routeIds.size
    val tripFilterCount = appliedFilters.tripIds.size
    var showRouteFilters by remember { mutableStateOf(false) }
    var showTripFilters by remember { mutableStateOf(false) }

    LaunchedEffect(appliedFilters) {
        viewModel.applyFilters(appliedFilters)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Vehicles") },
                actions = {
                    FilterEntryButton(
                        label = "Routes",
                        count = routeFilterCount,
                        onClick = { showRouteFilters = true },
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterEntryButton(
                        label = "Trips",
                        count = tripFilterCount,
                        onClick = { showTripFilters = true },
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
    ) { paddingValues ->
        if (showRouteFilters) {
            RouteFilterSheet(
                state = filterState,
                onDismissRequest = { showRouteFilters = false },
                onApply = {
                    filterViewModel.applyFilters()
                    showRouteFilters = false
                },
                onClear = { filterViewModel.clearRoutes() },
                onRetryRoutes = { filterViewModel.retryRoutes() },
                onRouteSearchChange = filterViewModel::updateRouteSearchQuery,
                onRouteToggle = filterViewModel::toggleRouteSelection,
            )
        }
        if (showTripFilters) {
            val tripItems = filterViewModel.tripsPagingFlow.collectAsLazyPagingItems()
            TripFilterSheet(
                state = filterState,
                trips = tripItems,
                onDismissRequest = { showTripFilters = false },
                onApply = {
                    filterViewModel.applyFilters()
                    showTripFilters = false
                },
                onClear = { filterViewModel.clearTrips() },
                onTripSearchChange = filterViewModel::updateTripSearchQuery,
                onTripToggle = filterViewModel::toggleTripSelection,
            )
        }
        VehicleListContent(
            lazyPagingItems = lazyPagingItems,
            onVehicleClick = onVehicleClick,
            rateLimitState = rateLimitState,
            onRateLimitDetected = viewModel::onRateLimitDetected,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Suppress("ParamsComparedByRef")
@Composable
private fun VehicleListContent(
    lazyPagingItems: LazyPagingItems<VehicleUiModel>,
    onVehicleClick: (String) -> Unit,
    rateLimitState: RateLimitUiState?,
    onRateLimitDetected: (RateLimitException) -> Unit,
    modifier: Modifier = Modifier,
) {
    val refreshState = lazyPagingItems.loadState.refresh
    val refreshError = (refreshState as? LoadState.Error)?.error
    val refreshRateLimit = refreshError as? RateLimitException
    val refreshRateLimitState = if (refreshRateLimit != null) rateLimitState else null
    var isRefreshing by remember { mutableStateOf(false) }

    // Update isRefreshing based on load state
    isRefreshing = refreshState is LoadState.Loading

    LaunchedEffect(refreshRateLimit) {
        refreshRateLimit?.let(onRateLimitDetected)
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { lazyPagingItems.refresh() },
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            // Initial loading
            refreshState is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
                LoadingContent()
            }
            // Initial error
            refreshState is LoadState.Error && lazyPagingItems.itemCount == 0 -> {
                ErrorContent(
                    message = "An error occurred",
                    rateLimitState = refreshRateLimitState,
                    onRetry = { lazyPagingItems.retry() },
                )
            }
            // Empty state
            refreshState is LoadState.NotLoading && lazyPagingItems.itemCount == 0 -> {
                EmptyContent()
            }
            // Content loaded
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        count = lazyPagingItems.itemCount,
                        key = { index ->
                            lazyPagingItems[index]?.let { "${it.id}_$index" } ?: index
                        },
                    ) { index ->
                        val vehicle = lazyPagingItems[index]
                        if (vehicle != null) {
                            VehicleCard(
                                vehicle = vehicle,
                                onClick = { onVehicleClick(vehicle.id) },
                            )
                        }
                    }

                    // Pagination loading indicator
                    if (lazyPagingItems.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }

                    // Pagination error
                    if (lazyPagingItems.loadState.append is LoadState.Error) {
                        item {
                            val error = (lazyPagingItems.loadState.append as LoadState.Error).error
                            val appendRateLimit = error as? RateLimitException
                            val appendRateLimitState =
                                if (appendRateLimit != null) rateLimitState else null
                            LaunchedEffect(appendRateLimit) {
                                appendRateLimit?.let(onRateLimitDetected)
                            }
                            PaginationErrorItem(
                                message = "Failed to load more",
                                rateLimitState = appendRateLimitState,
                                onRetry = { lazyPagingItems.retry() },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterEntryButton(label: String, count: Int, onClick: () -> Unit) {
    val buttonLabel = if (count > 0) "$label ($count)" else label
    if (count > 0) {
        FilledTonalButton(onClick = onClick) {
            Text(text = buttonLabel)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(text = buttonLabel)
        }
    }
}

internal fun retryButtonLabel(rateLimitState: RateLimitUiState?): String =
    if (rateLimitState?.retryEnabled == false) {
        "Retry in ${rateLimitState.countdownLabel}"
    } else {
        "Retry"
    }
