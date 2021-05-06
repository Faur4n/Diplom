package com.fauran.diplom.main.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.outlined.SpeakerGroup
import androidx.compose.material.icons.sharp.Audiotrack
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.fauran.diplom.R
import com.fauran.diplom.main.home.colorAnimation.ThemeColor
import com.fauran.diplom.models.User
import com.fauran.diplom.ui.theme.*
import com.fauran.diplom.util.getMatColor
import java.util.*


fun User.createSections(context: Context): List<Section> {
    val sections = mutableListOf<Section>()
    val mMusic = music
    if (mMusic != null){
        sections.add(
            Section(
                R.string.music_title,
                mMusic,
                ThemeColor(
                    Purple200,
                    Purple500,
                    Purple700,
                    Brush.verticalGradient(
                        colors = listOf(
                            Purple200,
                            Purple500,
                            Purple700,
                        )
                    )
                ),
                icon =  Icons.Sharp.Audiotrack
            )
        )
        val genres = mMusic.map {
            it.genres ?: emptyList()
        }.flatten().map {
            Genre(
                name = it,
                color = getMatColor(context = context,"200")
            )
        }
        sections.add(
            Section(
                title = R.string.genres_title,
                items = genres,
                colors = ThemeColor(
                    Teal200,
                    Teal500,
                    Teal700,
                    Brush.verticalGradient(
                        colors = listOf(
                            Teal200,
                            Teal500,
                            Teal700,
                        )
                    )
                ),
                icon = Icons.Outlined.SpeakerGroup
            )
        )
    }
    return sections
}

data class Section(
    @StringRes val title: Int,
    val items: List<BaseSection>,
    val colors: ThemeColor,
    val icon : ImageVector,
    val id : String = UUID.randomUUID().toString(),
    )

data class Genre(
    val name : String,
    val color : Color
) : BaseSection()

abstract class BaseSection()
