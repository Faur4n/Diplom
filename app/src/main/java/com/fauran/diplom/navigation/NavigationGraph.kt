package com.fauran.diplom.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.fauran.diplom.auth.AuthGraph
import com.fauran.diplom.main.MainGraph
import com.fauran.diplom.splash.SplashScreen
import com.fauran.diplom.splash.SplashViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.delay

val LocalRootNavController = staticCompositionLocalOf<NavController?> { null }

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
                val viewModel : SplashViewModel = hiltNavGraphViewModel()
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
