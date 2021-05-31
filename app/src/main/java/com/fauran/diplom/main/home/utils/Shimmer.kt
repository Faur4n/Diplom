package com.fauran.diplom.main.home.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.shimmer.ShimmerFrameLayout

@Composable
fun Shimmer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val shimmer = remember {
        ShimmerFrameLayout(context).apply {
            addView(ComposeView(context).apply {
                setContent(content)
            })
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { shimmer }
    ) { it.startShimmer() }
}