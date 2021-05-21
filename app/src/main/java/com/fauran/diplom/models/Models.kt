package com.fauran.diplom.models

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.fauran.diplom.main.home.BaseSection
import com.fauran.diplom.util.isSpotifyUser
import com.fauran.diplom.util.isVkUser
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

const val ACC_TYPE_SPOTIFY = "spotify"
const val ACC_TYPE_VK = "vk"

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
    var accounts: List<Account>? = null,
    @get:PropertyName("friends")
    @set:PropertyName("friends")
    var friends: List<RelatedFriend>? = null,
    @get:PropertyName("suggestions")
    @set:PropertyName("suggestions")
    var suggestions: List<Suggestion>? = null
) {
    val isVkEnabled: Boolean
        get() = accounts?.find {
            it.type == ACC_TYPE_VK
        } != null || isVkUser

    val isSpotifyEnabled : Boolean
        get() = accounts?.find {
            it.type == ACC_TYPE_SPOTIFY
        } != null || isSpotifyUser

}

data class RelatedFriend(
    @get:PropertyName("firstName")
    @set:PropertyName("firstName")
    var firstName: String? = null,
    @get:PropertyName("lastName")
    @set:PropertyName("lastName")
    var lastName: String? = null,
    @get:PropertyName("sex")
    @set:PropertyName("sex")
    var sex: String? = null,
    @get:PropertyName("city")
    @set:PropertyName("city")
    var city: String? = null,
    @get:PropertyName("country")
    @set:PropertyName("country")
    var country: String? = null,
    @get:PropertyName("photo")
    @set:PropertyName("photo")
    var photo: String? = null,
    @get:PropertyName("domain")
    @set:PropertyName("domain")
    var domain: String? = null,
    @get:PropertyName("interests")
    @set:PropertyName("interests")
    var interests: String? = null,
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String? = null
) : BaseSection()


data class Account(
    @get:PropertyName("type")
    @set:PropertyName("type")
    var type: String? = null,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String? = null,
    @get:PropertyName("token")
    @set:PropertyName("token")
    var token: String? = null,
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String? = null,
    @get:PropertyName("photoUrl")
    @set:PropertyName("photoUrl")
    var photoUrl: String? = null
)


data class MusicData(
    @get:PropertyName("genres")
    @set:PropertyName("genres")
    var genres: List<String>? = null,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String? = null,
    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String? = null,
) : BaseSection()


data class SpotifyTopArtistsResponse(
    val items: List<SpotifyTopArtistsItem>? = null
)

data class SpotifyTopArtistsItem(
    var genres: List<String>?,
    var name: String?,
    @SerializedName("images")
    var images: List<SpotifyImage>?
) {
    fun mapToData(): MusicData {
        return MusicData(
            genres = genres,
            name = name,
            imageUrl = images?.firstOrNull()?.url
        )
    }
}

data class SearchArtistResponse(
    val artists: SpotifyArtists
)

data class SpotifyArtists(
    val items: List<SpotifyArtist>
)

data class SpotifyArtist(
    @SerializedName("uri")
    val uri: String? = null,
    val name: String? = null,
    val images: List<SpotifyImage>? = null,
    val id: String? = null
)


data class ThemeColor(
    val light: Color,
    val medium: Color,
    val dark: Color,
    val gradient: Brush
)


data class Suggestion(
    @get:PropertyName("firstName")
    @set:PropertyName("firstName")
    var firstName: String? = null,
    @get:PropertyName("city")
    @set:PropertyName("city")
    var city: String? = null,
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String? = null,
    @get:PropertyName("lastName")
    @set:PropertyName("lastName")
    var lastName: String? = null,
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String? = null,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String? = null,
    @get:PropertyName("type")
    @set:PropertyName("type")
    var type: String? = null,
    @get:PropertyName("screenName")
    @set:PropertyName("screenName")
    var screenName: String? = null,
    @get:PropertyName("photo")
    @set:PropertyName("photo")
    var photo: String? = null,
) : BaseSection()

