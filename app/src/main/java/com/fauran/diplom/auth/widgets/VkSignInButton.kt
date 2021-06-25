package com.fauran.diplom.auth.widgets

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.main.vk_api.LocalVkCallback
import com.fauran.diplom.ui.theme.black
import com.fauran.diplom.ui.theme.googleText
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKScope

val vkScopes = listOf(VKScope.FRIENDS, VKScope.WALL, VKScope.AUDIO, VKScope.STATS)

@ExperimentalMaterialApi
@Composable
fun VkSignInButton(
    modifier: Modifier = Modifier,
    onResult: (VKAccessToken?) -> Unit,
    onStart: () -> Unit,
    onError: (Int) -> Unit,

    ) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val callback = LocalVkCallback.current

    VkButton(
        modifier = modifier,
        isActive = true,
        onClick = {
            onStart()
            if (activity != null) {
                VK.login(
                    activity,
                    vkScopes
                )
                callback.registerForCallback { token: VKAccessToken?, error: Int? ->
                    if (token == null && error != null) {
                        onError(error)
                        return@registerForCallback
                    }
                    onResult(token)
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
fun VkButton(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isActive: Boolean = true
) {
    Card(
        modifier = modifier
            .width(220.dp), onClick = {
            if (isActive) {
                onClick?.invoke()
            }
        }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_vk_compact_logo),
                contentDescription = stringResource(id = R.string.spotify_sign_in),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(
                    id =
                    if (isActive)
                        R.string.vk_sign_in
                    else R.string.vk_sign_in_yes
                ),
                style = googleText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )
            if (!isActive) {
                Icon(
                    Icons.Filled.Done,
                    contentDescription = "done",
                    tint = Color.Green,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
