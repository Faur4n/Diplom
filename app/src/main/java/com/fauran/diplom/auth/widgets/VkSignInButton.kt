package com.fauran.diplom.auth.widgets

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.main.vk_api.LocalVkCallback
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

    Card(
        modifier = modifier
            .width(220.dp),
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

        }) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_vk_compact_logo),
                contentDescription = stringResource(id = R.string.vk_sign_in),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(id = R.string.vk_sign_in),
                style = googleText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .align(Alignment.CenterVertically)

            )
        }
    }
}