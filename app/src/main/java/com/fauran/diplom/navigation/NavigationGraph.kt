package com.fauran.diplom.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

val LocalRootNavController = staticCompositionLocalOf<NavController?> { null }

@Composable
fun Navigation(){
    val navController = rememberNavController()
    CompositionLocalProvider(
            LocalRootNavController  provides navController
    ) {
        NavHost(navController = navController, startDestination = Nav.Loading.route){
            composable(Nav.Loading.route){
                Box(modifier = Modifier.fillMaxSize()){
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            composable(Nav.Auth.route){

            }
            composable(Nav.Main.route){

            }
        }
    }
}
