package com.fauran.diplom.main.home.list_items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.white

@Composable
fun TextBlock(){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                8.dp
            )
    ) {
        Text(
            text = stringResource(id = R.string.hello),
            style = Typography.h3,
            color = white,
        )
        Text(
            text = stringResource(id = R.string.description),
            style = Typography.subtitle2,
            color = white,
        )
    }
}