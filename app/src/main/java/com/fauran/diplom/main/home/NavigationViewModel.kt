package com.fauran.diplom.main.home

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fauran.diplom.TAG
import com.fauran.diplom.models.SpotifyArtist
import com.fauran.diplom.network.SpotifyApi
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val spotifyApi: SpotifyApi
) : ViewModel() {

    private val _currentScreen = MutableLiveData<HomeScreen>(HomeScreen.Home)
    val currentScreen: LiveData<HomeScreen> = _currentScreen
    private var downloadGenreJob: Job? = null
    private var spotifyLauncher: ActivityResultLauncher<Int>? = null

    fun init(launcher: ActivityResultLauncher<Int>) {
        spotifyLauncher = launcher
    }

    fun navigateHome() {
        _currentScreen.postValue(HomeScreen.Home)
    }

    fun navigateToGenres(genre: Genre) {
        downloadGenreJob?.cancel()
        downloadGenreJob = viewModelScope.launch {
            downloadGenre(genre) { list ->
                _currentScreen.postValue(HomeScreen.Genres(genre, list))
            }
        }
    }

    fun goBack() {
        when (currentScreen.value) {
            is HomeScreen.Genres -> {
                navigateHome()
            }
            HomeScreen.Home -> {
                _currentScreen.postValue(HomeScreen.Back)
            }
            else -> {
                Log.d(TAG, "goBack: Cant get more back")
            }
        }
    }

    private suspend fun downloadGenre(genre: Genre, onResult: (List<SpotifyArtist>) -> Unit) {
        spotifyApi.getGenreInfo(genre).suspendOnSuccess {
            Log.d(TAG, "downloadGenreInfo: $data")
            val items = data?.artists?.items
            if (items != null && items.isNotEmpty()) {
                onResult(items)
            } else {
                ToastBus.showToast("По данному жанру не удалось загрузить дополнительную информацию")
            }
        }.onError {
            handleSpotifyAuthError(spotifyLauncher)
        }
    }
}
