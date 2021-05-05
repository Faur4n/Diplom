package com.fauran.diplom.main.home

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.models.PageData
import com.fauran.diplom.models.User
import com.fauran.diplom.ui.theme.Purple200
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.background
import com.fauran.diplom.ui.theme.white
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch


fun User.cretePages(): List<Page> {
    val pages = mutableListOf<Page>()
    val mMusic = music
    pages.add(
        Page(
            R.string.music_title,
            mMusic ?: emptyList()
        )
    )
    pages.add(
        Page(
            R.string.music_title,
            mMusic ?: emptyList()
        )
    )
    pages.add(
        Page(
            R.string.music_title,
            mMusic ?: emptyList()
        )
    )
    return pages
}

data class Page(
    @StringRes val title: Int,
    val data: List<PageData>
)


@ExperimentalPagerApi
@Composable
fun RecommendationTabs(
    pagerState: PagerState,
    pages: List<Page>,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()
    Column(modifier = modifier) {
        TabRow(
            // Our selected tab is our current page
            selectedTabIndex = pagerState.currentPage,
            // Override the indicator, using the provided pagerTabIndicatorOffset modifier
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    text = { Text(stringResource(page.title)) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        if (pagerState.currentPage == index) {
                            ///scroll to top
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.background(Color.Transparent)
        ) { page ->
            val data = pages[page].data
            if (data.isNotEmpty()) {
                LazyColumn(state = listState) {
                    items(pages[page].data) { item ->
                        Log.d(TAG, "RecommendationTabs: DRAW $item")
                        Spacer(modifier = Modifier.size(8.dp))
                        when (item) {
                            is MusicData -> {
                                MusicItem(data = item, modifier = Modifier.align(Center))
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(white)
                ) {
                    Text(
                        text = "Sorry empty recommendation",
                        modifier = Modifier.align(Center)
                    )
                }
            }
        }

    }
}

@Composable
fun MusicItem(data: MusicData, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .shadow(10.dp)
            .background(background)
    ) {
        val paint = rememberCoilPainter(
            request = data.imageUrl,
            requestBuilder = {
                transformations(CircleCropTransformation())
            }
        )
        Column(
            Modifier
                .weight(1f)
                .align(CenterVertically)
                .fillMaxHeight()
                .padding(start = 8.dp)
        ) {

            Spacer(
                modifier = Modifier
                    .size(70.dp)
            )
            Text(
                text = data.name.toString(),
                textAlign = TextAlign.Start,
                style = Typography.h5,
                modifier = Modifier
            )
            FlowRow(
                modifier = Modifier,
                crossAxisSpacing = 2.dp
            ) {
                data.genres?.let { list ->
                    for ((index, genre) in list.withIndex()) {
                        if (index >= 1) {
                            Spacer(modifier = Modifier.size(8.dp))
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    Purple200,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(4.dp)
                        ) {
                            Text(text = genre, style = Typography.subtitle1, color = white)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
        }
        Spacer(modifier = Modifier.size(8.dp))
        Image(
            painter = paint,
            contentDescription = data.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .align(CenterVertically)
        )
        Spacer(modifier = Modifier.size(8.dp))
    }
}
