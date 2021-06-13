package com.fauran.diplom

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.fauran.diplom.main.home.utils.LocationBus
import com.fauran.diplom.main.home.utils.ContextBus
import com.fauran.diplom.main.vk_api.LocalVkCallback
import com.fauran.diplom.main.vk_api.VKCallback
import com.fauran.diplom.navigation.Navigation
import com.fauran.diplom.ui.theme.DiplomTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


val LocalGoogleSignInClient = staticCompositionLocalOf<GoogleSignInClient?> { null }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val callback: VKCallback = VKCallback()

    private val contextGetter = {
        this
    }
    private var locationProvider: FusedLocationProviderClient? = null

    private val locationGetter = {
        locationProvider
    }


    @ExperimentalCoroutinesApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextBus.addToastContextHandler(contextGetter)
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        LocationBus.addLocationHandler(locationGetter)
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
        VK.onActivityResult(requestCode, resultCode, data, callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        ContextBus.removeToastContextHandler(contextGetter)
        LocationBus.removeLocationHandler(locationGetter)
    }
}

