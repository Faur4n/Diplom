package com.fauran.diplom.main.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauran.diplom.TAG
import com.fauran.diplom.local.Preferences.FirebaseToken
import com.fauran.diplom.local.Preferences.SpotifyToken
import com.fauran.diplom.local.Preferences.updatePreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

sealed class HomeStatus {
    object Loading : HomeStatus()
    object FirstLaunch : HomeStatus()
    object Data : HomeStatus()
    object NotAuthorized : HomeStatus()
}

data class User(
    @get:PropertyName("gkey")
    @set:PropertyName("gkey")
    var gkey: String? = null,
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String? = null,
    @get:PropertyName("photo_url")
    @set:PropertyName("photo_url")
    var photoUrl: String? = null,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _status = MutableLiveData<HomeStatus>(HomeStatus.Loading)
    val status: LiveData<HomeStatus> = _status
    private val db = Firebase.firestore
    private val auth = Firebase.auth

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
                db.collection("users/").whereEqualTo("gkey", uuid).get().await().toObjects<User>()
            if (user.isEmpty()) {
                _status.postValue(HomeStatus.FirstLaunch)
                createUserOnFirstLaunch(uuid)
            } else {
                _status.postValue(HomeStatus.Data)
            }
        }
    }

    private suspend fun createUserOnFirstLaunch(uuid: String) {
        val email = auth.currentUser?.email
        val displayName = auth.currentUser?.displayName
        val photo = "https:${auth.currentUser?.photoUrl?.encodedSchemeSpecificPart}"

        val user = User(
            gkey = uuid,
            email = email,
            photoUrl = photo,
            name = displayName
        )
        kotlin.runCatching {
            db.collection("/users")
                .document(uuid)
                .set(user)
                .await()
        }.onFailure {
            _status.postValue(HomeStatus.NotAuthorized)
        }.onSuccess {
            _status.postValue(HomeStatus.Data)
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            val isSpotifyUser = auth.currentUser?.uid?.startsWith("spotify") == true
            context.updatePreferences(SpotifyToken,"")
            context.updatePreferences(FirebaseToken,"")
            Firebase.auth.signOut()
            _status.postValue(HomeStatus.NotAuthorized)
        }
    }
}