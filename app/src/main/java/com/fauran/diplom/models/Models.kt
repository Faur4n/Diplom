package com.fauran.diplom.models

import androidx.annotation.StringRes
import com.fauran.diplom.R
import com.google.firebase.firestore.PropertyName
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


data class User(
    @get:PropertyName("gkey")
    @set:PropertyName("gkey")
    var gkey: String? = null,
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String? = null,
    @get:PropertyName("photo_url")
    @set:PropertyName("photo_url")
    var photoUrl: String? = null,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String? = null,
    @get:PropertyName("music")
    @set:PropertyName("music")
    var music: List<MusicData>? = null,
)

data class MusicData(
    @get:PropertyName("genres")
    @set:PropertyName("genres")
    var genres : List<String>? = null,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name : String? = null,
    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl : String? = null,
) : PageData()

abstract class PageData()

data class SpotifyTopArtistsResponse(
    val items : List<SpotifyTopArtistsItem>? = null
)

data class SpotifyTopArtistsItem(
    var genres : List<String>?,
    var name : String?,
    @SerializedName("images")
    var images : List<SpotifyImage>?
){
    fun mapToData(): MusicData {
        return MusicData(
            genres = genres,
            name = name,
            imageUrl = images?.firstOrNull()?.url
        )
    }
}

