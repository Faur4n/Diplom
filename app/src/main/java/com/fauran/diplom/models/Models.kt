package com.fauran.diplom.models

import com.google.gson.annotations.SerializedName

data class SpotifyMe(
    @SerializedName("display_name")
    val displayName: String?,
    val email: String?,
    val href: String?,
    val id: String?,
    val images: List<SpotifyImage>,
    val type: String?,
    val uri: String?
)


data class SpotifyImage(
    val height: String?,
    val width: String?,
    val url: String?,
)