package com.fauran.diplom.network

import com.fauran.diplom.models.SpotifyMe
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET

interface SpotifyApiService{

    @GET("me")
    suspend fun getMe() : ApiResponse<SpotifyMe>
}