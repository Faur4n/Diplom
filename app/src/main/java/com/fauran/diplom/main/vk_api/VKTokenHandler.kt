package com.fauran.diplom.main.vk_api

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.fauran.diplom.TAG
import com.fauran.diplom.auth.widgets.vkScopes
import com.fauran.diplom.main.home.HomeViewModel
import com.fauran.diplom.util.saveVkToken
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import kotlinx.coroutines.launch


@Composable
fun VKTokenHandler(viewModel: HomeViewModel) {
    val vkCallback = LocalVkCallback.current
    val activity = LocalContext.current as? ComponentActivity
    DisposableEffect(activity) {
        Log.d(TAG, "VKTokenHandler: THIS IS CALLED ")
        val handler = object : VKTokenExpiredHandler {
            override fun onTokenExpired() {
                Log.d(TAG, "onTokenExpired: EXPIRED")
                if (activity != null) {
                    VK.login(activity, vkScopes)
                    return
                }
            }
        }
        if (activity != null) {
            VK.addTokenExpiredHandler(handler)
            vkCallback.registerForCallback { token, errorCode ->
                if (token == null && errorCode != null) {
                    viewModel.removeVkAccount()
                }
                if (token != null && errorCode == null) {
                    viewModel.viewModelScope.launch {
                        saveVkToken(activity, token.accessToken)
                    }
                }
            }
        } else Log.d(TAG, "onTokenExpired: Bad Context NULL Activity")
        onDispose {
            VK.removeTokenExpiredHandler(handler)
        }
    }
}

