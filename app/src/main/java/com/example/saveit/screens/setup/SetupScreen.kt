package com.example.saveit.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saveit.navigation.BottomNavigationBar
import com.example.saveit.ui.theme.cardColor
import com.example.saveit.ui.theme.textColor
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SetupScreen(navController : NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Contenido principal de la pantalla
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            LogoutButton(navController)
        }

        // Barra de navegación en la parte inferior
        BottomNavigationBar(navController = navController)
    }
}


@Composable
fun LogoutButton(navController: NavController) {
    Button(onClick = {
        // Cerrar sesión
        FirebaseAuth.getInstance().signOut()
        // Navegar a la pantalla de inicio de sesión
        navController.navigate("LoginScreen") {
            // Eliminar todas las pantallas anteriores para evitar volver atrás
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = textColor),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp,
            disabledElevation = 4.dp
        )


    ) {
        Text("Log Out")
    }
}