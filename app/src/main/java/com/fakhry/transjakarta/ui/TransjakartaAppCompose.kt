package com.fakhry.transjakarta.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fakhry.transjakarta.feature.vehicles.presentation.VehicleDetailScreen
import com.fakhry.transjakarta.feature.vehicles.presentation.VehicleDetailViewModel

@Composable
fun TransjakartaApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            MainScreen(
                onVehicleClick = { vehicleId ->
                    navController.navigate("vehicle_detail/$vehicleId")
                },
            )
        }
        composable(
            route = "vehicle_detail/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType }),
        ) {
            val viewModel: VehicleDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            VehicleDetailScreen(
                uiState = uiState,
                onRetry = viewModel::retry,
                modifier = Modifier,
            )
        }
    }
}
