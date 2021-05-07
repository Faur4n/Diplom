package com.fauran.diplom.main.home

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.LocalVkCallback
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.auth.contracts.SpotifySignInContract
import com.fauran.diplom.main.home.list_items.*
import com.fauran.diplom.models.*
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.defaultThemeColor
import com.fauran.diplom.ui.theme.white
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import soup.compose.material.motion.MaterialMotion
import soup.compose.material.motion.materialElevationScale


val LocalSpotifyEnabled = compositionLocalOf { false }
val LocalVkEnabled = compositionLocalOf { false }

sealed class HomeScreen {
    object Home : HomeScreen()
    data class Genres(val genre: Genre, val artists: List<SpotifyArtist>) : HomeScreen()
}

@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current as ComponentActivity
    val navController = LocalRootNavController.current
    val state = rememberScaffoldState()


    val status by viewModel.status.observeAsState(viewModel.status.value ?: HomeStatus.Loading)

    var showLoading by remember {
        mutableStateOf(true)
    }
    var user by remember() {
        mutableStateOf<User?>(null)
    }
    val sections by remember(user) {
        mutableStateOf(user?.createSections(context) ?: listOf(emptySection))
    }

    val isRefreshing by viewModel.isRefreshing.observeAsState(false)

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
    LaunchedEffect(status) {
        Log.d(TAG, "HomeScreen: $status")
        when (val st = status) {
            is HomeStatus.Data -> {
                user = st.user
                showLoading = false
            }
            HomeStatus.FirstLaunch -> {

                showLoading = false
            }
            HomeStatus.Loading -> {
                showLoading = true
            }
            HomeStatus.NotAuthorized -> {
                viewModel.logout(context, navController)
            }
            is HomeStatus.Error -> {
                Log.d(TAG, "HomeScreen: ${st.msg}")
                state.snackbarHostState.showSnackbar(st.msg)
            }
        }
    }
    var currentSection by remember {
        mutableStateOf(
            sections.first()
        )
    }
    val listState = rememberLazyListState()


    val (screen, onScreenChanged) = remember { mutableStateOf<HomeScreen>(HomeScreen.Home) }


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
                    Scaffold(
                        scaffoldState = state, drawerContent = {
                            Button(onClick = {
                                viewModel.logout(context, navController)
                            }) {
                                Text(text = "ВЫЙТИ")
                            }
                        }) {
                        Crossfade(targetState = showLoading) { loading ->
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
                                SwipeRefresh(
                                    state = rememberSwipeRefreshState(isRefreshing),
                                    onRefresh = {
                                        viewModel.refresh()
                                    }) {
                                    LazyColumn(
                                        state = listState,
                                        contentPadding = PaddingValues(bottom = 32.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .background(
                                                brush = defaultThemeColor.gradient
                                            )
                                            .clipToBounds()
                                            .fillMaxSize()
                                    ) {
                                        item {
                                            val animatedProgress = remember {
                                                androidx.compose.animation.core.Animatable(
                                                    initialValue = 0.8f
                                                )
                                            }
                                            LaunchedEffect(Unit) {
                                                animatedProgress.animateTo(
                                                    targetValue = 1f,
                                                    animationSpec = tween(300)
                                                )
                                            }
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        8.dp
                                                    )
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.hello),
                                                    style = Typography.h3,
                                                    color = white,

                                                )
                                                Text(
                                                    text = stringResource(id = R.string.description),
                                                    style = Typography.subtitle2,
                                                    color = white,
                                                )
                                                CardItem(
                                                    user = user,
                                                    viewModel,
                                                    Modifier.graphicsLayer(
                                                        scaleY = animatedProgress.value,
                                                        scaleX = animatedProgress.value
                                                    )

                                                )
                                            }
                                        }
                                        sections.forEach { section ->
                                            stickyHeader(section.id + "|") {
                                                ItemTitle(
                                                    title = stringResource(section.title),
                                                    icon = section.icon
                                                )
                                            }
                                            val items = section.items
                                            when {
                                                items.isNotEmpty() && items.first() is MusicData -> {
                                                    val data = items.map { it as MusicData }
                                                    items(data.windowed(2, 2, true)) { item ->
                                                        Row(modifier = Modifier.fillMaxWidth()) {
                                                            val first = item.firstOrNull()
                                                            if (first != null) {
                                                                MusicItem(
                                                                    first,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                            }
                                                            val second = item.lastOrNull()
                                                            if (second != null) {
                                                                MusicItem(
                                                                    second,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                items.isNotEmpty() && items.first() is Genre -> {
                                                    val item = items.map { it as Genre }
                                                    item() {
                                                        GenresItem(item) { genre ->
                                                            viewModel.downloadGenreInfo(genre)
                                                        }
                                                    }
                                                }
                                                items.isNotEmpty() && items.first() is RelatedFriend -> {
                                                    val item = items.map { it as RelatedFriend }

                                                    item() {
                                                        FriendsRow(friends = item)
                                                    }
                                                }
                                                items.isNotEmpty() && items.first() is Suggestion ->{
                                                    val item = items.map { it as Suggestion }
                                                    item() {
                                                        Text(text = item.toString())
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }
}



