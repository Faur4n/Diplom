package com.fauran.diplom.main.home.recommendations.models

import com.fauran.diplom.models.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecData(
    @SerialName("objectID")
    val id: String,
)

@Serializable
data class Category(
    val name: String,
    val screenName: String,
    val subs: List<SubCategory>
)

@Serializable
data class SubCategory(
    val name: String,
    val screenName: String
)

sealed class Intersection{

    data class MusicIntersection(val music: List<MusicData>) : Intersection()

    data class FriendsIntersection(val friends : List<RelatedFriend>) : Intersection()

    data class SuggestionsIntersection(val suggestions : List<Suggestion>) : Intersection()

}

data class RecommendationUser(
    val user: User,
    val intersections : List<Intersection>
)
