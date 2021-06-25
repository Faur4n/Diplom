package com.fauran.diplom.main.home.list_items

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.auth.widgets.SpotifyButton
import com.fauran.diplom.auth.widgets.SpotifySignInButton
import com.fauran.diplom.auth.widgets.VkButton
import com.fauran.diplom.auth.widgets.VkSignInButton
import com.fauran.diplom.main.home.AvatarImage
import com.fauran.diplom.main.home.HomeViewModel
import com.fauran.diplom.models.User
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.black
import com.fauran.diplom.ui.theme.primaryDark
import com.fauran.diplom.ui.theme.white
import com.fauran.diplom.util.isSpotifyUser
import com.fauran.diplom.util.isVkUser

@Composable
fun RoundedRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
//            .background(white)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}


@ExperimentalMaterialApi
@Composable
fun UserItem(
    user: User?,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rootNavController = LocalRootNavController.current
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryDark),
            verticalAlignment = CenterVertically
        ) {
            AvatarImage(
                url = user?.photoUrl, modifier = Modifier
                    .padding(8.dp)
                    .size(64.dp)
            )
            Text(
                text = user?.name ?: stringResource(id = R.string.no_name),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                style = Typography.h5, color = white
            )
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = white,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        viewModel.logout(context, rootNavController)
                    }
            )
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val type = when {
                isSpotifyUser -> "Spotify"
                isVkUser -> "VK"
                else -> "Google"
            }
            Text(
                text = "Вы вошли через $type.\nВойдите через другие соцсети, чтобы получать больше рекомендаций.",
                color = white,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(SolidColor(black), alpha = .6f)
                    .padding(8.dp)
            )
            Column(modifier = Modifier.align(CenterHorizontally),verticalArrangement = Arrangement.spacedBy(8.dp)) {

                if (user?.isSpotifyEnabled == true) {
                    SpotifyButton(isActive = false)
                } else {
                    SpotifySignInButton(onResult = {
                        viewModel.connectSpotify(context, it)
                    }, onStart = {

                    })
                }
                if (user?.isVkEnabled == true) {
                    VkButton(isActive = false)
                } else {
                    VkSignInButton(onResult = {
                        viewModel.connectVk(context, it)
                    }, onStart = {

                    }, onError = {

                    })
                }
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun CardItem(
    user: User?,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val animatedProgress = remember { Animatable(initialValue = 0.8f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(300, easing = LinearEasing)
        )
    }
    Card(
        elevation = 8.dp,
        modifier = modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            .clip(RoundedCornerShape(10.dp))
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
            val type = when {
                isSpotifyUser -> "Spotify"
                isVkUser -> "VK"
                else -> "Google"
            }

            Text(text = "Вы вошли через $type.\nВойдите через другие соцсети, чтобы получать больше рекомендаций.")
            Spacer(modifier = Modifier.size(16.dp))
            if (user?.isSpotifyEnabled == true) {
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
                    Icon(painter = paint, contentDescription = "done", tint = Color.Green)
                }
            } else {
                SpotifySignInButton(onResult = {
                    viewModel.connectSpotify(context, it)
                }, onStart = {

                })
            }
            Spacer(modifier = Modifier.size(16.dp))
            if (user?.isVkEnabled == true) {
                Row() {
                    Row(modifier = Modifier.weight(1f)) {
                        Image(
                            painterResource(R.drawable.ic_vk_full_logo),
                            contentDescription = "spotify",
                            modifier = Modifier.height(32.dp)
                        )
                    }
                    val paint = rememberVectorPainter(image = Icons.Filled.Done)
                    Icon(painter = paint, contentDescription = "done", tint = Color.Green)
                }
            } else {
                VkSignInButton(onResult = {
                    viewModel.connectVk(context, it)
                }, onStart = {

                }, onError = {

                })
            }
        }
    }

}