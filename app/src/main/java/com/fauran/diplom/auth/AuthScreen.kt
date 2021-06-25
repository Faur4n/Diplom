package com.fauran.diplom.auth

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.defaultThemeColor
import com.fauran.diplom.ui.theme.white
import com.fauran.diplom.util.showToast
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay

@ExperimentalPagerApi
@Composable
fun DescriptionPagerItem(index: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .background(SolidColor(Color.Black), alpha = .6f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (icon, text) = when (index) {
            0 -> {
                Icons.Default.PrivacyTip to stringResource(id = R.string.pager_first)
            }
            1 -> {
                Icons.Default.Recommend to stringResource(id = R.string.pager_second)
            }
            else -> {
                Icons.Default.Star to stringResource(id = R.string.pager_first)
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Icon(icon, contentDescription = null, tint = Color.White)
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            style = Typography.body1.copy(textAlign = TextAlign.Center),
            color = Color.White
        )
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
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
                navController?.popBackStack()
                navController?.navigate(Nav.Main.route)
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
                    modifier = Modifier
                        .weight(2f)
                        .align(Alignment.CenterHorizontally),
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
                    brush = defaultThemeColor.gradient
                )
                .clipToBounds()
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceAround
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
            Text(
                text = stringResource(id = R.string.auth_title),
                style = TextStyle(fontFamily = FontFamily(Font(R.font.shrikhand_regular))),
                fontSize = 64.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer(
                        translationY = -translation.value,
                        alpha = alpha.value
                    )
            )
            Spacer(modifier = Modifier.size(32.dp))


            val pagerState = rememberPagerState(pageCount = 3, infiniteLoop = true)
            LaunchedEffect(key1 = pagerState.currentPage, block = {
                delay(3000)
                val nextPage = if (pagerState.currentPage == pagerState.pageCount - 1) {
                    0
                } else {
                    pagerState.currentPage + 1
                }
                pagerState.animateScrollToPage(nextPage)
            })
            Column(
                modifier = Modifier.graphicsLayer(
                    translationY = -translation.value,
                    alpha = alpha.value
                )
            ) {
                HorizontalPager(
                    state = pagerState, modifier = Modifier
                        .fillMaxWidth()
                ) { index ->
                    DescriptionPagerItem(
                        index
                    )
                }
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        translationY = translation.value,
                        alpha = alpha.value
                    ),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                GoogleSignInButton(
                    Modifier.align(Alignment.CenterHorizontally).shadow(8.dp),
                    onStart = {
                        viewModel.loading()
                    },
                    onResult = { acc ->
                        viewModel.authWithGoogleAccount(acc)

                    }
                )
                SpotifySignInButton(
                    Modifier.align(Alignment.CenterHorizontally).shadow(8.dp),
                    onResult = {
                        viewModel.authWithSpotifyToken(context, it)
                    },
                    onStart = {
                        viewModel.loading()
                    }
                )
                VkSignInButton(
                    Modifier.align(Alignment.CenterHorizontally).shadow(8.dp),
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
