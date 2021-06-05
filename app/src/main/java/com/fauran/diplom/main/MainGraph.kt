package com.fauran.diplom.main

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fauran.diplom.TAG
import com.fauran.diplom.auth.contracts.SpotifySignInContract
import com.fauran.diplom.main.home.HomeScreen
import com.fauran.diplom.main.home.HomeViewModel
import com.fauran.diplom.main.home.ListSaver
import com.fauran.diplom.main.home.ListState
import com.fauran.diplom.main.home.genres_screen.GenresScreen
import com.fauran.diplom.main.home.recommendations.RecommendationList
import com.fauran.diplom.main.home.recommendations.RecommendationScreen
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.navigation.Roots
import com.fauran.diplom.navigation.Screens
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

val LocalMainNavController = staticCompositionLocalOf<NavController?> { null }

val bottomNavigationItems = listOf(
    BottomNavItem(
        Roots.Home.route,
        Icons.Default.Home
    ),
    BottomNavItem(
        Roots.Recommendations.route,
        Icons.Default.Recommend
    )
)

data class BottomNavItem(
    val route : String, val icon : ImageVector
)

@Composable
fun NavStateController(
    navState: MutableState<Bundle>,
    navController: NavController,
    content: @Composable () -> Unit
) {
    DisposableEffect(navController.currentBackStackEntryAsState()) {
        val callback = NavController.OnDestinationChangedListener { navController, _, _ ->
            navState.value = navController.saveState() ?: Bundle()
        }
        navController.addOnDestinationChangedListener(callback)
        navController.restoreState(navState.value)
        onDispose {
            navController.removeOnDestinationChangedListener(callback)

        }
    }
    content()
}

@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun MainGraph() {
    val navController = rememberNavController()
    val rootNavController = LocalRootNavController.current
    val context = LocalContext.current

    val viewModel: HomeViewModel? = rootNavController?.currentBackStackEntry?.let { hiltViewModel(it) }
    val spotifyHandler =
        rememberLauncherForActivityResult(contract = SpotifySignInContract()) {
            viewModel?.connectSpotify(context, it)
        }
    LaunchedEffect(Unit) {
        viewModel?.init(spotifyHandler)
    }
    val (listStateSaver, saveListState) = rememberSaveable(saver = ListSaver) {
        mutableStateOf(ListState())
    }
    val listState = rememberLazyListState(
        listStateSaver.initialFirstVisibleItemIndex,
        listStateSaver.initialFirstVisibleItemScrollOffset
    )

    LaunchedEffect(listState) {
        saveListState(
            ListState(
                listState.firstVisibleItemIndex,
                listState.firstVisibleItemScrollOffset
            )
        )
    }
    CompositionLocalProvider(
        LocalMainNavController provides navController
    ) {
        val NavStateSaver: Saver<MutableState<Bundle>, out Any> = Saver(
            save = { it.value },
            restore = { mutableStateOf(it) }
        )
        val homeState = rememberSaveable(saver = NavStateSaver) {
            mutableStateOf(Bundle())
        }


        Scaffold(bottomBar = {
            val currentScreen by navController.currentBackStackEntryAsState()
            BottomNavigation() {
                bottomNavigationItems.forEach { item ->
                    BottomNavigationItem(
                        selected = currentScreen?.destination?.route == item.route,
                        onClick = {
                            navController.navigate(item.route)
                        },
                        icon = {
                            val paint = rememberVectorPainter(image = item.icon)
                            Icon(painter = paint, contentDescription = null)
                        })
                }
            }
        }) {
            NavHost(navController = navController, startDestination = Roots.Home.route,modifier = Modifier.padding(it)) {

                composable(Roots.Home.route) {
                    val homeNavController = rememberNavController()
                    NavStateController(homeState,homeNavController){
                        NavHost(
                            navController = homeNavController,
                            startDestination = Screens.HomeScreen.route
                        ) {
                            composable(Screens.HomeScreen.route) {

                                if (viewModel != null) {
                                    HomeScreen(viewModel = viewModel,navController = homeNavController,listState)
                                }
                            }
                            composable(Screens.GenreScreen.route) {
                                if (viewModel != null) {
                                    GenresScreen(viewModel, homeNavController)
                                }
                            }
                        }
                    }

                }
                composable(Roots.Recommendations.route) {
                    if (viewModel != null) {
                        RecommendationScreen(
                            viewModel,
                        )
                    }
                }
            }
        }
    }
}