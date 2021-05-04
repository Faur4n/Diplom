package com.fauran.diplom.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.fauran.diplom.ui.theme.Typography

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    Scaffold(
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            val translation = remember { Animatable(initialValue = 600f) }
            LaunchedEffect(Unit) {
                translation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
            }
            val alpha = remember { Animatable(initialValue = 0f) }
            LaunchedEffect(Unit){
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .graphicsLayer(
                        translationY = -translation.value,
                        alpha = alpha.value
                    )


            ) {
                Text(
                    text = "Title",
                    style = Typography.h3,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
            Column(
                modifier = Modifier
                    .weight(2f)
                    .graphicsLayer(
                        translationY = translation.value,
                        alpha = alpha.value
                    )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = "This is Auth Screen", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}