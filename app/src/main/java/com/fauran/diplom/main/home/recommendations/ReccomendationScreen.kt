package com.fauran.diplom.main.home.recommendations

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.items
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.main.LocalMainNavController
import com.fauran.diplom.main.home.HomeViewModel
import com.fauran.diplom.main.home.recommendations.models.RecommendationUser
import com.fauran.diplom.main.home.recommendations.widgets.RecommendationItem
import com.fauran.diplom.models.User
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import soup.compose.material.motion.MaterialFadeThrough


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun RecommendationScreen(
    mainViewModel: HomeViewModel,
) {

    val userFlow =
        mainViewModel.state.map { it.user }.filterNotNull()

    val targerUser by userFlow.collectAsState(null)

    val viewModel =
        viewModel<RecommendationViewModel>(factory = RecommendationViewModelFactory(targerUser))
    val searcherLazyPaging = viewModel.hitsPager.collectAsSearcherLazyPaging()

    LaunchedEffect(Unit) {
        viewModel.getCategories(targerUser)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.recommendations)) },
                elevation = 8.dp,
            )
        },
    ) {
        MaterialFadeThrough(targetState = targerUser) { user ->
            if(user == null || searcherLazyPaging.pagingItems.itemCount == 0){
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }else{
                Column() {
                    RecommendationList(
                        me = user,
                        modifier = Modifier.fillMaxSize(),
                        searcherLazyPaging = searcherLazyPaging,
                        onDetailsClick = {
                            Log.d(TAG, "RecommendationScreen: $it")
                        }
                    )
                }
            }
        }


    }
}


@Composable
fun RecommendationList(
    me: User,
    modifier: Modifier = Modifier,
    searcherLazyPaging: SearcherLazyPaging<RecommendationUser>,
    onDetailsClick: (User) -> Unit,
) {
    val (data, state) = searcherLazyPaging

    LazyColumn(modifier, state) {
        items(data) { item ->
            RecommendationItem(recUser = item){ id ->
                item?.user?.let { it -> onDetailsClick(it) }
            }
        }
    }
}

