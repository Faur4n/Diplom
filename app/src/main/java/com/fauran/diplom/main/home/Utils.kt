package com.fauran.diplom.main.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.SpeakerGroup
import androidx.compose.material.icons.sharp.Audiotrack
import androidx.compose.material.icons.sharp.ContactSupport
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.fauran.diplom.R
import com.fauran.diplom.models.User
import com.fauran.diplom.util.getMatColor
import java.util.*

val emptySection =  Section(
    R.string.empty_section,
    emptyList(),
    icon =  Icons.Sharp.ContactSupport
)

fun User.createSections(context: Context): List<Section> {
    val sections = mutableListOf<Section>()
    val music = music
    if (music != null){
        sections.add(
            Section(
                R.string.music_title,
                music.shuffled(),
                icon =  Icons.Sharp.Audiotrack
            )
        )
        val genres = music.map {
            it.genres ?: emptyList()
        }.flatten().map {
            Genre(
                name = it,
                color = getMatColor(context = context,"200")
            )
        }.shuffled()
        sections.add(
            Section(
                title = R.string.genres_title,
                items = genres,
                icon = Icons.Outlined.SpeakerGroup
            )
        )
    }
    val friends = friends
    if(friends != null){
        sections.add(
            Section(
                title = R.string.friends_title,
                items = friends,
                icon = Icons.Filled.People
            )
        )
    }
    if(sections.isEmpty()){
        sections.add(emptySection)
    }
    return sections
}

data class Section(
    @StringRes val title: Int,
    val items: List<BaseSection>,
    val icon : ImageVector,
    val id : String = UUID.randomUUID().toString(),
    )

data class Genre(
    val name : String,
    val color : Color
) : BaseSection()

abstract class BaseSection()
