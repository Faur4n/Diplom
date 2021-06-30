package com.fauran.diplom.main.home.list_items

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.black
import com.fauran.diplom.ui.theme.white
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun MusicItem(
    item: MusicData,
    modifier: Modifier = Modifier,
) {
    val paint = rememberCoilPainter(
        request = item.imageUrl?.firstOrNull(),
        previewPlaceholder = R.drawable.ic_image
    )
    val animatedProgress = remember { Animatable(initialValue = 0.8f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(300, easing = LinearEasing)
        )
    }
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(14.dp)
            .shadow(8.dp)
            .width(150.dp)
            .graphicsLayer(scaleY = animatedProgress.value, scaleX = animatedProgress.value)
            .clickable {
                val uri = item.uri
                if (uri != null) {
                    val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    context.startActivity(launcher)
                }
            }

    ) {
        Image(
            painter = paint,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .fillMaxSize()
                .align(Alignment.Center)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = SolidColor(black), alpha = 0.5f)
                .padding(10.dp)
                .align(Alignment.BottomEnd)
        ) {
            Text(
                style = Typography.subtitle2,
                text = item.name.toString(),
                textAlign = TextAlign.Center,
                color = white,
                maxLines = 2,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }
    }
}