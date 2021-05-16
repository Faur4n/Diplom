package com.fauran.diplom.main.home

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.fauran.diplom.TAG
import com.fauran.diplom.auth.contracts.SpotifySignInContract
import com.fauran.diplom.main.vk_api.LocalVkCallback
import com.fauran.diplom.navigation.LocalRootNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import soup.compose.material.motion.MaterialMotion
import soup.compose.material.motion.materialElevationScale

@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun HomeHostScreen(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current as ComponentActivity
    val navController = LocalRootNavController.current

    val isVkEnabled by viewModel.isVkEnabled.observeAsState(viewModel.isVkEnabled.value ?: false)
    val isSpotifyEnabled by viewModel.isSpotifyEnabled.observeAsState(
        viewModel.isSpotifyEnabled.value ?: false
    )

    val vkCallback = LocalVkCallback.current

    LaunchedEffect(Unit) {
        viewModel.handleVkToken(context, vkCallback)
    }
    val spot = rememberLauncherForActivityResult(
        SpotifySignInContract()
    ) {
        viewModel.handleNewSpotifyToken(context, it)
    }
    LaunchedEffect(Unit) {
        viewModel.init(spot)
    }

    val (screen, onScreenChanged) = remember { mutableStateOf<HomeScreen?>(HomeScreen.Home) }

    val showGenres by viewModel.showGenres.observeAsState(viewModel.showGenres.value)

    LaunchedEffect(showGenres) {
        val genres = showGenres
        Log.d(TAG, "HomeScreen: $genres")
        if (genres != null) {
            onScreenChanged(genres)
            viewModel.consumeGenres()
        }
    }

    val dispatcher = LocalOnBackPressedDispatcherOwner.current

    val backCallback = remember(screen) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: $screen")
                when (screen) {
                    is HomeScreen.Genres -> {
                        onScreenChanged(HomeScreen.Home)
                    }
                    HomeScreen.Home -> {
                        navController?.popBackStack()
                    }
                }
            }
        }
    }
    val listState = rememberLazyListState()

    DisposableEffect(dispatcher, screen) {
        dispatcher?.onBackPressedDispatcher?.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }

    CompositionLocalProvider(
        LocalSpotifyEnabled provides isSpotifyEnabled,
        LocalVkEnabled provides isVkEnabled
    ) {
        MaterialMotion(
            targetState = screen,
            motionSpec = materialElevationScale(growing = false)
        ) { newScreen ->
            when (newScreen) {
                is HomeScreen.Genres -> {
                    GenresScreen(artists = newScreen.artists, genre = newScreen.genre) {
                        onScreenChanged(HomeScreen.Home)
                    }
                }
                HomeScreen.Home -> {
                    HomeScreen(viewModel = viewModel, listState = listState)
                }
            }
        }
    }
}




