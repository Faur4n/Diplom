package com.fauran.diplom.navigation

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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.fauran.diplom.auth.AuthGraph
import kotlinx.coroutines.delay

val LocalRootNavController = staticCompositionLocalOf<NavController?> { null }

@Composable
fun Navigation(){
    val navController = rememberNavController()
    CompositionLocalProvider(
            LocalRootNavController  provides navController
    ) {
        NavHost(navController = navController, startDestination = Nav.Loading.route){
            composable(Nav.Loading.route){
                Scaffold() {
                    Box(modifier = Modifier.fillMaxSize()){
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                LaunchedEffect(Unit){
                    delay(1000)
                    navController.navigate(Nav.Auth.route)
                }
            }
            composable(Nav.Auth.route){
                AuthGraph()
            }
            composable(Nav.Main.route){

            }
        }
    }
}
