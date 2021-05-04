package com.fauran.diplom.network



class SpotifyApi(
    private val api : SpotifyApiService
) {
    fun getMe() = api.getMe()
}