package com.fauran.diplom.main.home.list_items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.spotifyBlack
import com.fauran.diplom.ui.theme.spotifyGreen
import com.fauran.diplom.ui.theme.white

@Composable
fun ShareButton(
    shared: Boolean,
    modifier: Modifier = Modifier,
    onButtonClicked : () -> Unit,
) {
    Card(backgroundColor = white, modifier = modifier.padding(8.dp), elevation = 30.dp) {
        Column() {
            Text(
                text = if (shared) stringResource(id = R.string.interests_title) else stringResource(
                    id = R.string.share_you_interests
                ),
                color = if(shared) spotifyGreen else spotifyBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            OutlinedButton(
                onClick = {
                    onButtonClicked()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (shared) stringResource(id = R.string.interests_btn_title) else stringResource(
                        id = R.string.share
                    ), style = Typography.subtitle1
                )
            }
        }
    }

}