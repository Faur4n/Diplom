package com.fauran.diplom.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fauran.diplom.auth.AuthGraph
import com.fauran.diplom.main.MainGraph
import com.fauran.diplom.splash.SplashScreen
import com.fauran.diplom.splash.SplashViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

val LocalRootNavController = staticCompositionLocalOf<NavController?> { null }

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun Navigation(){
    val navController = rememberNavController()
    CompositionLocalProvider(
            LocalRootNavController  provides navController
    ) {
        NavHost(navController = navController, startDestination = Nav.Splash.route){
            composable(Nav.Splash.route){
                val viewModel : SplashViewModel = hiltViewModel()
                SplashScreen(viewModel)
            }
            composable(Nav.Auth.route){
                AuthGraph()
            }
            composable(Nav.Main.route){
                MainGraph()
            }
        }
    }
}
