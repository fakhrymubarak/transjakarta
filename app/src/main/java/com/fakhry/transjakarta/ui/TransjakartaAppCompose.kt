package com.fakhry.transjakarta.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

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
            // TODO Slicing Details Screen
        }
    }
}
