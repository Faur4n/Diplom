package com.fauran.diplom.main.home.list_items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.main.home.colorAnimation.LocalThemeColors
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.spotifyBlack
import com.fauran.diplom.ui.theme.white
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun MusicRow(
    items: List<MusicData>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.background(Color.Transparent)
    ) {
        items(items) { item ->
            RowMusicItem(item = item)
        }
    }
}

@Composable
fun RowMusicItem(
    item: MusicData
) {
    val paint = rememberCoilPainter(
        request = item.imageUrl,
    )
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(150.dp)
    ) {
        Image(
            painter = paint,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = SolidColor(spotifyBlack), alpha = 0.5f)
                .padding(10.dp)
                .align(Alignment.BottomEnd)
        ) {
            Text(style = Typography.subtitle2,text = item.name.toString(),textAlign = TextAlign.Center, color = white, maxLines = 2,modifier = Modifier.align(
                Alignment.Center))
        }
    }
}