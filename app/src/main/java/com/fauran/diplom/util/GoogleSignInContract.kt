package com.fauran.diplom.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.fauran.diplom.BuildConfig
import com.fauran.diplom.TAG
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class GoogleSignInContract(private val context: Context) : ActivityResultContract<Int, GoogleSignInAccount?>() {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(
            BuildConfig.GOOGLE_WEB_SERVER_API_KEY
        )
        .requestEmail()
        .build()

    override fun createIntent(context: Context, type: Int) : Intent {

        val client = GoogleSignIn.getClient(context,gso)

        return client.signInIntent
    }


    override fun parseResult(resultCode: Int, result: Intent?): GoogleSignInAccount? {

        val task = GoogleSignIn.getSignedInAccountFromIntent(result)
        val res = try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
//            firebaseAuthWithGoogle(account.idToken!!)
            account
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            null
        }

        return res
    }
}