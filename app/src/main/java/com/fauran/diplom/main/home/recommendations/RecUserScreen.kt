package com.fauran.diplom.main.home.recommendations

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.fauran.diplom.main.home.recommendations.models.Intersection
import com.fauran.diplom.main.home.recommendations.models.RecommendationUser
import com.fauran.diplom.main.home.recommendations.widgets.ExpandableItem
import com.fauran.diplom.main.home.recommendations.widgets.FriendsItem
import com.fauran.diplom.main.home.recommendations.widgets.NewFriendsRow
import com.fauran.diplom.main.home.utils.Genre
import com.fauran.diplom.main.home.utils.createSections
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.models.RelatedFriend
import com.fauran.diplom.models.Suggestion
import com.fauran.diplom.ui.theme.defaultThemeColor
import com.fauran.diplom.util.ifListOf
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun RecommendationUserScreen(
    navController: NavController,
    recUser: RecommendationUser
) {
    val user = recUser.user
    val pagerState = rememberPagerState(pageCount = user.friends?.size ?: 0)
    val context = LocalContext.current
    val resources = context.resources
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
        )
    }, modifier = Modifier.background(defaultThemeColor.gradient)) {
        val listState = rememberLazyListState()
        val sections = remember(user) {
            user.createSections(context)
        }
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier
                .clipToBounds()
                .fillMaxSize()
                .background(defaultThemeColor.gradient)
        ) {

            item {
                Card(modifier = Modifier.padding(16.dp),elevation = 8.dp) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                    ) {
                        Log.d(TAG, "RecommendationUserScreen: ${user.photoUrl}")
                        val paint = rememberCoilPainter(request = user.photoUrl, requestBuilder = {
                            transformations(CircleCropTransformation())
                        }, fadeIn = true)
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),horizontalArrangement = Arrangement.Center) {
                            Image(
                                painter = paint,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)

                            )
                        }
                        recUser.intersections.forEach { item ->
                            when (item) {
                                is Intersection.FriendsIntersection -> {
                                    if(item.friends.isNotEmpty()){
                                        val friendsPagerState = rememberPagerState(pageCount = item.friends.size)
                                        ExpandableItem(label = resources.getQuantityString(
                                            R.plurals.same_friends,
                                            item.friends.size,
                                            item.friends.size
                                        )) {
                                            NewFriendsRow(pagerState = friendsPagerState, items = item.friends)
                                        }
                                    }
                                }
                                is Intersection.MusicIntersection -> {
                                    if(item.music.isNotEmpty()){
                                        ExpandableItem(
                                            label =  resources.getQuantityString(
                                                R.plurals.same_music,
                                                item.music.size,
                                                item.music.size
                                            )
                                        ){
                                            LazyRow(contentPadding = PaddingValues(start = 8.dp,end = 8.dp)) {
                                                items(item.music){ listItem ->
                                                    MusicItem(item = listItem)
                                                }
                                            }
                                        }
                                    }
                                }
                                is Intersection.SuggestionsIntersection -> {

                                }
                            }
                        }

                    }
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
//                            viewModel.goToGenres(genre)
                        }
                    }
                }
                items.ifListOf<RelatedFriend> { friends ->
                    item {
                        NewFriendsRow(pagerState = pagerState, items = friends)
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