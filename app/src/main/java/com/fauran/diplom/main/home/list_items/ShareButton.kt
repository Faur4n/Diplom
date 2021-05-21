package com.fauran.diplom.main.home.list_items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.ui.theme.spotifyBlack
import com.fauran.diplom.ui.theme.white

@Composable
fun ShareButton(
    modifier: Modifier = Modifier,
    onShareClicked: () -> Unit
) {
    Card(backgroundColor = white, modifier = modifier.clickable {
        onShareClicked()
    }) {
        Text(
            text = stringResource(id = R.string.share_you_interests),
            color = spotifyBlack,
            modifier = Modifier.padding(16.dp)
        )
    }

}