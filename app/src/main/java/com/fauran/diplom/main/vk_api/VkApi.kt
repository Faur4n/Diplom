package com.fauran.diplom.main.vk_api

import android.util.Log
import com.fauran.diplom.TAG
import com.fauran.diplom.models.RelatedFriend
import com.fauran.diplom.models.Suggestion
import com.fauran.diplom.util.tryOrNull
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.base.dto.BaseUserGroupFields
import com.vk.sdk.api.friends.FriendsService
import com.vk.sdk.api.friends.dto.FilterParam
import com.vk.sdk.api.friends.dto.FriendsGetSuggestionsResponse
import com.vk.sdk.api.friends.dto.NameCaseParam
import com.vk.sdk.api.newsfeed.NewsfeedService
import com.vk.sdk.api.newsfeed.dto.NewsfeedGetRecommendedResponse
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersFields
import com.vk.sdk.api.users.dto.UsersUserFull
import com.vk.sdk.api.users.dto.UsersUserXtrCounters
import java.text.SimpleDateFormat
import java.util.*
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
                        UsersFields.ABOUT,
                        UsersFields.BDATE
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

    private fun getAge(date: Date): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob.time = date

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        val ageInt = age

        return ageInt
    }

    fun UsersUserFull.toRelatedFriends(): RelatedFriend {
        Log.d(TAG, "toRelatedFriends: $bdate")
        val bdate = bdate
        var age =
            if (bdate != null) {
                val date = tryOrNull {
                    SimpleDateFormat("d.m.yyyy", Locale.getDefault()).parse(bdate)
                }
                if (date != null) {
                    getAge(date)
                } else null

            } else null



        return RelatedFriend(
            firstName = firstNameNom,
            lastName = lastNameNom,
            sex = sex?.name,
            city = city?.title,
            country = country?.title,
            photo = photo200Orig,
            domain = domain,
            interests = interests,
            id = id?.toString(),
            about = about,
            age = age
        )


    }

    suspend fun getNewsSuggestions() =
        suspendCoroutine<NewsfeedGetRecommendedResponse> { continuation ->
            VK.execute(
                NewsfeedService().newsfeedGetRecommended(
                    count = 50,
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
                ), object : VKApiCallback<NewsfeedGetRecommendedResponse> {
                    override fun fail(error: Exception) {
                        continuation.resumeWith(Result.failure(error))
                    }

                    override fun success(result: NewsfeedGetRecommendedResponse) {
                        continuation.resumeWith(Result.success(result))
                    }
                })
        }


    fun NewsfeedGetRecommendedResponse.toSuggestions(): List<Suggestion> {
        Log.d(TAG, "toSuggestions: ${this.groups}")
        Log.d(TAG, "toSuggestions: ${this.profiles}")
        val groups = (groups ?: emptyList()).asSequence().map { group ->
            Suggestion(
                name = group.name,
                description = group.description,
                city = group.country?.title,
                id = group.id?.toString(),
                type = group.type?.name,
                photo = group.photo200,
                screenName = group.screenName,

                )
        }.shuffled()
        val profiles = (profiles ?: emptyList()).asSequence().map { profile ->
            Suggestion(
                firstName = profile.firstName,
                city = profile.city?.title,
                photo = profile.photo200,
                lastName = profile.lastName,
                type = profile.type?.name,
                id = profile.id?.toString(),
                screenName = profile.screenName,
                description = profile.serviceDescription,
            )
        }.shuffled()
        return (groups + profiles).shuffled().toList()

//        return when (this) {
//            is UsersSubscriptionsItem.UsersUserXtrType -> {
//                Suggestion(
//                    firstName = firstName,
//                    lastName = lastName,
//                    id = id?.toString(),
//                    type = type?.name,
//                    screenName = screenName,
//                    photo = photo100
//                )
//            }
//            is UsersSubscriptionsItem.GroupsGroupFull -> {
//                Suggestion(
//                    city = city?.title,
//                    description = description,
//                    id = id?.toString(),
//                    name = name,
//                    type = type?.name,
//                    photo = photo200
//                )
//            }
//        }
    }


}