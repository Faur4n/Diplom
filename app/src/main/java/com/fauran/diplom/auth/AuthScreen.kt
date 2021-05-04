package com.fauran.diplom.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.fauran.diplom.R
import com.fauran.diplom.auth.widgets.GoogleSignInButton
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.navigation.Nav
import com.fauran.diplom.ui.theme.Purple200
import com.fauran.diplom.ui.theme.Purple500
import com.fauran.diplom.ui.theme.Purple700
import com.fauran.diplom.ui.theme.Typography
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AuthScreen(viewModel: AuthViewModel) {

    val navController = LocalRootNavController.current
    val loginErrorString = stringResource(R.string.login_error)
    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val onError: () -> Unit = {
        scope.launch {
            state.snackbarHostState.showSnackbar(loginErrorString)
        }
    }
    val onSuccess: () -> Unit = {
        navController?.navigate(Nav.Main.route) {
            popUpTo(Nav.Auth.route) {
                inclusive = true
            }
        }
    }

    Scaffold(
        scaffoldState = state
        ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Purple200,
                            Purple500,
                            Purple700
                        )
                    )
                )
                .clipToBounds()
                .padding(8.dp)
        ) {
            val translation = remember { Animatable(initialValue = 600f) }
            LaunchedEffect(Unit) {
                translation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
            }
            val alpha = remember { Animatable(initialValue = 0f) }
            LaunchedEffect(Unit) {
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
                    text = stringResource(id = R.string.auth_title)
                        .capitalize(Locale.getDefault()),
                    style = Typography.h3,
                    color = Color.White,
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
                    GoogleSignInButton(
                        Modifier.align(Alignment.Center),
                        onSuccess = onSuccess,
                        onError = onError
                    )
                }
            }
        }
    }
}
