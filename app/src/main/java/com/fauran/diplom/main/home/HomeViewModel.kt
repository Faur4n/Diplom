package com.fauran.diplom.main.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.fauran.diplom.TAG
import com.fauran.diplom.local.Preferences.FirebaseToken
import com.fauran.diplom.local.Preferences.SpotifyToken
import com.fauran.diplom.local.Preferences.updatePreferences
import com.fauran.diplom.models.*
import com.fauran.diplom.navigation.Nav
import com.fauran.diplom.network.SpotifyApi
import com.fauran.diplom.util.saveSpotifyToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.skydoves.sandwich.getOrNull
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.suspendOnSuccess
import com.spotify.sdk.android.authentication.AuthenticationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

sealed class HomeStatus {
    object Loading : HomeStatus()
    object FirstLaunch : HomeStatus()
    data class Data(val user: User) : HomeStatus()
    object NotAuthorized : HomeStatus()
}

val isSpotifyUser get() = Firebase.auth.currentUser?.uid?.startsWith("spotify") == true

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyApi : SpotifyApi
) : ViewModel() {

    private val _status = MutableLiveData<HomeStatus>(HomeStatus.Loading)
    val status: LiveData<HomeStatus> = _status
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var currentUser : User? = null
    init {
        isFirstLaunch()
    }

    private fun isFirstLaunch() {
        viewModelScope.launch {
            Log.d(TAG, "isFirstLaunch: ${auth.currentUser}")
            val uuid = auth.currentUser?.uid
            if (uuid == null) {
                _status.postValue(HomeStatus.NotAuthorized)
                return@launch
            }
            val user =
                db.collection("users/").whereEqualTo("gkey", uuid).get().await().toObjects<User>().firstOrNull()
            if (user == null) {
                _status.postValue(HomeStatus.FirstLaunch)
                createUserOnFirstLaunch(uuid)
            } else {
                currentUser = user
                _status.postValue(HomeStatus.Data(user))
            }
        }
    }

    private suspend fun createUserOnFirstLaunch(uuid: String) {
        val email = auth.currentUser?.email
        val displayName = auth.currentUser?.displayName
        val photo = "https:${auth.currentUser?.photoUrl?.encodedSchemeSpecificPart}"
        Log.d(TAG, "createUserOnFirstLaunch: $isSpotifyUser")
        val musicData = if(isSpotifyUser){
            getMusicData()
        }else{
            null
        }
        val user = User(
            gkey = uuid,
            email = email,
            photoUrl = photo,
            name = displayName,
            music = musicData
        )
        currentUser = user
        saveUser(user)
    }

    suspend fun getMusicData(): List<MusicData>? {
        return spotifyApi.getTopArtists().getOrNull()?.items?.map { it.mapToData() }
    }

    private suspend fun saveUser(user: User){
        kotlin.runCatching {
            db.collection("/users")
                .document(user.gkey.toString())
                .set(user)
                .await()
        }.onFailure {
            Log.d(TAG, "createUserOnFirstLaunch: ${it.message} $it")
            _status.postValue(HomeStatus.NotAuthorized)
        }.onSuccess {
            _status.postValue(HomeStatus.Data(user))
        }
    }

    fun connectSpotify(context: Context,response: AuthenticationResponse?) {
        if (response == null || response.type != AuthenticationResponse.Type.TOKEN) {
//            sendError("Bad spotify reponse")
            return
        }
        val token = response.accessToken
        if (token == null) {
//            sendError("Empty spotify token")
            return
        }
        Log.d(TAG, "authWithSpotifyToken: SPOTIFY TOKEN $token")
        viewModelScope.launch {
            saveSpotifyToken(context, token)
            spotifyApi.getMe().suspendOnSuccess {
                data?.let { me ->
                    saveSpotifyAccount(token,me)
                    Log.d(TAG, "authWithSpotifyToken: $me")
                }
            }.onError {
                Log.d(TAG, "authWithSpotifyToken: ${this.message()}")
            }
        }
    }

    private suspend fun saveSpotifyAccount(token: String,me : SpotifyMe){
        val musicData = getMusicData()
        val curUser = currentUser
        if(curUser != null){
            val accs = (curUser.accounts ?: emptyList()).toMutableList()
            val spotifyAccount = Account(
                type = ACC_TYPE_SPOTIFY,
                name = me.displayName,
                token = token,
                email = me.email,
                photoUrl = me.href
            )
            accs.add(spotifyAccount)
            val newUser = curUser.copy(
                accounts = accs,
                music = musicData
            )
            saveUser(newUser)
        }
    }

    fun logout(context: Context,navController: NavController?) {
        viewModelScope.launch {
            context.updatePreferences(SpotifyToken,"")
            context.updatePreferences(FirebaseToken,"")
            Firebase.auth.signOut()
            navController?.navigate(Nav.Auth.route) {
                popUpTo(Nav.Main.route) {
                    inclusive = true
                }
            }
        }
    }
}