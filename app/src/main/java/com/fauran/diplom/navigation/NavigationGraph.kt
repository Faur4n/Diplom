package com.fauran.diplom.navigation

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fauran.diplom.TAG
import com.fauran.diplom.auth.AuthGraph
import com.fauran.diplom.splash.SplashScreen
import com.fauran.diplom.splash.SplashViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

val LocalRootNavController = staticCompositionLocalOf<NavController?> { null }

@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
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
                val entry  =  navController.currentBackStackEntryAsState()
                LaunchedEffect(key1 = entry, block = {
                    Log.d(TAG, "Navigation: ${navController.backQueue.map { it.destination.route }}")
                })
                MainGraph()
            }
        }
    }
}
