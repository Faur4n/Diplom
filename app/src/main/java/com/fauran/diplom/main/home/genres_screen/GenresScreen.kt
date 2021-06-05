package com.fauran.diplom.main.home.genres_screen

import android.content.Intent
import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.transform.CircleCropTransformation
import com.fauran.diplom.R
import com.fauran.diplom.main.home.HomeViewModel
import com.fauran.diplom.main.home.list_items.ItemTitle
import com.fauran.diplom.models.SpotifyArtist
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.defaultThemeColor
import com.fauran.diplom.ui.theme.spotifyBlack
import com.fauran.diplom.ui.theme.white
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.flow.filterNotNull
import soup.compose.material.motion.MaterialFadeThrough
import java.util.*


@Composable
fun GenresScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {

    val targetState by viewModel.genreState.filterNotNull()
        .collectAsState(initial = viewModel.genreState.replayCache.firstOrNull())
    val listState = rememberLazyListState()


    val dispatcher = LocalOnBackPressedDispatcherOwner.current

    val navState = navController.currentBackStackEntryAsState()
    val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.consumeGenreState(navController)
        }
    }

    dispatcher?.onBackPressedDispatcher?.addCallback(backCallback)
    DisposableEffect(navState) {
        onDispose {
            backCallback.remove()
        }
    }

    MaterialFadeThrough(targetState = targetState) { state ->
        if (state == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Center))
            }
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = state.genre.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            })
                        },
                        elevation = 8.dp,
                        navigationIcon = {
                            IconButton(onClick = {
                                dispatcher?.onBackPressedDispatcher?.onBackPressed()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(defaultThemeColor.gradient)
                ) {
                    item {
                        ItemTitle(
                            title = stringResource(id = R.string.genres_screen_title),
                            icon = null
                        )
                    }
                    items(state.artists) { artist ->
                        ArtistItem(artist = artist, modifier = Modifier.fillMaxWidth())
                    }
                }
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
            fadeIn = true,
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
