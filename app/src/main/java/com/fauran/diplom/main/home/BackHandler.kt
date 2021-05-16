package com.fauran.diplom.main.home

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
fun BackHandler(onBack: () -> Unit) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
    }

    DisposableEffect(dispatcher) {
        dispatcher?.onBackPressedDispatcher?.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}