package com.fauran.diplom.main

import android.util.Log
import com.fauran.diplom.TAG
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersFields
import com.vk.sdk.api.users.dto.UsersUserXtrCounters
import kotlin.coroutines.suspendCoroutine

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
}