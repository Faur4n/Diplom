package com.fauran.diplom.network



class SpotifyApi(
    private val api : SpotifyApiService
) {
    suspend fun getMe() = api.getMe()

    suspend fun getTopArtists() = api.getTopArtists()
}