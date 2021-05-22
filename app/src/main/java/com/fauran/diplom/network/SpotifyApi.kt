package com.fauran.diplom.network

import com.fauran.diplom.main.home.utils.Genre
import com.fauran.diplom.models.SearchArtistResponse
import com.skydoves.sandwich.ApiResponse


class SpotifyApi(
    private val api : SpotifyApiService
) {
    suspend fun getMe() = api.getMe()

    suspend fun getTopArtists() = api.getTopArtists()

    suspend fun getGenreInfo(genre: Genre): ApiResponse<SearchArtistResponse> {
        val searchQuery = "genre:${genre.name}"
        return api.getSearchArtist(searchQuery)
    }
}