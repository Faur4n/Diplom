package com.fauran.diplom.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fauran.diplom.navigation.Screen

val LocalMainNavController = staticCompositionLocalOf<NavController?>{ null }

@Composable
fun MainGraph(){
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalMainNavController provides navController
    ) {
        NavHost(navController = navController, startDestination = Screen.HomeScreen.route){
            composable(Screen.HomeScreen.route){
                Scaffold() {
                    Box(modifier = Modifier.fillMaxSize()){
                        Text(text = "THIS IS HOME SCREEN",Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}