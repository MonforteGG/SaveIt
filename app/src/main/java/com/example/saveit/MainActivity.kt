package com.example.saveit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.saveit.navigation.AppNavigation
import com.example.saveit.ui.theme.E09LoginTheme
import com.example.saveit.ui.theme.backgroundColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Si usas la opción de pantalla a pantalla
        setContent {
            E09LoginTheme {
                // Call the main UI
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    // Define the diagonal gradient brush


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 46.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Navigation Controller
            val navController = rememberNavController()
            // Pass the NavController to AppNavigation
            AppNavigation(navController = navController)
        }
    }
}
