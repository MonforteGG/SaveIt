package com.example.saveit.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults

import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color

import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screens.HomeScreen,
        Screens.ExpensesScreen,
        Screens.SetupScreen
    )
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route


    NavigationBar(containerColor = Color(0xFF21242B), contentColor = Color.White) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screens.HomeScreen -> Icons.Default.Home
                            Screens.ExpensesScreen -> Icons.Default.AttachMoney
                            Screens.SetupScreen -> Icons.Default.Settings
                            else -> Icons.Default.Home
                        },
                        contentDescription = screen.route,
                        tint = if (isSelected) { Color.White} else Color.DarkGray

                    )
                },
                label = null,
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(Screens.HomeScreen.route) { inclusive = false }
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isSelected) { Color.Green} else Color.White, // Color del ícono seleccionado
                    unselectedIconColor = Color.LightGray, // Color del ícono no seleccionado
                    indicatorColor = Color.Transparent // Cambia el color de la "sombra" o indicador detrás del ícono
                )
            )


        }
    }
}
