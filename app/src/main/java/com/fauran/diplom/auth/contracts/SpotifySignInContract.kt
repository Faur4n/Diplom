package com.fauran.diplom.auth.contracts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.fauran.diplom.BuildConfig
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.authentication.LoginActivity
import com.spotify.sdk.android.authentication.LoginActivity.REQUEST_KEY
import com.spotify.sdk.android.authentication.AuthenticationClient

class SpotifySignInContract : ActivityResultContract<Int, AuthenticationResponse?>() {

    private val EXTRA_AUTH_REQUEST = "EXTRA_AUTH_REQUEST"

    override fun createIntent(context: Context, type: Int): Intent {
        val redirectUri =
            "${context.getString(R.string.com_spotify_sdk_redirect_scheme)}://${context.getString(R.string.com_spotify_sdk_redirect_host)}"
        val request =
            AuthenticationRequest.Builder(
                BuildConfig.SPORIFY_CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                redirectUri
            )
                .setScopes(arrayOf(
                    "user-read-recently-played",
                    "user-top-read",
                    "user-follow-read",
                    "user-read-email",
                    "user-library-read",
                    "user-read-private"
                ))
                .build()

        Log.d(TAG, "createIntent: ${request.clientId}")
        val bundle = Bundle()
        bundle.putParcelable(REQUEST_KEY, request)
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(EXTRA_AUTH_REQUEST, bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        return intent
    }


    override fun parseResult(resultCode: Int, result: Intent?): AuthenticationResponse? {

        return AuthenticationClient.getResponse(resultCode, result)
    }

}