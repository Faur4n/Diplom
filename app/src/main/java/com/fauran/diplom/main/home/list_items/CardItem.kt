package com.fauran.diplom.main.home.list_items

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.auth.widgets.SpotifySignInButton
import com.fauran.diplom.auth.widgets.VkSignInButton
import com.fauran.diplom.main.home.widgets.AvatarImage
import com.fauran.diplom.main.home.HomeViewModel
import com.fauran.diplom.main.home.LocalSpotifyEnabled
import com.fauran.diplom.main.home.LocalVkEnabled
import com.fauran.diplom.models.User
import com.fauran.diplom.ui.theme.Green500
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.util.isSpotifyUser
import com.fauran.diplom.util.isVkUser

@Composable
fun CardItem(
    user: User?,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val spotifyConnected  = LocalSpotifyEnabled.current

    val vkConnected = LocalVkEnabled.current
    val animatedProgress = remember { Animatable(initialValue = 0.8f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(300, easing = LinearEasing)
        )
    }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .shadow(32.dp)
            .graphicsLayer(scaleY = animatedProgress.value, scaleX = animatedProgress.value)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row() {
                Text(
                    text = user?.name ?: stringResource(id = R.string.no_name),
                    textAlign = TextAlign.Start,
                    style = Typography.h6,
                    modifier = Modifier
                        .weight(1f)
                        .align(CenterVertically)
                )
                AvatarImage(
                    user?.photoUrl,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            val type = when{
                isSpotifyUser -> "Spotify"
                isVkUser -> "VK"
                else -> "Google"
            }

            Text(text = "Вы вошли через $type.\nВойдите через другие соцсети, чтобы получать больше рекомендаций.")
            Spacer(modifier = Modifier.size(16.dp))
            if (spotifyConnected) {
                Row() {
                    Row(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Spotify", modifier = Modifier
                                .align(CenterVertically), fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Image(
                            painterResource(R.drawable.ic_spotify),
                            contentDescription = "spotify",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    val paint = rememberVectorPainter(image = Icons.Filled.Done)
                    Icon(painter = paint, contentDescription = "done", tint = Green500)
                }
            } else {
                SpotifySignInButton(onResult = {
                    viewModel.connectSpotify(context, it)
                }, onStart = {

                })
            }
            Spacer(modifier = Modifier.size(16.dp))
            if (vkConnected) {
                Row() {
                    Row(modifier = Modifier.weight(1f)) {
                        Image(
                            painterResource(R.drawable.ic_vk_full_logo),
                            contentDescription = "spotify",
                            modifier = Modifier.height(32.dp)
                        )
                    }
                    val paint = rememberVectorPainter(image = Icons.Filled.Done)
                    Icon(painter = paint, contentDescription = "done", tint = Green500)
                }
            } else {
                VkSignInButton(onResult = {
                    viewModel.connectVk(context, it)
                }, onStart = {

                },onError = {

                })
            }
        }
    }
}