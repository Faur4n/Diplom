package com.fauran.diplom.main.home.list_items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fauran.diplom.main.home.Genre
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.spotifyBlack
import com.google.accompanist.flowlayout.FlowRow


@Composable
fun GenresItem(
    genres: List<Genre>
) {
    FlowRow(
        crossAxisSpacing = 2.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        genres.take(25).forEach { genre ->
            Box(
                modifier = Modifier
                    .background(
                        genre.color,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(start = 8.dp,top = 4.dp , end = 8.dp , bottom = 4.dp)
            ) {
                Text(text = genre.name, style = Typography.subtitle1, color = spotifyBlack)
            }
        }
    }

}