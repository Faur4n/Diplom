package com.fauran.diplom.network

import com.fauran.diplom.models.SearchArtistResponse
import com.fauran.diplom.models.SpotifyMe
import com.fauran.diplom.models.SpotifyTopArtistsResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.random.Random


interface SpotifyApiService {

    @GET("me")
    suspend fun getMe(): ApiResponse<SpotifyMe>

    @GET("me/top/artists")
    suspend fun getTopArtists(
        @Query("offset") offset: Int = Random.nextInt(
            0,
            50
        )
    ): ApiResponse<SpotifyTopArtistsResponse>

    @GET("/v1/search")
    suspend fun getSearchArtist(
        @Query("q") query: String,
        @Query("type") type: String = "artist"
    ): ApiResponse<SearchArtistResponse>
}