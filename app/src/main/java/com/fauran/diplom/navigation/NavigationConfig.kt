package com.fauran.diplom.navigation

sealed class Nav(
    val startDestination: String,
    val route: String,
) {
    object Auth : Nav(Screen.Auth.route, "auth_nav")
    object Splash : Nav(Screen.Loading.route, "splash_nav")
    object Main : Nav(Screen.HomeScreen.route, "main_nav")
}


sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Loading : Screen("loading")
    object HomeScreen : Screen("home_screen")
}