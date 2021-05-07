package com.fauran.diplom.main.home.list_items

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.models.RelatedFriend
import com.fauran.diplom.ui.theme.Typography
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
    val initialPage = remember(friends) { if (friends.isNotEmpty() && friends.size >= 3) 2 else 0 }
    val pagerState =
        rememberPagerState(friends.size, initialOffscreenLimit = 5, initialPage = initialPage)
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) { page ->
        val friend = friends[page]
        val paint = rememberCoilPainter(
            request = friend.photo,
            requestBuilder = {
                placeholder(R.drawable.ic_image)
                transformations(CircleCropTransformation())
            }
        )
        val city = remember(friend) { friend.city }
        val country = remember(friend) { friend.country }
        val context = LocalContext.current
        val interests = remember(friend){ friend.interests }
        Card(
            elevation = 32.dp,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(250.dp)
                .clipToBounds()
                .clickable {
                    val id = friend.id
                    Log.d(TAG, "FriendsRow: $id")
                    if (id != null) {
                        val launcher = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("vkontakte://profile/${id}")
                        )
                        context.startActivity(launcher)
                    }
                }
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
            Column(
                modifier = Modifier
                    .align(Center)
            ) {
                Spacer(modifier = Modifier.size(8.dp))

                Image(
                    painter = paint,
                    contentDescription = friend.lastName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .align(CenterHorizontally)
                        .shadow(32.dp, CircleShape)
                        .fillMaxSize()
                )
                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "${friend.firstName} ${friend.lastName}",
                    textAlign = TextAlign.Center,
                    style = Typography.subtitle1,
                    modifier = Modifier.align(CenterHorizontally)
                )

                if (city != null) {
                    Spacer(modifier = Modifier.size(8.dp))
                    val text = remember(city, country) {
                        if (country != null)
                            "$country, $city"
                        else city
                    }
                    Text(
                        text = text, textAlign = TextAlign.Center,
                        style = Typography.subtitle2,
                        modifier = Modifier.align(CenterHorizontally)
                    )
                }
                if (interests != null) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = interests, textAlign = TextAlign.Center,
                        style = Typography.subtitle2,
                        modifier = Modifier.align(CenterHorizontally)
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
            }

        }
        // Card content
    }
}
