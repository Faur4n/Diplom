package com.fauran.diplom.main.home.list_items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.fauran.diplom.ui.theme.*

@Composable
fun ItemTitle(
    title: String,
    icon: ImageVector?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryDark)
            .padding(16.dp)
    ) {

        Text(
            style = Typography.h6,
            text = title,
            modifier = Modifier
                .weight(1f)
                .align(CenterVertically),
            color = white
        )
        if (icon != null) {
            val paint = rememberVectorPainter(image = icon)
            Icon(
                painter = paint,
                contentDescription = "icon",
                tint = white,
                modifier = Modifier.align(CenterVertically)
            )
        }
    }
}