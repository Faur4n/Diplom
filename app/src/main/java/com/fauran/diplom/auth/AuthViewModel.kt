package com.fauran.diplom.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauran.diplom.TAG
import com.fauran.diplom.local.Preferences
import com.fauran.diplom.local.Preferences.updatePreferences
import com.fauran.diplom.main.vk_api.VkApi
import com.fauran.diplom.models.SpotifyMe
import com.fauran.diplom.network.SpotifyApi
import com.fauran.diplom.util.saveSpotifyToken
import com.fauran.diplom.util.saveVkToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.suspendOnSuccess
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.vk.api.sdk.auth.VKAccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject


sealed class AuthStatus {
    object Success : AuthStatus()
    data class Error(val message: String? = null) : AuthStatus()
    object NotAuthorized : AuthStatus()
    object Loading : AuthStatus()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val spotifyApi: SpotifyApi
) : ViewModel() {

    private val _signedIn = MutableLiveData<AuthStatus>(AuthStatus.NotAuthorized)
    val signedIn: LiveData<AuthStatus> = _signedIn

    private val gson = GsonBuilder().create()

    private suspend fun getFirebaseToken(me: SpotifyMe, accessToken: String, context: Context) {
        val data = hashMapOf(
            "email" to me.email,
            "user_id" to me.id,
            "username" to me.displayName,
            "token" to accessToken,
            "url" to me.images.firstOrNull()?.url
        )
        try {
            val response = Firebase.functions
                .getHttpsCallable("spotifyToToken")
                .call(data)
                .await()
            Log.d(TAG, "getFirebaseToken: $response")
            val responseMap =
                gson.fromJson<Map<String, String>>(response.data.toString(), Map::class.java)
            val fbToken = responseMap["token"]
            Log.d(TAG, "getFirebaseToken: ${fbToken}")
            if (fbToken != null) {
                context.updatePreferences(Preferences.FirebaseToken, fbToken)
                signInWithCustomToken(fbToken)
            } else {
                sendError("function don't return token")

            }
        } catch (th: Throwable) {
            sendError(th.message.toString())
        }
    }

    private fun signInWithCustomToken(token: String) {
        Firebase.auth.signInWithCustomToken(token).addOnSuccessListener {
            _signedIn.postValue(AuthStatus.Success)
        }.addOnFailureListener {
            Log.d(TAG, "signInWithCustomToken: $it")
            sendError(it.message.toString())
        }
    }

    fun loading() {
        _signedIn.postValue(AuthStatus.Loading)
    }


    fun authWithSpotifyToken(context: Context, response: AuthenticationResponse?) {
        if (response == null || response.type != AuthenticationResponse.Type.TOKEN) {
            sendError("Bad spotify reponse")
            return
        }
        val token = response.accessToken
        if (token == null) {
            sendError("Empty spotify token")
            return
        }
        Log.d(TAG, "authWithSpotifyToken: SPOTIFY TOKEN $token")
        viewModelScope.launch {
            saveSpotifyToken(context, token)
            spotifyApi.getMe().suspendOnSuccess {
                data?.let { me ->
                    Log.d(TAG, "authWithSpotifyToken: $me")
                    getFirebaseToken(me, token, context)
                }
            }.onError {
                Log.d(TAG, "authWithSpotifyToken: ${this.message()}")
                sendError(message())
            }
        }
    }

    fun authWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "authWithGoogleAccount: $account")
        val token = account?.idToken
        when {
            account == null || token == null -> {
                sendError("Wrong token")
            }
            else -> {
                val credential = GoogleAuthProvider.getCredential(token, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener {
                    _signedIn.postValue(AuthStatus.Success)
                }.addOnFailureListener {
                    sendError("Cant Authorize in Firebase")
                }
            }
        }
    }

    fun authWithVkToken(context: Context, token: VKAccessToken?) {
        if (token == null) {
            sendError("Bad vk token")
            return
        }
        val accessToken = token.accessToken

        viewModelScope.launch {
            saveVkToken(context = context, accessToken)
            val profile = VkApi.getVkProfile(token.userId)
            Log.d(TAG, "authWithVkToken: ${token.accessToken} ${token.phoneAccessKey}")
            val email = token.email
            val data = mutableMapOf<String, String>(
                "phone" to token.phone.toString(),
                "user_id" to token.userId.toString(),
                "username" to "${profile?.firstName} ${profile?.lastName}",
                "token" to accessToken,
                "url" to profile?.photo200Orig.toString()
            )
            if (email != null) {
                data["email"] = email
            }
            try {
                val response = Firebase.functions
                    .getHttpsCallable("vkToToken")
                    .call(data)
                    .await()
                Log.d(TAG, "authWithVkToken: ${response.data}")
                val responseMap =
                    gson.fromJson<Map<String, String>>(response.data.toString(), Map::class.java)
                val fbToken = responseMap["token"]
                Log.d(TAG, "authWithVkToken: ${fbToken}")
                if (fbToken != null) {
                    context.updatePreferences(Preferences.FirebaseToken, fbToken)
                    Log.d(TAG, "authWithVkToken: $fbToken")
                    signInWithCustomToken(fbToken)
                } else {
                    sendError("function don't return token")

                }
            } catch (th: Throwable) {
                sendError(th.message.toString())
            }
        }
    }


    fun sendError(msg: String) {
        Log.d(TAG, "sendError: $msg")
        _signedIn.postValue(AuthStatus.Error(msg))
        _signedIn.postValue(AuthStatus.NotAuthorized)
    }
}
