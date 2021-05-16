package com.fauran.diplom.main.vk_api

import androidx.compose.runtime.staticCompositionLocalOf
import com.vk.api.sdk.auth.VKAuthCallback

val LocalVkCallback = staticCompositionLocalOf { VKCallback() }

class VKCallback() {

    var callback: VKAuthCallback? = null

    fun registerForCallback(callback: VKAuthCallback) {
        this.callback = callback
    }
}