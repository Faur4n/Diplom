package com.fauran.diplom.auth.widgets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
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
import com.fauran.diplom.SPOTIFY_SIGN_IN
import com.fauran.diplom.auth.AuthViewModel
import com.fauran.diplom.auth.contracts.SpotifySignInContract
import com.fauran.diplom.ui.theme.googleText
import com.fauran.diplom.ui.theme.spotifyBlack
import com.fauran.diplom.ui.theme.spotifyGreen
import com.spotify.sdk.android.authentication.AuthenticationResponse


@Composable
fun SpotifySignInButton(
    modifier: Modifier = Modifier,
    onResult: (AuthenticationResponse?) -> Unit,
    onStart : () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(SpotifySignInContract()) { response ->
        onResult(response)
    }
    Card(
        backgroundColor = spotifyBlack,
        modifier = modifier
            .width(220.dp)
            .clickable {
                onStart()
                launcher.launch(SPOTIFY_SIGN_IN)
            }) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_spotify),
                contentDescription = stringResource(id = R.string.spotify_sign_in),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(id = R.string.spotify_sign_in),
                style = googleText,
                color = spotifyGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .align(Alignment.CenterVertically)

            )
        }
    }
}