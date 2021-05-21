package com.fauran.diplom.main.vk_api

import androidx.compose.runtime.staticCompositionLocalOf
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback

val LocalVkCallback = staticCompositionLocalOf { VKCallback() }

class VKCallback() : VKAuthCallback {

    private var callback : ((token: VKAccessToken?, errorCode: Int?) -> Unit)? = null

    fun registerForCallback(onLogin : (token: VKAccessToken?,errorCode: Int?) -> Unit) {
        callback = onLogin
    }

    override fun onLogin(token: VKAccessToken) {
        callback?.invoke(token,null)
        callback = null
    }

    override fun onLoginFailed(errorCode: Int) {
        callback?.invoke(null,errorCode)
        callback = null
    }
}