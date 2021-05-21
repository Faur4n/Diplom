package com.fauran.diplom.main.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fauran.diplom.TAG
import com.fauran.diplom.main.home.list_items.*
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.models.RelatedFriend
import com.fauran.diplom.models.Suggestion
import com.fauran.diplom.models.User
import com.fauran.diplom.ui.theme.defaultThemeColor
import com.fauran.diplom.util.ifListOf
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun MainHomeScreen(
    user: User,
    viewModel: HomeViewModel,
    navigationViewModel: NavigationViewModel,
    listState: LazyListState
) {
    val isRefreshing by viewModel.isRefreshing.observeAsState(false)
    val context = LocalContext.current

    val sections = remember(user) {
        user.createSections(context)
    }
    val friends = remember(sections){
        val list = sections.filterNot {
            it.items.filterIsInstance<RelatedFriend>().isEmpty()
        }.firstOrNull()
        Log.d(TAG, "MainHomeScreen: friends list $list")
        list?.items?.filterIsInstance<RelatedFriend>() ?: emptyList()
    }
    val initialPage = remember(friends) { if (friends.isNotEmpty() && friends.size >= 3) 2 else 0 }
    val pagerState =
        rememberPagerState(friends.size, initialOffscreenLimit = 5, initialPage = initialPage)


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
                TextBlock()
            }
            item {
                CardItem(
                    user = user,
                    viewModel,
                )
            }
            item {
                ShareButton(modifier = Modifier.fillMaxWidth()) {

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
                            navigationViewModel.navigateToGenres(genre)
                        }
                    }
                }
                items.ifListOf<RelatedFriend> { friends ->
                    item() {
                        FriendsRow(friends = friends,pagerState)
                    }
                }
                items.ifListOf<Suggestion> {
                    item {
                        Text(text = it.toString())
                    }
                }
            }
        }
    }
}