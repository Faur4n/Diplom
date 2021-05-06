package com.fauran.diplom.main.home.list_items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.fauran.diplom.main.home.colorAnimation.LocalThemeColors
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.ui.theme.Purple200
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.background
import com.fauran.diplom.ui.theme.white
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.flowlayout.FlowRow


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
                        val color = LocalThemeColors.current
                        Box(
                            modifier = Modifier
                                .background(
                                    color.light,
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
