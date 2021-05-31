package com.fauran.diplom.main.home

import android.util.Log
import com.fauran.diplom.TAG
import com.fauran.diplom.main.vk_api.VkApi
import com.fauran.diplom.main.vk_api.VkApi.toRelatedFriends
import com.fauran.diplom.main.vk_api.VkApi.toSuggestion
import com.fauran.diplom.models.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class HomeUseCase @Inject constructor() {
    private val db = Firebase.firestore

    suspend fun updateVkData(user: User?) {
        coroutineScope {
            val suggestion = getSuggestions()

            val friends = getFriendsData()

            if (user != null) {
                val newUser = user.copy(
                    friends = friends,
                    searchableFriends = friends.map {
                        SearchableFriend(
                            it.firstName,
                            it.lastName,
                            it.sex,
                            it.city,
                            it.country,
                            it.id
                        )
                    },
                    suggestions = suggestion,
                    searchableSuggestions = suggestion?.map {
                        SearchableSuggestion(
                            it.firstName,
                            it.lastName,
                            it.name,
                            it.type,
                            it.id
                        )
                    },
                )
                saveUser(newUser)
            }
        }
    }

    private suspend fun getFriendsData(): List<RelatedFriend> {
        return VkApi.getRelatedFriends().items.map { it.toRelatedFriends() }
    }

    private suspend fun getSuggestions(): List<Suggestion>? {
        return VkApi.getNewsSuggestions().items?.map { it.toSuggestion() }
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

}