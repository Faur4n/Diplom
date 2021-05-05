package com.fauran.diplom.main.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.fauran.diplom.TAG
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.navigation.Nav


@Composable
fun HomeScreen(
    viewModel: HomeViewModel
){
    val navController = LocalRootNavController.current
    val status by viewModel.status.observeAsState(viewModel.status.value ?: HomeStatus.Loading)
    var showLoading by remember {
        mutableStateOf(true)
    }


    LaunchedEffect(status){
        Log.d(TAG, "HomeScreen: $status")
        when(status){
            HomeStatus.Data -> {

            }
            HomeStatus.FirstLaunch -> {
                showLoading = false
            }
            HomeStatus.Loading -> {
                showLoading = true
            }
            HomeStatus.NotAuthorized -> {
                navController?.navigate(Nav.Auth.route) {
                    popUpTo(Nav.Main.route) {
                        inclusive = true
                    }
                }
            }
        }

    }

    Scaffold() {
        Column(modifier = Modifier.fillMaxSize()){
            if(showLoading){
                Text(text = "Loading", textAlign = TextAlign.Center,modifier = Modifier.align(Alignment.CenterHorizontally))
                LinearProgressIndicator(modifier =  Modifier.align(Alignment.CenterHorizontally))
            }else{
                Text(text = "THIS IS HOME SCREEN", Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}