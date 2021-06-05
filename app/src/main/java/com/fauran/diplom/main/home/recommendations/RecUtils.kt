package com.fauran.diplom.main.home.recommendations

import com.fauran.diplom.main.home.recommendations.models.Intersection
import com.fauran.diplom.models.User

class RecUtils {

    companion object{
        fun findIntersections(user: User?,secondUser: User?,maxCount: Int = 6): List<Intersection> {
            val music = user?.music?.intersect(
                secondUser?.music ?: emptyList()
            )?.toList() ?: emptyList()
            val suggestions = user?.suggestions?.intersect(
                secondUser?.suggestions ?: emptyList()
            )?.toList() ?: emptyList()
            val friends = user?.friends?.intersect(
                secondUser?.friends ?: emptyList()
            )?.toList() ?: emptyList()
            return listOf(
                Intersection.MusicIntersection(music),
                Intersection.FriendsIntersection(friends),
                Intersection.SuggestionsIntersection(suggestions)
            )
        }

    }
}