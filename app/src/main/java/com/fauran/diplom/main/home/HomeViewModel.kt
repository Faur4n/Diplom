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
import com.fauran.diplom.models.SpotifyImage
import com.fauran.diplom.models.User
import com.fauran.diplom.navigation.Nav
import com.fauran.diplom.network.SpotifyApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.skydoves.sandwich.getOrNull
import com.skydoves.sandwich.getOrThrow
import com.skydoves.sandwich.onSuccess
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


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyApi : SpotifyApi
) : ViewModel() {

    private val _status = MutableLiveData<HomeStatus>(HomeStatus.Loading)
    val status: LiveData<HomeStatus> = _status
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    val isSpotifyUser = auth.currentUser?.uid?.startsWith("spotify") == true

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
            spotifyApi.getTopArtists().getOrNull()?.items?.map { it.mapToData() }
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
        kotlin.runCatching {
            db.collection("/users")
                .document(uuid)
                .set(user)
                .await()
        }.onFailure {
            Log.d(TAG, "createUserOnFirstLaunch: ${it.message} $it")
            _status.postValue(HomeStatus.NotAuthorized)
        }.onSuccess {
            _status.postValue(HomeStatus.Data(user))
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