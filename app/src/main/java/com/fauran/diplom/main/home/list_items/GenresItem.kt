package com.fauran.diplom.main.home.list_items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fauran.diplom.main.home.utils.Genre
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.black
import com.google.accompanist.flowlayout.FlowRow


@Composable
fun GenresItem(
    genres: List<Genre>,
    modifier: Modifier = Modifier,
    onItemClick : (Genre) -> Unit
) {
    FlowRow(
        crossAxisSpacing = 2.dp,
        mainAxisSpacing = 2.dp,
        modifier = modifier.fillMaxSize()
    ) {
        genres.take(25).forEach { genre ->
            Box(
                modifier = Modifier
                    .background(
                        genre.color,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        onItemClick(genre)
                    }
                    .padding(start = 16.dp,top = 8.dp , end = 16.dp , bottom = 8.dp)
            ) {
                Text(text = genre.name, style = Typography.subtitle1, color = black)
            }
        }
    }
}