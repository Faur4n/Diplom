package com.fauran.diplom.navigation

sealed class Nav(
    val startDestination: String,
    val route: String,
) {
    object Auth : Nav(Roots.Auth.route, "auth_nav")
    object Splash : Nav(Roots.Loading.route, "splash_nav")
    object Main : Nav(Roots.Home.route, "main_nav")
}

sealed class Roots(val route: String) {
    object Auth : Roots("auth")
    object Loading : Roots("loading")
    object Home : Roots("home")
    object Recommendations : Roots("recommendation")
}

sealed class Screens(val route: String){
    object HomeScreen : Screens("home_screen")
    object GenreScreen : Screens("genres_screen")
    object RecommendationScreen : Screens("recommendation_screen")
    object RecUserScreen : Screens("rec_user_screen")
}

