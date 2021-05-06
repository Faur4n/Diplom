package com.fauran.diplom.main.home.list_items

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.fauran.diplom.main.home.colorAnimation.LocalThemeColors
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.white

@Composable
fun ItemTitle(
    title : String,
    icon : ImageVector
){
    val colors = LocalThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.light)
            .padding(16.dp)
    ) {
        val paint = rememberVectorPainter(image = icon)
        Text(style = Typography.h6,text = title,modifier = Modifier.weight(1f).align(CenterVertically),color = white)
        Icon(painter = paint, contentDescription = "icon",tint = white,modifier = Modifier.align(CenterVertically))
    }
}