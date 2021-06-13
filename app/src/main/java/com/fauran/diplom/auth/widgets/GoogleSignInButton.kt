package com.fauran.diplom.auth.widgets

import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.fauran.diplom.GOOGLE_SIGN_IN
import com.fauran.diplom.R
import com.fauran.diplom.auth.contracts.GoogleSignInContract
import com.fauran.diplom.ui.theme.googleText
import com.google.android.gms.auth.api.signin.GoogleSignInAccount


@ExperimentalMaterialApi
@Composable
fun GoogleSignInButton(
    modifier: Modifier,
    onResult: (GoogleSignInAccount?) -> Unit,
    onStart: () -> Unit,

    ) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(GoogleSignInContract()) { account ->
        onResult(account)
    }
    Card(
        modifier = modifier
            .width(220.dp),
        onClick = {
            onStart()
            launcher.launch(GOOGLE_SIGN_IN)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = stringResource(id = R.string.google_sign_in),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(id = R.string.google_sign_in),
                style = googleText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .align(Alignment.CenterVertically)

            )
        }
    }
}