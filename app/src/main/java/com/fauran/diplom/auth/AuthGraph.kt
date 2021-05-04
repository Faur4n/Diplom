package com.fauran.diplom.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fauran.diplom.navigation.Screen

val LocalAuthNavController  = staticCompositionLocalOf<NavController?> { null }

@Composable
fun AuthGraph(){
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalAuthNavController provides navController
    ) {
        NavHost(navController = navController, startDestination = Screen.Auth.route){
            composable(Screen.Auth.route){
                val viewModel : AuthViewModel = hiltNavGraphViewModel()
                AuthScreen(viewModel = viewModel)
            }
        }
    }
}