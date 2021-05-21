package com.fauran.diplom.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.navigation.Nav

@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    val context = LocalContext.current
    val navController = LocalRootNavController.current
    Scaffold() {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    LaunchedEffect(Unit) {
        navController?.let {
            if (viewModel.userIsAuthorized()) {
                navController.navigate(Nav.Main.route){
                    popUpTo(Nav.Splash.route){
                        inclusive = true
                    }
                }
            } else {
                navController.navigate(Nav.Auth.route){
                    popUpTo(Nav.Splash.route){
                        inclusive = true
                    }
                }
            }
        }
    }
}