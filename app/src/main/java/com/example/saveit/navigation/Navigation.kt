package com.example.saveit.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.saveit.screens.addExpense.AddExpenseScreen
import com.example.saveit.screens.home.ExpensesScreen
import com.example.saveit.screens.home.HomeScreen
import com.example.saveit.screens.home.SetupScreen
import com.example.saveit.screens.login.LoginScreen
import com.example.saveit.screens.splash.SplashScreen


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.SplashScreen.route) {
        composable(route = Screens.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(route = Screens.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(route = Screens.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route = Screens.ExpensesScreen.route) {
            ExpensesScreen(navController)
        }
        // Modificar la ruta de AddExpenseScreen para pasar el gastoId
        composable(route = Screens.AddExpenseScreen.route) { backStackEntry ->
            val gastoId = backStackEntry.arguments?.getString("gastoId")
            AddExpenseScreen(navController, gastoId)
        }
        composable(route = Screens.SetupScreen.route) {
            SetupScreen(navController)
        }
    }
}