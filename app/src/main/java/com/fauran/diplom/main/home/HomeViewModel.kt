package com.fauran.diplom.main.home

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.fauran.diplom.SPOTIFY_SIGN_IN
import com.fauran.diplom.TAG
import com.fauran.diplom.VKCallback
import com.fauran.diplom.VK_SIGN_IN
import com.fauran.diplom.auth.contracts.SpotifySignInContract
import com.fauran.diplom.auth.widgets.vkScopes
import com.fauran.diplom.local.Preferences.FirebaseToken
import com.fauran.diplom.local.Preferences.SpotifyToken
import com.fauran.diplom.local.Preferences.VKToken
import com.fauran.diplom.local.Preferences.updatePreferences
import com.fauran.diplom.main.VkApi
import com.fauran.diplom.main.VkApi.toRelatedFriends
import com.fauran.diplom.models.*
import com.fauran.diplom.navigation.Nav
import com.fauran.diplom.network.SpotifyApi
import com.fauran.diplom.util.isSpotifyUser
import com.fauran.diplom.util.isVkUser
import com.fauran.diplom.util.saveSpotifyToken
import com.fauran.diplom.util.saveVkToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.skydoves.sandwich.*
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
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
    private val spotifyApi: SpotifyApi
) : ViewModel() {

    private val _status = MutableLiveData<HomeStatus>(HomeStatus.Loading)
    val status: LiveData<HomeStatus> = _status
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var currentUser: User? = null
    private var userListener: ListenerRegistration? = null

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _isVkEnabled = MutableLiveData<Boolean>(false)
    val isVkEnabled: LiveData<Boolean> = _isVkEnabled

    private val _isSpotifyEnabled = MutableLiveData<Boolean>(false)
    val isSpotifyEnabled: LiveData<Boolean> = _isSpotifyEnabled
    var spotifyLauncher : ActivityResultLauncher<Int>? = null
    var vkLauncher : ActivityResultLauncher<Int>? = null

    fun init(spotifyLauncher : ActivityResultLauncher<Int>) {
        this.spotifyLauncher = spotifyLauncher
        viewModelScope.launch {
            Log.d(TAG, "isFirstLaunch: ${auth.currentUser}")
            val uuid = auth.currentUser?.uid
            if (uuid == null) {
                _status.postValue(HomeStatus.NotAuthorized)
                return@launch
            }
            listenUser(uuid)
        }
    }

    fun handleVkToken(activity: ComponentActivity,vkCallback: VKCallback){
        VK.addTokenExpiredHandler(object : VKTokenExpiredHandler{
            override fun onTokenExpired() {
                vkLauncher?.launch(VK_SIGN_IN)
                VK.login(activity, vkScopes)
            }
        })
        vkCallback.registerForCallback(object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                val accessToken = token.accessToken
                viewModelScope.launch {
                    saveVkToken(activity,accessToken)
                }
            }

            override fun onLoginFailed(errorCode: Int) {
                viewModelScope.launch {
                    val userAccounts = currentUser?.accounts?.toMutableList()
                    userAccounts?.removeAll {
                        it.type == ACC_TYPE_VK
                    }
                    val newUser = currentUser?.copy(
                        accounts = userAccounts
                    )
                    if (newUser != null) {
                        saveUser(newUser)
                    }
                    Log.d(TAG, "getMusicData: CANT GET VK")
                }
            }
        })

    }

    private fun listenUser(uuid: String) {
        viewModelScope.launch {
            userListener = db.collection("users/").whereEqualTo("gkey", uuid)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        _status.postValue(HomeStatus.NotAuthorized)
                    }
                    val user = value?.toObjects<User>()?.firstOrNull()
                    if (user != null) {
                        currentUser = user

                        var vk = false
                        var spotify = false
                        user.accounts?.forEach {
                            when (it.type) {
                                ACC_TYPE_SPOTIFY -> {
                                    spotify = true
                                }
                                ACC_TYPE_VK -> {
                                    vk = true
                                }
                            }
                        }

                        val isSpotifyEnabled = isSpotifyUser || spotify
                        val isVkEnabled = isVkUser || vk
                        if (!isVkEnabled) {
                            VK.logout()
                        }
                        val music = user.music
                        if (isSpotifyEnabled && (user.music == null || music == null || music.isEmpty())) {
                            viewModelScope.launch {
                                updateUserMusic()
                            }
                        }
                        val friends = user.friends
                        if (isVkEnabled && (friends == null || friends.isEmpty())) {
                            viewModelScope.launch {
                                updateUserFriends()
                            }
                        }

                        _isSpotifyEnabled.postValue(isSpotifyEnabled)
                        _isVkEnabled.postValue(isVkEnabled)

                        _status.postValue(HomeStatus.Data(user))
                    } else {
                        _status.postValue(HomeStatus.FirstLaunch)
                        createUserOnFirstLaunch(uuid)
                    }
                }
        }
    }

    suspend fun updateUserFriends() {
        _isRefreshing.postValue(true)

        val friends = getFriendsData()
        val user = currentUser
        if (user != null) {
            val newUser = user.copy(
                friends = friends
            )
            saveUser(newUser)
        }
        _isRefreshing.postValue(false)

    }

    suspend fun updateUserMusic() {
        _isRefreshing.postValue(true)
        Log.d(TAG, "updateUserMusic: UPDATE MUSIC")
        val musicData = getMusicData()
        val newUser = currentUser?.copy(
            music = musicData
        )
        if (newUser != null) {
            saveUser(newUser)
        }
        _isRefreshing.postValue(false)

    }

    private fun createUserOnFirstLaunch(uuid: String) {
        viewModelScope.launch {
            val email = auth.currentUser?.email
            val displayName = auth.currentUser?.displayName
            val photo = "https:${auth.currentUser?.photoUrl?.encodedSchemeSpecificPart}"

            val user = User(
                gkey = uuid,
                email = email,
                photoUrl = photo,
                name = displayName,
            )
            saveUser(user)
        }
    }

    fun handleNewSpotifyToken(context: Context, response: AuthenticationResponse?){
        viewModelScope.launch {
            val token = response?.accessToken
            if (token != null) {
                saveSpotifyToken(context, token)
                getMusicData()
            } else {
                val userAccounts = currentUser?.accounts?.toMutableList()
                userAccounts?.removeAll {
                    it.type == ACC_TYPE_SPOTIFY
                }
                val newUser = currentUser?.copy(
                    accounts = userAccounts
                )
                if (newUser != null) {
                    saveUser(newUser)
                }
                Log.d(TAG, "getMusicData: CANT GET SPOTIFY")
            }
        }
    }



    suspend fun getMusicData(): List<MusicData>? {
        var result: List<MusicData>? = null
        spotifyApi.getTopArtists().onError {
            if (statusCode.code == 401) {
                spotifyLauncher?.launch(SPOTIFY_SIGN_IN)
            }
        }.onSuccess {
            result = data?.items?.map { it.mapToData() }
        }
        return result
    }

    suspend fun getFriendsData(): List<RelatedFriend> {
        return VkApi.getRelatedFriends().items.map { it.toRelatedFriends() }
    }

    private suspend fun saveUser(user: User) {
        kotlin.runCatching {
            db.collection("/users")
                .document(user.gkey.toString())
                .set(user)
                .await()
        }.onFailure {
            Log.d(TAG, "createUserOnFirstLaunch: ${it.message} $it")
        }
    }

    fun connectVk(context: Context, token: VKAccessToken?) {
        if (token == null) {
//            sendError("Bad spotify reponse")
            return
        }
        val accessToken = token.accessToken
        viewModelScope.launch {
            saveVkToken(context, accessToken)
            val profile = VkApi.getVkProfile(token.userId)
            saveVkAccount(
                token = accessToken,
                displayName = "${profile?.firstName} ${profile?.lastName}",
                photoUrl = profile?.photo200Orig.toString()
            )

        }
    }


    fun connectSpotify(context: Context, response: AuthenticationResponse?) {
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
                    saveSpotifyAccount(token, me)
                    Log.d(TAG, "authWithSpotifyToken: $me")
                }
            }.onError {
                Log.d(TAG, "authWithSpotifyToken: ${this.message()}")
            }
        }
    }

    private suspend fun saveSpotifyAccount(token: String, me: SpotifyMe) {
        val curUser = currentUser
        if (curUser != null) {
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
            )
            saveUser(newUser)
        }
    }

    private suspend fun saveVkAccount(
        token: String,
        displayName: String,
        photoUrl: String?,
    ) {
        //get vk data

        val curUser = currentUser
        Log.d(TAG, "saveVkAccount: $curUser")
        if (curUser != null) {
            val accs = (curUser.accounts ?: emptyList()).toMutableList()
            val vkAccount = Account(
                type = ACC_TYPE_VK,
                name = displayName,
                token = token,
                photoUrl = photoUrl
            )
            accs.add(vkAccount)
            val newUser = curUser.copy(
                accounts = accs,
            )
            saveUser(newUser)
        }
    }

    fun logout(context: Context, navController: NavController?) {
        viewModelScope.launch {
            context.updatePreferences(SpotifyToken, "")
            context.updatePreferences(FirebaseToken, "")
            context.updatePreferences(VKToken, "")
            VK.logout()
            Firebase.auth.signOut()
            navController?.navigate(Nav.Auth.route) {
                popUpTo(Nav.Main.route) {
                    inclusive = true
                }
            }
        }
    }

    fun refresh(activity: ComponentActivity) {
        viewModelScope.launch {
            if (isSpotifyEnabled.value == true) {
                updateUserMusic()
            }
            if (isVkEnabled.value == true) {
                updateUserFriends()
            }
        }
    }
}