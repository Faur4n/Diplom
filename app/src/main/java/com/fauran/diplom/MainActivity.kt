package com.fauran.diplom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.fauran.diplom.ui.theme.DiplomTheme
import dagger.hilt.android.AndroidEntryPoint
import com.fauran.diplom.navigation.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


val LocalGoogleSignInClient = staticCompositionLocalOf<GoogleSignInClient?> { null }


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            CompositionLocalProvider(
                LocalGoogleSignInClient provides googleSignInClient
            ) {
                DiplomTheme {
                    // A surface container using the 'background' color from the theme
                    Navigation()
                }
            }
        }
    }
}

