package com.fauran.diplom.main.home.recommendations.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.unit.dp
import com.fauran.diplom.models.RelatedFriend
import com.google.accompanist.pager.*
import kotlin.math.absoluteValue

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun NewFriendsRow(
    pagerState: PagerState,
    items : List<RelatedFriend>,
    modifier: Modifier = Modifier
){
    HorizontalPager(state = pagerState,modifier = modifier
        .fillMaxWidth()
        .padding(top = 8.dp, bottom = 8.dp),itemSpacing = 16.dp) { page ->
        val listItem = items[page]
        FriendsItem(item = listItem,
            Modifier
                .width(300.dp)
                .graphicsLayer {
                    // Calculate the absolute offset for the current page from the
                    // scroll position. We use the absolute value which allows us to mirror
                    // any effects for both directions
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                    // We animate the scaleX + scaleY, between 85% and 100%
                    lerp(
                        start = ScaleFactor(0.85f, 0.85f),
                        stop = ScaleFactor(1f, 1f),
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale.scaleX
                        scaleY = scale.scaleY
                    }

                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = ScaleFactor(0.5f, 0.5f),
                        stop = ScaleFactor(1f, 1f),
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).scaleY
                })
    }
    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
        HorizontalPagerIndicator(
            pagerState = pagerState,
        )
    }
    Spacer(modifier = Modifier.size(8.dp))
}