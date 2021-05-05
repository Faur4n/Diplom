package com.fauran.diplom.main.home

import android.util.Log
import androidx.compose.animation.Animatable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.models.User
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.ui.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay

@Composable
fun animateGradient(page : Int): Int {
    return 0
}


@ExperimentalPagerApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val navController = LocalRootNavController.current
    val status by viewModel.status.observeAsState(viewModel.status.value ?: HomeStatus.Loading)
    var showLoading by remember {
        mutableStateOf(true)
    }
    var user by remember() {
        mutableStateOf<User?>(null)
    }
    val pages by remember(user) {
        mutableStateOf(user?.cretePages() ?: emptyList())
    }
    val pagerState = rememberPagerState(pageCount = pages.size)

    val listState = rememberLazyListState()


    var colors by remember{ mutableStateOf(purples)}
    LaunchedEffect(pagerState.currentPage){
        when(pagerState.currentPage){
            0 -> colors = purples
            1 -> colors = teals
            2 -> colors = greens
        }
    }
    val firstColor = remember {
        Animatable(colors[0])
    }

    val secondColor = remember {
        Animatable(colors[1])
    }
    val thirdColor = remember {
        Animatable(colors[2])
    }
    LaunchedEffect(pagerState.currentPage){
        firstColor.animateTo(colors[0])
    }
    LaunchedEffect(pagerState.currentPage){
        secondColor.animateTo(colors[1])
    }
    LaunchedEffect(pagerState.currentPage){
        thirdColor.animateTo(colors[2])
    }

    val currentGradient = remember(firstColor.value,secondColor.value,thirdColor.value){
        Brush.verticalGradient(
           colors = listOf(
               firstColor.value,
               secondColor.value,
               firstColor.value
           )
        )
    }




    LaunchedEffect(status) {
        Log.d(TAG, "HomeScreen: $status")
        when (val st = status) {
            is HomeStatus.Data -> {
                user = st.user
                showLoading = false
            }
            HomeStatus.FirstLaunch -> {
                showLoading = false
            }
            HomeStatus.Loading -> {
                showLoading = true
            }
            HomeStatus.NotAuthorized -> {
                viewModel.logout(context, navController)
            }
        }

    }

    Scaffold(drawerContent = {
        Button(onClick = {
            viewModel.logout(context, navController)
        }) {
            Text(text = "ВЫЙТИ")
        }
    }) {
        Crossfade(targetState = showLoading) { loading ->
            if (loading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.align(Center)) {
                        Text(
                            text = "Loading",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(CenterHorizontally)
                        )
                        LinearProgressIndicator(modifier = Modifier.align(CenterHorizontally))
                    }
                }
            } else {

                Column(
                    modifier = Modifier
                        .background(
                            brush = currentGradient
                        )
                        .clipToBounds()
                        .fillMaxSize()
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .padding(16.dp)
                            .shadow(32.dp)
                            .align(CenterHorizontally)
                    ) {
                        Column {
                            Spacer(modifier = Modifier.size(16.dp))
                            AvatarImage(
                                user?.photoUrl,
                                modifier = Modifier.align(CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                text = user?.name ?: stringResource(id = R.string.no_name),
                                textAlign = TextAlign.Center,
                                style = Typography.h6,
                                modifier = Modifier.align(CenterHorizontally)
                            )
                        }
                    }
                        RecommendationTabs(
                            pages = pages,
                            pagerState = pagerState,
                            listState = listState,
                            modifier = Modifier
                                .weight(2f)
                                .fillMaxSize()
                                .background(Color.Transparent)
                        )
                }
            }
        }
    }
}



