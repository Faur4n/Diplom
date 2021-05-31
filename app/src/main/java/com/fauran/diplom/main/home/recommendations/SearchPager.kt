package com.fauran.diplom.main.home.recommendations

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.paging.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.search.helper.deserialize
import com.algolia.search.model.response.ResponseSearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
public fun <T : Any> SearcherSingleIndexPager<T>.collectAsSearcherLazyPaging(
    state: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope()
): SearcherLazyPaging<T> {
    val pagingItems = flow.collectAsLazyPagingItems()
    val searcherLazyPaging = SearcherLazyPaging(pagingItems, state, scope)
    onSearcherChange { searcherLazyPaging.resetAsync() }
    return searcherLazyPaging
}

public data class SearcherLazyPaging<T : Any>(
    public val pagingItems: LazyPagingItems<T>,
    public val listState: LazyListState,
    public val scope: CoroutineScope
) {

    /**
     * Resets the lazy paging component content and state.
     * This is done typically by refreshing the content and scrolling to the top.
     */
    public suspend fun reset() {
        pagingItems.refresh()
        listState.scrollToItem(0)
    }

    /**
     * Resets the lazy paging component content and state.
     * This is done typically by refreshing the content and scrolling to the top.
     */
    public fun resetAsync() {
        scope.launch { reset() }
    }
}

public class SearcherSingleIndexPager<T : Any>(
    searcher: SearcherSingleIndex,
    pagingConfig: PagingConfig = PagingConfig(pageSize = 10),
    transformer: suspend (ResponseSearch) -> List<T>
) : SearcherPager<T> {

    override val flow: Flow<PagingData<T>> = Pager(pagingConfig) {
        SearcherSingleIndexPagingSource(
            searcher = searcher,
            transformer = transformer
        )
    }.flow

    private var searcherChangeCallback: (() -> Unit)? = null

    override fun notifySearcherChanged() {
        searcherChangeCallback?.invoke()
    }

    internal fun onSearcherChange(callback: () -> Unit) {
        this.searcherChangeCallback = callback
    }
}

class SearcherSingleIndexPagingSource<T : Any>(
    private val searcher: SearcherSingleIndex,
    private val transformer: suspend (ResponseSearch) -> List<T>,

) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int {
        return 0 // on refresh (for new query), start from the first page (number zero)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val pageNumber = params.key ?: 0
            searcher.query.page = pageNumber
            searcher.query.hitsPerPage = params.loadSize
            val response = search()
            val data = transformer(response)
            val nextKey = if (pageNumber < response.nbPages) pageNumber + 1 else null
            LoadResult.Page(
                data = data,
                prevKey = null, // no paging backward
                nextKey = nextKey
            )
        } catch (throwable: Throwable) {
            LoadResult.Error(throwable)
        }
    }

    private suspend fun search(): ResponseSearch {
        try {
            searcher.isLoading.value = true
            val response = searcher.search()
            withContext(searcher.coroutineScope.coroutineContext) {
                searcher.response.value = response
                searcher.isLoading.value = false
            }
            return response
        } catch (throwable: Throwable) {
            withContext(searcher.coroutineScope.coroutineContext) {
                searcher.error.value = throwable
                searcher.isLoading.value = false
            }
            throw throwable
        }
    }
}
@Composable
internal fun SearchColors(): TextFieldColors {
    return TextFieldDefaults.textFieldColors(
        backgroundColor = MaterialTheme.colors.background,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
    )
}

/**
 * Pager for Search.
 */
public interface SearcherPager<T : Any> {

    /**
     * A cold Flow of PagingData, which emits new instances of PagingData once they become invalidated.
     */
    public val flow: Flow<PagingData<T>>

    /**
     * Notify searcher's configuration has changed.
     */
    public fun notifySearcherChanged()
}

@ExperimentalAnimationApi
@Composable
public fun SearchBox(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    onValueChange: (String, Boolean) -> Unit = { _, _ -> },
    query: MutableState<String> = mutableStateOf(""),
    colors: TextFieldColors = SearchColors()
) {
    val text = rememberSaveable { query }
    Card(modifier = modifier, elevation = 4.dp) {
        TextField(
            value = text.value,
            textStyle = textStyle.merge(TextStyle(textDecoration = TextDecoration.None)),
            onValueChange = {
                val isSubmit = it.endsWith("\n")
                val value = if (isSubmit) it.removeSuffix("\n") else it
                text.value = value
                onValueChange(value, isSubmit)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground
                )
            },
            placeholder = {
                Text(
                    text = "hint",
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.2f)
                )
            },
            trailingIcon = {
                val visible = query.value.isNotEmpty()
                SearchClearIcon(visible) {
                    text.value = ""
                    onValueChange("", false)
                }
            },
            maxLines = 1,
            colors = colors,
        )
    }
}
@ExperimentalAnimationApi
@Composable
internal fun SearchClearIcon(
    visible: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth / 3 },
            animationSpec = tween(100, easing = LinearOutSlowInEasing)
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth / 3 },
            animationSpec = tween(100, easing = LinearOutSlowInEasing)
        ) + fadeOut()
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}