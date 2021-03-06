package com.fauran.diplom.main.home

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauran.diplom.TAG
import com.fauran.diplom.main.home.utils.ContextBus
import com.fauran.diplom.main.home.utils.Genre
import com.fauran.diplom.main.home.utils.handleSpotifyAuthError
import com.fauran.diplom.models.SpotifyArtist
import com.fauran.diplom.models.User
import com.fauran.diplom.network.SpotifyApi
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val spotifyApi: SpotifyApi
) : ViewModel() {

//    private val _currentScreen = MutableLiveData<HomeScreen>()
//    val currentScreen: LiveData<HomeScreen> = _currentScreen
//    private var downloadGenreJob: Job? = null
//    private var spotifyLauncher: ActivityResultLauncher<Int>? = null
//
//    private val backStack = Stack<HomeScreen>()
//
//    fun init(launcher: ActivityResultLauncher<Int>) {
//        spotifyLauncher = launcher
//        navigateTo(HomeScreen.Home)
//    }
//
//    fun navigateHome() {
//        navigateTo(HomeScreen.Home)
//    }
//
//    private fun navigateTo(screen: HomeScreen) {
//        backStack.push(screen)
//        _currentScreen.postValue(screen)
//    }
//
//
//    fun navigateToGenres(genre: Genre) {
//        downloadGenreJob?.cancel()
//        downloadGenreJob = viewModelScope.launch {
//            downloadGenre(genre) { list ->
//                navigateTo(HomeScreen.Genres(genre, list))
//            }
//        }
//    }
//
//    fun navigateToRecommendations() {
//        navigateTo(HomeScreen.Recommendations)
//    }
//
//    fun navigateToUserDetails(user: User) {
//        navigateTo(HomeScreen.UserDetails(user))
//    }
//
//    fun goBack() {
//        backStack.pop()
//        try {
//            _currentScreen.postValue(backStack.lastElement())
//        } catch (th: NoSuchElementException) {
//            _currentScreen.postValue(HomeScreen.Back)
//        }
////
////        when (currentScreen.value) {
////            is HomeScreen.Genres -> {
////                navigateHome()
////            }
////            is HomeScreen.Recommendations -> {
////                navigateHome()
////            }
////            HomeScreen.Home -> {
////                _currentScreen.postValue(HomeScreen.Back)
////            }
////            is HomeScreen.UserDetails{
////                _currentScreen.postValue(HomeScreen.Back)
////            }
////            else -> {
////                Log.d(TAG, "goBack: Cant get more back")
////            }
////        }
//    }
//
//    private suspend fun downloadGenre(genre: Genre, onResult: (List<SpotifyArtist>) -> Unit) {
//        spotifyApi.getGenreInfo(genre).suspendOnSuccess {
//            Log.d(TAG, "downloadGenreInfo: $data")
//            val items = data?.artists?.items
//            if (items != null && items.isNotEmpty()) {
//                onResult(items)
//            } else {
//                ContextBus.showToast("???? ?????????????? ?????????? ???? ?????????????? ?????????????????? ???????????????????????????? ????????????????????")
//            }
//        }.onError {
//            handleSpotifyAuthError(spotifyLauncher)
//        }
//    }
}
