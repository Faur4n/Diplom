package com.fauran.diplom.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.fauran.diplom.models.ThemeColor

val primary = Color(0xffb388ff)
val primaryLight = Color(0xffe7b9ff)
val primaryDark = Color(0xff805acb)
val accent = primaryLight
val background = Color(0xFFf5f5f5)

val backgroundGray = Color(0xFFe0e0e0)
val white = Color(0xFFffffff)
val black = Color(0xFF191414)
val spotifyGreen = Color(0xFF1db954)
val opposite = Color(0xFFcba55a)


val defaultThemeColor = ThemeColor(
    primaryLight,
    primary,
    primaryDark,
    Brush.verticalGradient(
        colors = listOf(
            primaryDark,
            primary,
            primaryLight,
        )
    )
)
