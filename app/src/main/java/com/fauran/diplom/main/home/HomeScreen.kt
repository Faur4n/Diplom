package com.fauran.diplom.main.home

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.main.home.list_items.*
import com.fauran.diplom.main.home.utils.Genre
import com.fauran.diplom.main.home.utils.createSections
import com.fauran.diplom.main.vk_api.VKTokenHandler
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.models.RelatedFriend
import com.fauran.diplom.models.Suggestion
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.navigation.Screens
import com.fauran.diplom.ui.theme.backgroundGray
import com.fauran.diplom.ui.theme.black
import com.fauran.diplom.ui.theme.defaultThemeColor
import com.fauran.diplom.util.ifListOf
import com.fauran.diplom.util.rememberLazyListStateSavable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    listState: LazyListState
) {
    val context = LocalContext.current as ComponentActivity
    val rootNavController = LocalRootNavController.current
    val scaffoldState = rememberScaffoldState()
    val state by viewModel.state.collectAsState(viewModel.state.value)
    val user = remember(state) { state.user }

    val isRefreshing by viewModel.isRefreshing.observeAsState(false)
    val sections = remember(user) {
        user?.createSections(context) ?: emptyList()
    }
    val friends = remember(sections) {
        val list = sections.filterNot {
            it.items.filterIsInstance<RelatedFriend>().isEmpty()
        }.firstOrNull()
        Log.d(TAG, "MainHomeScreen: friends list $list")
        list?.items?.filterIsInstance<RelatedFriend>() ?: emptyList()
    }

    val initialPage = remember(friends) { if (friends.isNotEmpty() && friends.size >= 3) 2 else 0 }
    val pagerState =
        rememberPagerState(friends.size, initialOffscreenLimit = 5, initialPage = initialPage)

    LaunchedEffect(state) {
        val error = state.error
        val logout = state.logout
        when {
            logout -> {
                viewModel.logout(context, rootNavController)
                return@LaunchedEffect
            }
            error != null -> scaffoldState.snackbarHostState.showSnackbar(error)
        }
    }

    val genreState by viewModel.genreState.collectAsState(null)
    LaunchedEffect(genreState) {
        if (genreState != null) {
            navController.navigate(Screens.GenreScreen.route)
        }
    }
    VKTokenHandler(viewModel = viewModel)

    Scaffold(scaffoldState = scaffoldState) {
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = "Loading",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    LinearProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }
            }
        } else {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    viewModel.refresh()
                },modifier = Modifier.background(defaultThemeColor.gradient)) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 32.dp),
                    modifier = Modifier
//                        .background(
//                            SolidColor(black),
//                            alpha = .6f
//                        )
                        .clipToBounds()
                        .fillMaxSize()
                ) {
                    item {
                        UserItem(
                            user = user,
                            viewModel,
                        )
                    }
                    sections.forEach { section ->
                        stickyHeader(section.id + "|") {
                            ItemTitle(
                                title = stringResource(section.title),
                                icon = section.icon
                            )
                        }
                        val items = section.items
                        items.ifListOf<MusicData> { data ->
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
                        items.ifListOf<Genre> {
                            item {
                                GenresItem(it) { genre ->
                                    viewModel.goToGenres(genre)
                                }
                            }
                        }
                        items.ifListOf<RelatedFriend> { friends ->
                            item {
                                FriendsRow(friends = friends, pagerState)
                            }
                        }
                        items.ifListOf<Suggestion> { suggestions ->
                            items(suggestions) {
                                if (it.hasSomething())
                                    SuggestionCard(
                                        suggestion = it,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                            }
                        }

                    }
                }
            }
        }
    }
}
