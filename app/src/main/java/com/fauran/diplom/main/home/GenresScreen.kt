package com.fauran.diplom.main.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.fauran.diplom.R
import com.fauran.diplom.main.home.list_items.ItemTitle
import com.fauran.diplom.models.SpotifyArtist
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.defaultThemeColor
import com.fauran.diplom.ui.theme.spotifyBlack
import com.fauran.diplom.ui.theme.white
import com.google.accompanist.coil.rememberCoilPainter
import java.util.*


@Composable
fun GenresScreen(
    genre: Genre,
    artists: List<SpotifyArtist>,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = genre.name.capitalize(Locale.getDefault())) },
                elevation = 8.dp,
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                modifier = Modifier.background(genre.color)
            )
        }
    ) {
        val state = rememberLazyListState()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .background(defaultThemeColor.gradient)
        ) {
            item {
                ItemTitle(title = stringResource(id = R.string.genres_screen_title), icon = null)
            }
            items(artists) { artist ->
                ArtistItem(artist = artist, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun ArtistItem(artist: SpotifyArtist, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(SolidColor(spotifyBlack), alpha = 0.5f)
            .clickable {
                val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(artist.uri))
                context.startActivity(launcher)
            }
            .padding(start = 8.dp, end = 8.dp)
    ) {
        val paint = rememberCoilPainter(
            request = artist.images?.firstOrNull()?.url,
            previewPlaceholder = R.drawable.ic_image,
            requestBuilder = {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_image)
            }
        )

        Text(
            text = artist.name.toString(),
            textAlign = TextAlign.Start,
            style = Typography.subtitle1,
            color = white,
            modifier = Modifier
                .align(CenterVertically)
                .weight(1f)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Image(
            painter = paint,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(128.dp)
                .padding(16.dp)
                .align(CenterVertically)
        )
    }
}
