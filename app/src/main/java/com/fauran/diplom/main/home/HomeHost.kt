package com.fauran.diplom.main.home

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.main.home.genres_screen.GenresScreen
import com.fauran.diplom.main.home.list_items.*
import com.fauran.diplom.main.home.recommendations.RecData
import com.fauran.diplom.main.home.recommendations.RecommendationScreen
import com.fauran.diplom.main.home.utils.Genre
import com.fauran.diplom.main.vk_api.VKTokenHandler
import com.fauran.diplom.models.*
import com.fauran.diplom.navigation.LocalRootNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import soup.compose.material.motion.MaterialMotion
import soup.compose.material.motion.materialElevationScale

sealed class HomeScreen {
    object Home : HomeScreen()
    data class Genres(val genre: Genre, val artists: List<SpotifyArtist>) : HomeScreen()
    data class Recommendations(val data: List<RecData>) : HomeScreen()
    object Back : HomeScreen()
}

val LocalSpotifyLauncher = compositionLocalOf<ActivityResultLauncher<Int>?> { null }

@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun HomeHost(
    viewModel: HomeViewModel,
    navigationViewModel: NavigationViewModel
) {
    val context = LocalContext.current as ComponentActivity
    val navController = LocalRootNavController.current
    val scaffoldState = rememberScaffoldState()
    val state by viewModel.state.observeAsState(viewModel.state.value)
    val listState = rememberLazyListState()
    val (isLoading, setIsLoading) = remember {
        mutableStateOf(true)
    }

    LaunchedEffect(state) {
        val user = state?.user
        val error = state?.error
        val logout = state?.logout
        when {
            logout == true -> {
                viewModel.logout(context, navController)
                return@LaunchedEffect
            }
            user != null -> setIsLoading(false)
            error != null -> scaffoldState.snackbarHostState.showSnackbar(error)
        }
    }

    VKTokenHandler(viewModel = viewModel)

    val screen by navigationViewModel.currentScreen.observeAsState(navigationViewModel.currentScreen.value)

    BackHandler(routeState = screen) {
        Log.d(TAG, "handleOnBackPressed: $screen")
        navigationViewModel.goBack()
    }

    MaterialMotion(
        targetState = screen,
        motionSpec = materialElevationScale(growing = false)
    ) { newScreen ->
        when (newScreen) {
            HomeScreen.Back -> {
                navController?.popBackStack()
            }
            is HomeScreen.Genres -> {
                GenresScreen(artists = newScreen.artists, genre = newScreen.genre) {
                    navigationViewModel.navigateHome()
                }
            }
            HomeScreen.Home -> {
                Crossfade(targetState = isLoading) { loading ->
                    if (loading) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.align(Center)) {
                                Text(
                                    text = "Loading",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(CenterHorizontally)
                                )
                                LinearProgressIndicator(
                                    modifier = Modifier.align(
                                        CenterHorizontally
                                    )
                                )
                            }
                        }
                    } else {
                        Scaffold(
                            scaffoldState = scaffoldState,
                            topBar = {
                                TopAppBar(
                                    title = { Text(text = stringResource(id = R.string.app_name)) },
                                    elevation = 8.dp,
                                    actions = {
                                        IconButton(onClick = {
                                            viewModel.logout(context, navController)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Logout,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                            },
                        ) {
                            val user = state?.user
                            if (user != null) {
                                MainHomeScreen(
                                    user = user,
                                    viewModel = viewModel,
                                    navigationViewModel = navigationViewModel,
                                    listState
                                )
                            }
                        }
                    }
                }
            }
            is HomeScreen.Recommendations -> {
                val user = state?.user
                if (user != null) {
                    RecommendationScreen(user, onSearchChanged = {

                    }) {
                        navigationViewModel.navigateHome()
                    }
                }

            }
        }
    }
}





