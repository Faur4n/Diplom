package com.fauran.diplom.auth

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fauran.diplom.navigation.Roots

val LocalAuthNavController  = staticCompositionLocalOf<NavController?> { null }

@ExperimentalMaterialApi
@Composable
fun AuthGraph(){
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalAuthNavController provides navController
    ) {
        NavHost(navController = navController, startDestination = Roots.Auth.route){
            composable(Roots.Auth.route){
                val viewModel : AuthViewModel = hiltViewModel()
                AuthScreen(viewModel = viewModel)
            }
        }
    }
}