package com.fauran.diplom.auth

import android.util.Log
import android.widget.Space
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.auth.widgets.GoogleSignInButton
import com.fauran.diplom.auth.widgets.SpotifySignInButton
import com.fauran.diplom.auth.widgets.VkSignInButton
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.navigation.Nav
import com.fauran.diplom.ui.theme.*
import com.fauran.diplom.util.showToast
import java.util.*

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val context = LocalContext.current
    val navController = LocalRootNavController.current
    val loginErrorString = stringResource(R.string.login_error)
    val state = rememberScaffoldState()
    val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.sign_in_animation) }
    val animationState = rememberLottieAnimationState(autoPlay = true, repeatCount = Int.MAX_VALUE)
    val signedIn by viewModel.signedIn.observeAsState(
        viewModel.signedIn.value ?: AuthStatus.NotAuthorized
    )
    var showLoadingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(signedIn) {
        Log.d(TAG, "AuthScreen: $signedIn")
        when (val status = signedIn) {
            is AuthStatus.Error -> {
                context.showToast(status.message.toString())
                state.snackbarHostState.showSnackbar(loginErrorString)
            }
            AuthStatus.NotAuthorized -> {
                showLoadingDialog = false
            }
            AuthStatus.Success -> {
                navController?.navigate(Nav.Main.route) {
                    popUpTo(Nav.Main.route) {
                        inclusive = true
                    }
                }
            }
            AuthStatus.Loading -> {
                showLoadingDialog = true
            }
        }
    }
    if (showLoadingDialog) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Column(
                modifier = Modifier
                    .size(280.dp)
                    .background(white)
            ) {
                Text(
                    text = "Пробуем вас авторизовать",
                    textAlign = TextAlign.Center,
                    style = Typography.h6,
                    modifier = Modifier
                        .align(
                            Alignment.CenterHorizontally
                        )
                        .weight(1f)
                )
                LottieAnimation(
                    animationSpec,
                    modifier = Modifier.weight(2f).align(Alignment.CenterHorizontally),
                    animationState = animationState
                )
            }
        }
    }
    Scaffold(
        scaffoldState = state
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = PurpleGradient
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
                    text = stringResource(id = R.string.auth_title).toUpperCase(Locale.getDefault()),
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
                    .weight(1f)
                    .fillMaxWidth()
                    .graphicsLayer(
                        translationY = translation.value,
                        alpha = alpha.value
                    )
            ) {
                GoogleSignInButton(
                    Modifier.align(Alignment.CenterHorizontally),
                    onStart = {
                        viewModel.loading()
                    },
                    onResult = { acc ->
                        viewModel.authWithGoogleAccount(acc)

                    }
                )
                Spacer(modifier = Modifier.size(32.dp))
                SpotifySignInButton(
                    Modifier.align(Alignment.CenterHorizontally),
                    onResult = {
                        viewModel.authWithSpotifyToken(context, it)
                    },
                    onStart = {
                        viewModel.loading()
                    }
                )
                Spacer(modifier = Modifier.size(32.dp))
                VkSignInButton(
                    Modifier.align(Alignment.CenterHorizontally),
                    onResult = {
                            viewModel.authWithVkToken(context, it)
                    },
                    onStart = {
                        viewModel.loading()
                    },
                    onError = { code ->
                        viewModel.sendError("VK LOGIN ERROR CODE $code")
                    }
                )
            }
        }
    }
}
