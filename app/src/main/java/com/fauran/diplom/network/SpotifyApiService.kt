package com.fauran.diplom.network

import com.google.gson.JsonObject
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET

interface SpotifyApiService{

    @GET("/me")
    fun getMe() : ApiResponse<JsonObject>
}