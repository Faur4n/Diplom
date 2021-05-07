package com.fauran.diplom.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.fauran.diplom.models.ThemeColor

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)

val Teal200 = Color(0xFFc3fdff)
val Teal500 = Color(0xFF90caf9)
val Teal700 = Color(0xFF5d99c6)

val Green200 = Color(0xFF98ee99)
val Green500 = Color(0xFF66bb6a)
val Green700 = Color(0xFF338a3e)

val PurpleGradient = Brush.verticalGradient(
    colors = listOf(
        Purple200,
        Purple500,
        Purple700
    )
)

val defaultThemeColor = ThemeColor(
    Purple200,
    Purple500,
    Purple700,
    Brush.verticalGradient(
        colors = listOf(
            Purple200,
            Purple500,
            Purple700,
        )
    )
)




val primary = Color(0xFF7b1fa2)
val primaryDark = Color(0xFF4a0072)
val accent = Color(0xFFae52d4)
val background = Color(0xFFf5f5f5)
val backgroundGray = Color(0xFFe0e0e0)
val white = Color(0xFFffffff)
val spotifyBlack = Color(0xFF191414)
val spotifyGreen = Color(0xFF1db954)