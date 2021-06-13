package com.fauran.diplom.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val json = Json { }

@Serializable
data class ListState(
    val initialFirstVisibleItemIndex: Int = 0,
    val initialFirstVisibleItemScrollOffset: Int = 0
)

@Composable
fun rememberLazyListStateSavable(): LazyListState {
    val listSaver: Saver<MutableState<ListState>, out Any> = Saver(
        save = {
            json.encodeToString(it.value)
        },
        restore = { mutableStateOf(json.decodeFromString(it)) }
    )
    val (listStateSaver, saveListState) = rememberSaveable(saver = listSaver) {
        mutableStateOf(ListState())
    }
    val listState = rememberLazyListState(
        listStateSaver.initialFirstVisibleItemIndex,
        listStateSaver.initialFirstVisibleItemScrollOffset
    )

    LaunchedEffect(listState) {
        saveListState(
            ListState(
                listState.firstVisibleItemIndex,
                listState.firstVisibleItemScrollOffset
            )
        )
    }
    return listState
}
