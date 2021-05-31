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
import com.fauran.diplom.main.home.utils.Shimmer
import com.fauran.diplom.models.MusicData
import com.fauran.diplom.models.User
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecData(
    @SerialName("objectID")
    val id: String,
//    val music : List<MusicData>,
//    @SerialName("searchable_friends")
//    val friends : List<SearchableFriend>,
//    @SerialName("searchable_suggestions")
//    val suggestions : List<SearchableSuggestion>,
)

@Serializable
data class Category(
    val name: String,
    val screenName: String,
    val subs: List<SubCategory>
)

@Serializable
data class SubCategory(
    val name: String,
    val screenName: String
)

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun RecommendationScreen(
    user: User,
    onSearchChanged: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = viewModel<RecommendationViewModel>()

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
        val scope = rememberCoroutineScope()
//        val searchBox = viewModel.searchBox
        val searcherLazyPaging = viewModel.hitsPager.collectAsSearcherLazyPaging()
//            .map { pagingData ->
//            val newList = mutableListOf<User>()
//            pagingData.map{
//                Log.d(TAG, "RecommendationScreen: $it")
//                val user = viewModel.getUser(it.id)
//                if (user != null) {
//                    newList.add(user)
//                }
//            }
//            PagingData.from(newList)
//        }
//            .collectAsLazyPagingItems()
        val listState = rememberLazyListState()
        Column() {
//            SearchBox(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(12.dp),
//                query = searchBox.query,
//                onValueChange = { query, isSubmit ->
////                    searchBox.onValueChange(query, isSubmit)
//                    searcherLazyPaging.resetAsync()
//                }
//            )
            RecommendationList(
                me = user,
                modifier = Modifier.fillMaxSize(),
                searcherLazyPaging = searcherLazyPaging,
                viewModel = viewModel
            )
        }
//
//        val (searchString, changeSearchString) = remember { mutableStateOf(TextFieldValue("")) }
//        LazyColumn(
//            state = listState,
//            contentPadding = PaddingValues(8.dp),
//            modifier = Modifier
//                .fillMaxSize()
//                .background(defaultThemeColor.gradient)
//        ) {
//
//            stickyHeader {
//                OutlinedTextField(
//                    value = searchString, singleLine = true,
//                    onValueChange = {
//                        changeSearchString(it)
//                    },
//
//                    modifier = Modifier.fillMaxWidth()
//                )
//                LazyRow() {
//
//                }
//            }
//        }
    }
}


@Composable
fun RecommendationList(
    me: User,
    modifier: Modifier = Modifier,
    searcherLazyPaging: SearcherLazyPaging<User>,
    viewModel: RecommendationViewModel
) {
    val (data, state) = searcherLazyPaging
    val (isLoading, setIsLoading) = remember { mutableStateOf(false) }
    LaunchedEffect(data) {
//           viewModel.loadUsers(data.snapshot().items)
    }
    LazyColumn(modifier, state) {
        items(data) { item ->
            val intersection = remember(item) {
                item?.findIntersection(me)
            }
            Card(
                Modifier.padding(8.dp),
            ) {
                Column(
                    Modifier.padding(8.dp),
                ) {

                    ///shimmer
                    if (isLoading) {
                        Shimmer() {
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .background(Color.Blue)
                            ) {
                                Column(
                                    Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                ) {
                                    Row() {
                                        Box(modifier = Modifier.size(128.dp))
                                        Text(
                                            text = "",
                                            modifier
                                                .weight(1f)
                                                .height(32.dp)
                                        )
                                    }
                                    Row {
                                        //intesections
                                        Box(modifier.fillMaxWidth()) {
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            modifier = modifier
                                .fillMaxWidth(),
                            text = item?.name.toString(),
                            style = MaterialTheme.typography.body1
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy((-16).dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(intersection?.music ?: emptyList()) {
                                val paint =
                                    rememberCoilPainter(request = it.imageUrl, requestBuilder = {
                                        transformations(CircleCropTransformation())
                                    })
                                Image(
                                    painter = paint,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


fun User.findIntersection(user: User): Intersection {
    val music = user.music?.intersect(
        music ?: emptyList()
    )?.take(6)
    Log.d(TAG, "findIntersection: $music")
    return Intersection(music = music?.toList() ?: emptyList())
}

data class Intersection(
    val music: List<MusicData> = emptyList()
)