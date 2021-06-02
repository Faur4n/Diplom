package com.fauran.diplom.main.home.recommendations

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.items
import coil.transform.CircleCropTransformation
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.main.home.recommendations.models.RecommendationUser
import com.fauran.diplom.main.home.recommendations.widgets.RecommendationItem
import com.fauran.diplom.main.home.utils.Shimmer
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.models.User
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun RecommendationScreen(
    user: User,
    onSearchChanged: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = viewModel<RecommendationViewModel>(factory = RecommendationViewModelFactory(user))

    LaunchedEffect(Unit) {
        viewModel.getCategories(user)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.recommendations)) },
                elevation = 8.dp,
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
    ) {
        val searcherLazyPaging = viewModel.hitsPager.collectAsSearcherLazyPaging()
        Column() {
            RecommendationList(
                me = user,
                modifier = Modifier.fillMaxSize(),
                searcherLazyPaging = searcherLazyPaging
            )
        }
    }
}


@Composable
fun RecommendationList(
    me: User,
    modifier: Modifier = Modifier,
    searcherLazyPaging: SearcherLazyPaging<RecommendationUser>
) {
    val (data, state) = searcherLazyPaging

    LazyColumn(modifier, state) {
        items(data) { item ->
            RecommendationItem(recUser = item)
        }
    }
}

