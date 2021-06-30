package com.fauran.diplom.main.home.recommendations

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.items
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.main.home.HomeViewModel
import com.fauran.diplom.main.home.recommendations.models.RecommendationUser
import com.fauran.diplom.main.home.recommendations.widgets.RecommendationItem
import com.fauran.diplom.models.User
import com.fauran.diplom.navigation.Screens
import com.fauran.diplom.ui.theme.defaultThemeColor
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import soup.compose.material.motion.MaterialFadeThrough


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun RecommendationScreen(
    navController: NavController,
    mainViewModel: HomeViewModel,
) {
    val userFlow =
        mainViewModel.state.map { it.user }.filterNotNull()
    val targetUser = runBlocking {
        userFlow.first()
    }
    val viewModel =
        viewModel<RecommendationViewModel>(factory = RecommendationViewModelFactory(targetUser))
    val searcherLazyPaging = viewModel.hitsPager.collectAsSearcherLazyPaging()

    LaunchedEffect(Unit) {
        viewModel.getCategories(targetUser)
    }
    val oldUsers by mainViewModel.recUsers.collectAsState(initial = mainViewModel.recUsers.replayCache.firstOrNull() ?: emptyList() )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.recommendations)) },
                elevation = 8.dp,
            )
        },modifier = Modifier.background(defaultThemeColor.gradient)
    ) {
        MaterialFadeThrough(targetState = targetUser) { user ->
            Log.d(
                TAG,
                "RecommendationScreen: ${searcherLazyPaging.pagingItems.itemCount} ${oldUsers.isEmpty()}"
            )
            if (searcherLazyPaging.pagingItems.itemCount == 0 && oldUsers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                Column() {
                    RecommendationList(
                        modifier = Modifier.fillMaxSize(),
                        searcherLazyPaging = searcherLazyPaging,
                        oldItems = oldUsers,
                        onDetailsClick = {
                            mainViewModel.currentRecUser = it
                            navController.navigate(Screens.RecUserScreen.route)
                            Log.d(TAG, "RecommendationScreen: $it")
                        },
                        onSave = {
                            mainViewModel.saveRecUsers(it)
                        }
                    )
                }
            }
        }


    }
}


@Composable
fun RecommendationList(
    modifier: Modifier = Modifier,
    searcherLazyPaging: SearcherLazyPaging<RecommendationUser>,
    oldItems : List<RecommendationUser>,
    onDetailsClick: (RecommendationUser) -> Unit,
    onSave: (List<RecommendationUser>) -> Unit
) {
    val (data, state) = searcherLazyPaging
    Log.d(TAG, "RecommendationList: ${oldItems.size} ---- ${data.itemCount}" )
    LaunchedEffect(key1 = data, block = {
        val items = data.snapshot().items
        if(items.isNotEmpty())
            onSave(items)
    })
    if(data.itemCount != 0){
        LazyColumn(modifier, state) {
            items(data) { item ->
                RecommendationItem(recUser = item) { id ->
                    item?.let { it -> onDetailsClick(it) }
                }
            }
        }
    }else{
        LazyColumn(modifier, state) {
            items(oldItems) { item ->
                RecommendationItem(recUser = item) { id ->
                    onDetailsClick(item)
                }
            }
        }
    }

}

