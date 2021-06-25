package com.fauran.diplom.main.home.recommendations.widgets

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.fauran.diplom.R
import com.fauran.diplom.main.home.recommendations.models.Intersection
import com.fauran.diplom.main.home.recommendations.models.RecommendationUser
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.models.RelatedFriend
import com.fauran.diplom.models.Suggestion
import com.fauran.diplom.ui.theme.Typography
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun RecommendationItem(
    recUser: RecommendationUser?,
    modifier: Modifier = Modifier,
    onDetailsClick : (isUser : String) -> Unit,
) {
    val context = LocalContext.current

    Card(
        Modifier.padding(8.dp),
    ) {
        Column {
            ///shimmer
            Row(Modifier.padding(8.dp)) {
                Text(
                    modifier = modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .weight(1f),
                    text = recUser?.user?.name.toString(),
                    style = MaterialTheme.typography.h5
                )
                val avatarPaint =
                    rememberCoilPainter(
                        request = recUser?.user?.photoUrl ?: R.drawable.ic_image,
                        requestBuilder = {
                            transformations(CircleCropTransformation())
                        }
                    )
                Image(
                    painter = avatarPaint,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))
            val intersections = recUser?.intersections
            intersections?.forEach { intersection ->
                when (intersection) {
                    is Intersection.FriendsIntersection -> {
                        if (intersection.friends.isNotEmpty())
                            SameFriendsRow(
                                friends = intersection.friends,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            )
                    }
                    is Intersection.MusicIntersection -> {
                        if (intersection.music.isNotEmpty())
                            SameMusicRow(music = intersection.music, Modifier.fillMaxWidth())
                    }
                    is Intersection.SuggestionsIntersection -> {
                        if (intersection.suggestions.isNotEmpty())
                            SameSuggestionRow(
                                suggestions = intersection.suggestions,
                                modifier = Modifier.padding(start = 8.dp)
                            )

                    }
                }
            }
            TextButton(
                onClick = {
                    recUser?.user?.gkey?.let {
                        onDetailsClick(it)
                    }
                }, modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp)
            ) {
                Text(text = stringResource(id = R.string.more), style = Typography.button,modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun SameMusicRow(
    music: List<MusicData>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy((-16).dp),
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        item{
            Text(text = stringResource(id = R.string.same_music,music.size),modifier = Modifier.fillMaxWidth()
                .padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.size(32.dp))

        }
        items(music) {
            val paint =
                rememberCoilPainter(request = it.imageUrl?.firstOrNull(), requestBuilder = {
                    transformations(CircleCropTransformation())
                })
            Image(
                painter = paint,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun SameFriendsRow(
    friends: List<RelatedFriend>,
    modifier: Modifier = Modifier
) {

    val resources = LocalContext.current.resources
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy((-16).dp),
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        item{
            Text(text = resources.getQuantityString(R.plurals.same_friends,friends.size,friends.size),modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.size(32.dp))
        }
        items(friends) { item ->
            val paint =
                rememberCoilPainter(request = item.photo, requestBuilder = {
                    transformations(CircleCropTransformation())
                })
            Image(
                painter = paint,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun SameSuggestionRow(
    suggestions: List<Suggestion>,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.same_suggestion, suggestions.size),
            modifier = Modifier.weight(1f)
        )
    }
}