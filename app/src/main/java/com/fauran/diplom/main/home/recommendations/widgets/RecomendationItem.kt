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
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun RecommendationItem(
    recUser: RecommendationUser?,
    modifier: Modifier = Modifier
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
                                modifier = Modifier.padding(start = 8.dp)
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
            TextButton(onClick = {
                Toast.makeText(context, "ПОДРОБНЕЕ", Toast.LENGTH_SHORT).show()
            }, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Подробнее")
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
        modifier = modifier
    ) {
        items(music) {
            val paint =
                rememberCoilPainter(request = it.imageUrl, requestBuilder = {
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
    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.same_friends, friends.size),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy((-16).dp)) {
            friends.take(3).forEach {
                val paint =
                    rememberCoilPainter(request = it.photo, requestBuilder = {
                        transformations(CircleCropTransformation())
                    })
                Image(
                    painter = paint,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        if (friends.size > 3) {
            Text(text = "...")
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