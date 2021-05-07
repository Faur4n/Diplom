package com.fauran.diplom.main

import android.util.Log
import com.fauran.diplom.TAG
import com.fauran.diplom.models.RelatedFriend
import com.fauran.diplom.models.Suggestion
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.base.dto.BaseUserGroupFields
import com.vk.sdk.api.friends.FriendsService
import com.vk.sdk.api.friends.dto.FilterParam
import com.vk.sdk.api.friends.dto.FriendsGetSuggestionsResponse
import com.vk.sdk.api.friends.dto.NameCaseParam
import com.vk.sdk.api.newsfeed.NewsfeedService
import com.vk.sdk.api.newsfeed.dto.NewsfeedGetSuggestedSourcesResponse
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersFields
import com.vk.sdk.api.users.dto.UsersSubscriptionsItem
import com.vk.sdk.api.users.dto.UsersUserFull
import com.vk.sdk.api.users.dto.UsersUserXtrCounters
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

object VkApi {

    suspend fun getVkProfile(userId: Int) =
        suspendCoroutine<UsersUserXtrCounters?> { continuation ->
            VK.execute(
                UsersService().usersGet(
                    listOf(userId.toString()),
                    listOf(
                        UsersFields.PHOTO_200_ORIG
                    )
                ), object : VKApiCallback<List<UsersUserXtrCounters>> {
                    override fun fail(error: Exception) {
                        Log.d(TAG, "fail: $error")
                        continuation.resumeWith(Result.failure(error))
                    }

                    override fun success(result: List<UsersUserXtrCounters>) {
                        continuation.resumeWith(Result.success(result.firstOrNull()))
                    }
                })
        }

    //Get related friends with random offset
    suspend fun getRelatedFriends() =
        suspendCoroutine<FriendsGetSuggestionsResponse> { continuation ->
            val random = Random.nextInt(0, 100)
            Log.d(TAG, "getRelatedFriends: $random")
            VK.execute(
                FriendsService().friendsGetSuggestions(
                    filter = listOf(
                        FilterParam.CONTACTS,
                        FilterParam.MUTUAL,
                        FilterParam.MUTUAL_CONTACTS
                    ),
                    count = 20,
                    offset = random,
                    fields = listOf(
                        UsersFields.LAST_NAME_NOM,
                        UsersFields.FIRST_NAME_NOM,
                        UsersFields.PHOTO_200_ORIG,
                        UsersFields.CITY,
                        UsersFields.COUNTRY,
                        UsersFields.DOMAIN,
                        UsersFields.INTERESTS,
                        UsersFields.SEX,
                    ),
                    nameCase = NameCaseParam.ACCUSATIVE
                ), object : VKApiCallback<FriendsGetSuggestionsResponse> {
                    override fun fail(error: Exception) {
                        continuation.resumeWith(Result.failure(error))
                    }

                    override fun success(result: FriendsGetSuggestionsResponse) {
                        continuation.resumeWith(Result.success(result))
                    }
                }
            )
        }

    fun UsersUserFull.toRelatedFriends(): RelatedFriend {
        return RelatedFriend(
            firstName = firstNameNom,
            lastName = lastNameNom,
            sex = sex?.name,
            city = city?.title,
            country = country?.title,
            photo = photo200Orig,
            domain = domain,
            interests = interests,
            id = id?.toString()
        )

    }


    suspend fun getNewsSuggestions() =
        suspendCoroutine<NewsfeedGetSuggestedSourcesResponse> { continuation ->
            VK.execute(
                NewsfeedService().newsfeedGetSuggestedSources(
                    count = 50,
                    shuffle = true,
                    fields = listOf(
                        BaseUserGroupFields.ABOUT,
                        BaseUserGroupFields.NAME,
                        BaseUserGroupFields.SCREEN_NAME,
                        BaseUserGroupFields.TYPE,
                        BaseUserGroupFields.ACTIVITIES,
                        BaseUserGroupFields.DESCRIPTION,
                        BaseUserGroupFields.PHOTO_200,
                        BaseUserGroupFields.PHOTO_100,
                        BaseUserGroupFields.CITY,
                        BaseUserGroupFields.COUNTRY,
                        BaseUserGroupFields.COMMON_COUNT
                    )
                ), object : VKApiCallback<NewsfeedGetSuggestedSourcesResponse> {
                    override fun fail(error: Exception) {
                        continuation.resumeWith(Result.failure(error))
                    }

                    override fun success(result: NewsfeedGetSuggestedSourcesResponse) {
                        continuation.resumeWith(Result.success(result))
                    }
                })
        }


    fun UsersSubscriptionsItem.toSuggestion() : Suggestion{
            return when(this){
                is UsersSubscriptionsItem.UsersUserXtrType -> {
                    Suggestion(
                        firstName = firstName,
                        lastName = lastName,
                        id = id?.toString(),
                        type = type?.name,
                        screenName = screenName,
                        photo = photo100
                    )
                }
                is UsersSubscriptionsItem.GroupsGroupFull -> {
                    Suggestion(
                        city = city?.title,
                        description = description,
                        id = id?.toString(),
                        name = name,
                        type = type?.name,
                        photo = photo200
                    )
                }
            }
    }



}