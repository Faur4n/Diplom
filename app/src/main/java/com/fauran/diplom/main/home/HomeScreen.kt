package com.fauran.diplom.main.home

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.TAG
import com.fauran.diplom.main.home.colorAnimation.LocalThemeColors
import com.fauran.diplom.main.home.colorAnimation.animateGradient
import com.fauran.diplom.main.home.list_items.*
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.models.User
import com.fauran.diplom.navigation.LocalRootNavController
import com.fauran.diplom.ui.theme.defaultThemeColor


@ExperimentalFoundationApi
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
    val sections by remember(user) {
        mutableStateOf(user?.createSections(context) ?: emptyList())
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
    var currentSection by remember {
        mutableStateOf(
            sections.firstOrNull()
        )
    }

    val colors = animateGradient(currentSection) ?: defaultThemeColor

    CompositionLocalProvider(
        LocalThemeColors provides colors
    ) {
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
                    val state = rememberLazyListState()
                    LaunchedEffect(state.firstVisibleItemIndex, block = {
                        val item = state.layoutInfo.visibleItemsInfo.find { it.index == state.firstVisibleItemIndex }
                        val key = item?.key
                        if(key is String){
                            val id = key.substringBefore("|")
                            val section = sections.find { it.id == id }
                            if(currentSection?.id != section?.id){
                                currentSection = section
                            }
                        }
                    })
                    LazyColumn(
                        state = state,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .background(
                                brush = colors.gradient
                            )
                            .clipToBounds()
                            .fillMaxSize()
                    ) {
                        item {
                            CardItem(user = user,viewModel)
                        }
                        sections.forEach { section ->
                            stickyHeader(section.id + "|") {
                                ItemTitle(title = stringResource(section.title), icon = section.icon)
                            }
                            val items = section.items
                            when{
                                items.isNotEmpty() && items.first() is MusicData ->{
                                    item {
                                        MusicRow(
                                            items.map { it as MusicData }
                                        )
                                    }
                                }
                                items.isNotEmpty() && items.first() is Genre ->{
                                    item {
                                        GenresItem(genres = items.map { it as Genre })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



