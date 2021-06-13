package com.fauran.diplom.main.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.fauran.diplom.TAG
import com.fauran.diplom.local.Preferences.FirebaseToken
import com.fauran.diplom.local.Preferences.SpotifyToken
import com.fauran.diplom.local.Preferences.VKToken
import com.fauran.diplom.local.Preferences.updatePreferences
import com.fauran.diplom.main.home.utils.ContextBus
import com.fauran.diplom.main.home.utils.Genre
import com.fauran.diplom.main.home.utils.LocationBus
import com.fauran.diplom.main.home.utils.handleSpotifyAuthError
import com.fauran.diplom.main.vk_api.VkApi
import com.fauran.diplom.models.*
import com.fauran.diplom.navigation.Nav
import com.fauran.diplom.network.SpotifyApi
import com.fauran.diplom.util.saveSpotifyToken
import com.fauran.diplom.util.saveVkToken
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.skydoves.sandwich.*
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

data class HomeState(
    val user: User? = null,
    val error: String? = null,
    val logout: Boolean = false
)

data class GenreState(
    val genre: Genre,
    val artists: List<SpotifyArtist>
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyApi: SpotifyApi,
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var currentUser: User? = null
    private var userListener: ListenerRegistration? = null
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    private var spotifyLauncher: ActivityResultLauncher<Int>? = null
    private val tokenSrc = CancellationTokenSource()

    private val _genreState = MutableSharedFlow<GenreState?>(1)
    val genreState = _genreState.asSharedFlow()

    @ExperimentalCoroutinesApi
    fun init(spotifyLauncher: ActivityResultLauncher<Int>) {
        this.spotifyLauncher = spotifyLauncher
        viewModelScope.launch {
            Log.d(TAG, "isFirstLaunch: ${auth.currentUser}")
            val uuid = auth.currentUser?.uid
            if (uuid == null) {
                updateState(logout = true)
                return@launch
            }
            listenUser(uuid)
        }
    }

    private fun updateState(user: User? = null, error: String? = null, logout: Boolean? = null) {
        val state = state.value
        viewModelScope.launch {
            _state.emit(
                HomeState(
                    user = user ?: state.user,
                    error = error,
                    logout = logout ?: state.logout
                )
            )
        }
    }

    fun removeVkAccount() {
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

    private fun listenUser(uuid: String) {
        viewModelScope.launch {
            userListener = db.collection("users/").whereEqualTo("gkey", uuid)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        updateState(logout = true)
                    }
                    val user = value?.toObjects<User>()?.firstOrNull()
                    if (user != null) {
                        currentUser = user
                        if (!user.isVkEnabled) {
                            VK.logout()
                        }
                        Log.d(
                            TAG,
                            "listenUser: VK ${user.isVkEnabled} SPOT ${user.isSpotifyEnabled}"
                        )
                        val music = user.music
                        if (user.isSpotifyEnabled && (user.music == null || music == null || music.isEmpty())) {
                            updateUserMusic()
                            return@addSnapshotListener
                        }
                        val friends = user.friends
                        if (user.isVkEnabled && (friends == null || friends.isEmpty())) {
                            updateUserFriends()
                            return@addSnapshotListener
                        }
                        updateState(user = user)
                    } else {
                        createUserOnFirstLaunch(uuid)
                    }
                }
        }
    }

    private fun updateUserFriends() {
        viewModelScope.launch {
            _isRefreshing.postValue(true)
            try {
                homeUseCase.updateVkData(currentUser)
            } catch (th: Throwable) {
                Log.d(TAG, "updateUserFriends: $th")
            }
            _isRefreshing.postValue(false)
        }
    }

    private fun updateUserMusic() {
        viewModelScope.launch {
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


    fun handleNewSpotifyToken(context: Context, response: AuthenticationResponse?) {
        viewModelScope.launch {
            val token = response?.accessToken
            if (token != null) {
                saveSpotifyToken(context, token)
                refresh()
            }
        }
    }

    private suspend fun getMusicData(): List<MusicData>? {
        var result: List<MusicData>? = null
        spotifyApi.getTopArtists().onError {
            val err = handleSpotifyAuthError(spotifyLauncher)
            updateState(error = err)
        }.onSuccess {
            result = data?.items?.map { it.mapToData() }
        }
        return result
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
            updateState(error = "Bad spotify reponse")
            return
        }
        val token = response.accessToken
        if (token == null) {
            updateState(error = "Empty spotify token")
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
                updateState(error = message())
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

    fun makeUserShared() {
        val locationClient = LocationBus.getLocationClient()
        val context = ContextBus.getContext()
        if (context != null && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient
                ?.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                    tokenSrc.token
                )
                ?.addOnSuccessListener { location ->
                    try {
                        currentUser?.copy(
                            shared = true,
                            location = Location(
                                location.latitude,
                                location.longitude
                            )
                        )?.let {
                            viewModelScope.launch {
                                saveUser(
                                    it
                                )
                            }
                        }
                    } catch (th: Throwable) {
                        Log.d(TAG, "makeUserShared: ${th.message}")
                    }
                }
        }
    }

    fun refresh() {
        if (currentUser?.isSpotifyEnabled == true) {
            updateUserMusic()
        }
        if (currentUser?.isVkEnabled == true) {
            updateUserFriends()
        }
    }

    fun goToGenres(genre: Genre) {
        viewModelScope.launch {
            downloadGenre(genre) { list ->
                _genreState.emit(GenreState(genre, list))
            }
        }
    }

    private suspend fun downloadGenre(
        genre: Genre,
        onResult: suspend (List<SpotifyArtist>) -> Unit
    ) {
        spotifyApi.getGenreInfo(genre).suspendOnSuccess {
            Log.d(TAG, "downloadGenreInfo: $data")
            val items = data?.artists?.items
            if (items != null && items.isNotEmpty()) {
                onResult(items)
            } else {
                ContextBus.showToast("По данному жанру не удалось загрузить дополнительную информацию")
            }
        }.onError {
            handleSpotifyAuthError(spotifyLauncher)
        }
    }

    fun consumeGenreState(navController: NavController?) {
        viewModelScope.launch {
            _genreState.emit(null)
            navController?.popBackStack()
        }
    }


    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        tokenSrc.cancel()
    }
}