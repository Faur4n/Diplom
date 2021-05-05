package com.fauran.diplom.network

import com.fauran.diplom.models.SpotifyMe
import com.fauran.diplom.models.SpotifyTopArtistsResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET


interface SpotifyApiService{

    @GET("me")
    suspend fun getMe() : ApiResponse<SpotifyMe>

    @GET("me/top/artists")
    suspend fun getTopArtists() : ApiResponse<SpotifyTopArtistsResponse>
}