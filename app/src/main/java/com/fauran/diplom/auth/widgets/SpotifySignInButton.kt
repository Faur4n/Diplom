package com.fauran.diplom.auth.widgets

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.SPOTIFY_SIGN_IN
import com.fauran.diplom.auth.contracts.SpotifySignInContract
import com.fauran.diplom.ui.theme.black
import com.fauran.diplom.ui.theme.googleText
import com.fauran.diplom.ui.theme.spotifyGreen
import com.spotify.sdk.android.authentication.AuthenticationResponse


@ExperimentalMaterialApi
@Composable
fun SpotifySignInButton(
    modifier: Modifier = Modifier,
    onResult: (AuthenticationResponse?) -> Unit,
    onStart: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(SpotifySignInContract()) { response ->
        onResult(response)
    }
    SpotifyButton(modifier,isActive = true, onClick = {
        onStart()
        launcher.launch(SPOTIFY_SIGN_IN)
    })
}

@ExperimentalMaterialApi
@Composable
fun SpotifyButton(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isActive: Boolean = true
) {
    Card(
        backgroundColor = black,
        modifier = modifier
            .width(220.dp), onClick = {
            if(isActive){
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
                painter = painterResource(id = R.drawable.ic_spotify),
                contentDescription = stringResource(id = R.string.spotify_sign_in),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(
                    id =
                    if (isActive)
                        R.string.spotify_sign_in
                    else R.string.spotify_sign_in_yes
                ),
                style = googleText,
                color = spotifyGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )
            if (!isActive) {
                Icon(Icons.Filled.Done, contentDescription = "done", tint = Color.Green,modifier = Modifier.size(24.dp))
            }
        }
    }
}