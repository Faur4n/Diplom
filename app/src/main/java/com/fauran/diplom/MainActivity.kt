package com.fauran.diplom

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.fauran.diplom.navigation.Navigation
import com.fauran.diplom.ui.theme.DiplomTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiConfig
import com.vk.api.sdk.auth.VKAuthCallback
import dagger.hilt.android.AndroidEntryPoint


val LocalGoogleSignInClient = staticCompositionLocalOf<GoogleSignInClient?> { null }

val LocalVkCallback = staticCompositionLocalOf { VKCallback() }

class VKCallback() {

    var callback: VKAuthCallback? = null

    fun registerForCallback(callback: VKAuthCallback) {
        this.callback = callback
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    val callback: VKCallback = VKCallback()

    @ExperimentalFoundationApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VK.setConfig(VKApiConfig(this,lang = "ru",appId = BuildConfig.VK_APP_ID))

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            CompositionLocalProvider(
                LocalGoogleSignInClient provides googleSignInClient,
                LocalVkCallback provides callback
            ) {
                DiplomTheme {
                    // A surface container using the 'background' color from the theme
                    Navigation()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val c = callback.callback
        if(c != null)
            VK.onActivityResult(requestCode, resultCode, data, c)
    }
}

