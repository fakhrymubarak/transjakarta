package com.fakhry.transjakarta.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.fakhry.transjakarta.core.designsystem.theme.TransjakartaTheme
import com.fakhry.transjakarta.feature.vehicles.presentation.VehicleListScreen

private enum class BottomNavItem(
    val title: String,
    val icon: ImageVector,
) {
    List("List", Icons.AutoMirrored.Filled.List),
    Map("Map", Icons.Default.Place),
}

@Composable
fun MainScreen(onVehicleClick: (String) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = BottomNavItem.entries

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (tabs[selectedTab]) {
            BottomNavItem.List -> {
                VehicleListScreen(
                    onVehicleClick = onVehicleClick,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            BottomNavItem.Map -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Map view coming soon",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    TransjakartaTheme {
        MainScreen(onVehicleClick = {})
    }
}
