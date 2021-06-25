package com.fauran.diplom.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.fauran.diplom.R
import com.fauran.diplom.ui.theme.black
import com.google.accompanist.coil.rememberCoilPainter


@Composable
fun AvatarImage(url: String?, modifier: Modifier = Modifier) {
    val avatarPainter = rememberCoilPainter(
        request = url,
        previewPlaceholder = R.drawable.ic_user,
        fadeIn = true,
        requestBuilder = {
            this.transformations(CircleCropTransformation())
        }
    )
    Image(
        painter = avatarPainter, contentDescription = "avatar", modifier = modifier
            .clip(CircleShape)                       // clip to the circle shape
            .border(2.dp, black, CircleShape)
    )
}