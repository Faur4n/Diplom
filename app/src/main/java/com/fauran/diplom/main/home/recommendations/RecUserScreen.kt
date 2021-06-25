package com.fauran.diplom.main.home.recommendations

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.transform.CircleCropTransformation
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.main.home.list_items.*
import com.fauran.diplom.main.home.utils.Genre
import com.fauran.diplom.main.home.utils.createSections
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.models.RelatedFriend
import com.fauran.diplom.models.Suggestion
import com.fauran.diplom.models.User
import com.fauran.diplom.util.ifListOf
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun RecommendationUserScreen(
    navController: NavController,
    user: User
) {
    Log.d(TAG, "RecommendationUserScreen: ${user.photoUrl}")
    val paint = rememberCoilPainter(request = user.photoUrl, requestBuilder = {
        transformations(CircleCropTransformation())
    })
    val pagerState = rememberPagerState(pageCount = user.friends?.size ?: 0)
    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            title = { Text(text = user.name.toString()) },
            elevation = 8.dp,
            actions = {

                Icon(painter = paint, contentDescription = null,modifier=  Modifier.size(54.dp))
            }
        )
    }) {
        val listState = rememberLazyListState()
        val context = LocalContext.current
        val sections = remember(user) {
            user.createSections(context)
        }
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier
                .clipToBounds()
                .fillMaxSize()
        ) {
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
//                            viewModel.goToGenres(genre)
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