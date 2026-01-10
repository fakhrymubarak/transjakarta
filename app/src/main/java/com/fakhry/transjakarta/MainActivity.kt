package com.fakhry.transjakarta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fakhry.transjakarta.core.designsystem.theme.TransjakartaTheme
import com.fakhry.transjakarta.ui.TransjakartaApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TransjakartaTheme {
                TransjakartaApp()
            }
        }
    }
}
