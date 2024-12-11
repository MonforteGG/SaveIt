package com.example.saveit.navigation

sealed class Screens(val route: String) {
    object SplashScreen : Screens("SplashScreen")
    object LoginScreen : Screens("LoginScreen")
    object HomeScreen : Screens("HomeScreen")
    object ExpensesScreen : Screens("ExpensesScreen")
    object AddExpenseScreen : Screens("AddExpenseScreen/{gastoId}") {
        fun passGastoId(gastoId: String) = "AddExpenseScreen/$gastoId"
    }
    object SetupScreen : Screens("SetupScreen")
}