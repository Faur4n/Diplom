package com.fauran.diplom.main.home.list_items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.unit.dp
import com.fauran.diplom.models.RelatedFriend
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlin.math.absoluteValue


@ExperimentalPagerApi
@Composable
fun FriendsRow(
    friends: List<RelatedFriend>
) {
    val pagerState = rememberPagerState(friends.size)
    HorizontalPager(state = pagerState,
        offscreenLimit  = 1,
        modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)) { page ->
        Card(
            Modifier
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
                }
        ) {
            val friend = friends[page]
            val paint = rememberCoilPainter(
                request = friend.photo,
            )
            Image(
                painter = paint,
                contentDescription = friend.lastName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .fillMaxSize()
            )
            // Card content
        }
    }

}
