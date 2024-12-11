package com.example.saveit.screens.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

import com.example.saveit.ui.theme.textColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    // Estado para la progresión de la barra de carga
    var progress by remember { mutableStateOf(0f) }

    // Lanzamos una corrutina para incrementar el progreso de la barra
    LaunchedEffect(key1 = true) {
        val increment = 0.01f // Incremento de la barra de carga
        while (progress < 1f) {
            progress += increment
            delay(20) // Control de la velocidad de llenado de la barra
        }
        navController.navigate("LoginScreen")
    }

    // Diseño principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Texto animado
            Text(
                text = "save it.",
                modifier = Modifier.padding(top = 50.dp, bottom = 60.dp),
                style = TextStyle(
                    fontSize = 70.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = textColor
            )

            // Barra de carga
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(200.dp)
                    .height(8.dp),
                color = textColor,
                trackColor = Color.LightGray,
            )
        }
    }
}
