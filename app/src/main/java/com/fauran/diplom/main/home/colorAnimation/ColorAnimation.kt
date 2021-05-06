package com.fauran.diplom.main.home.colorAnimation

import androidx.compose.animation.Animatable
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.fauran.diplom.main.home.Section
import com.fauran.diplom.ui.theme.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController


data class ThemeColor(
    val light: Color,
    val medium: Color,
    val dark: Color,
    val gradient: Brush
)

val LocalThemeColors = compositionLocalOf {
    ThemeColor(
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
}

@Composable
fun animateGradient(section: Section?): ThemeColor? {
    if (section == null){
        return null
    }

    var colors by remember { mutableStateOf(section.colors) }
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(section) {
        colors = section.colors
    }
    val firstColor = remember {
        Animatable(colors.light)
    }
    val secondColor = remember {
        Animatable(colors.medium)
    }
    val thirdColor = remember {
        Animatable(colors.dark)
    }
    LaunchedEffect(section) {
        firstColor.animateTo(colors.light)
    }
    LaunchedEffect(section) {
        secondColor.animateTo(colors.medium)
    }
    LaunchedEffect(section) {
        thirdColor.animateTo(colors.dark)
    }

    val currentColors = remember(firstColor.value, secondColor.value, thirdColor.value) {
        systemUiController.setSystemBarsColor(
            firstColor.value
        )

        ThemeColor(
            firstColor.value,
            secondColor.value,
            thirdColor.value,
            Brush.verticalGradient(
                colors = listOf(
                    firstColor.value,
                    secondColor.value,
                    firstColor.value
                )
            )
        )
    }

    return currentColors

}



