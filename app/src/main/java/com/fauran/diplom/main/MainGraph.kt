package com.fauran.diplom.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fauran.diplom.auth.contracts.SpotifySignInContract
import com.fauran.diplom.main.home.HomeHost
import com.fauran.diplom.main.home.HomeViewModel
import com.fauran.diplom.main.home.LocalSpotifyLauncher
import com.fauran.diplom.main.home.NavigationViewModel
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.navigation.Nav
import com.fauran.diplom.navigation.Screen
import com.google.accompanist.pager.ExperimentalPagerApi
import javax.inject.Inject

val LocalMainNavController = staticCompositionLocalOf<NavController?>{ null }

@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun MainGraph(){
    val navController = rememberNavController()
    val rootNavController = LocalRootNavController.current

    CompositionLocalProvider(
        LocalMainNavController provides navController
    ) {
        NavHost(navController = navController, startDestination = Screen.HomeScreen.route){
            composable(Screen.HomeScreen.route){
                rootNavController?.let { controller ->
                    val viewModel : HomeViewModel = hiltViewModel(
                        controller.getBackStackEntry(Nav.Main.route)
                    )
                    val navViewModel : NavigationViewModel = hiltViewModel(
                        controller.getBackStackEntry(Nav.Main.route)
                    )
                    val spot = rememberLauncherForActivityResult(
                        SpotifySignInContract()
                    ) {
                        viewModel.handleNewSpotifyToken(navController.context, it)
                    }

                    LaunchedEffect(Unit) {
                        viewModel.init(spot)
                        navViewModel.init(spot)
                    }
                    CompositionLocalProvider(
                        LocalSpotifyLauncher provides spot
                    ) {
                        HomeHost(viewModel = viewModel,navigationViewModel = navViewModel)
                    }
                }
            }
        }
    }
}