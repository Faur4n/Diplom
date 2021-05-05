package com.fauran.diplom.network

import android.content.Context
import android.util.Log
import com.fauran.diplom.local.Preferences
import com.fauran.diplom.local.Preferences.getPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class SpotifyAuthInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = runBlocking {
            context.getPreferences(Preferences.SpotifyToken).firstOrNull()
        }
        return if (token != null) {

            val request = chain.request().newBuilder()
                .addHeader("Accept","application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $token")
                .build()
            val response = chain.proceed(request)
            Log.d("okhttp", response.code.toString())
            response
        } else {
            Response.Builder().code(401).message("spotify token is null").build()
        }

    }

}