package com.fauran.diplom.models

import androidx.annotation.StringRes
import com.fauran.diplom.R
import com.fauran.diplom.main.home.BaseSection
import com.fauran.diplom.main.home.Section
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
    @get:PropertyName("accounts")
    @set:PropertyName("accounts")
    var accounts : List<Account>?  = null
)

const val ACC_TYPE_SPOTIFY = "spotify"
data class Account(
    @get:PropertyName("type")
    @set:PropertyName("type")
    var type: String? = null,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String? = null,
    @get:PropertyName("token")
    @set:PropertyName("token")
    var token : String? = null,
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String? = null,
    @get:PropertyName("photoUrl")
    @set:PropertyName("photoUrl")
    var photoUrl: String? =null
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
) : BaseSection()


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

