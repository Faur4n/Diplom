package com.fauran.diplom.main.home

import android.content.Context
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.SpeakerGroup
import androidx.compose.material.icons.sharp.Audiotrack
import androidx.compose.material.icons.sharp.ContactSupport
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.fauran.diplom.R
import com.fauran.diplom.SPOTIFY_SIGN_IN
import com.fauran.diplom.models.User
import com.fauran.diplom.util.getMatColor
import com.skydoves.sandwich.ApiResponse
import java.util.*

val emptySection = Section(
    R.string.empty_section,
    emptyList(),
    icon = Icons.Sharp.ContactSupport
)

fun User.createSections(context: Context): List<Section> {
    val sections = mutableListOf<Section>()
    val music = music
    if (music != null) {
        sections.add(
            Section(
                R.string.music_title,
                music.shuffled(),
                icon = Icons.Sharp.Audiotrack
            )
        )
        val genres = music.map {
            it.genres ?: emptyList()
        }.flatten().map {
            Genre(
                name = it,
                color = getMatColor(context = context, "200")
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
    if (friends != null) {
        sections.add(
            Section(
                title = R.string.friends_title,
                items = friends,
                icon = Icons.Filled.People
            )
        )
    }
    val suggestions = suggestions
    if (suggestions != null) {
        sections.add(
            Section(
                R.string.suggestions_title,
                suggestions,
                Icons.Default.Star
            )
        )
    }

    if (sections.isEmpty()) {
        sections.add(emptySection)
    }
    return sections
}

data class Section(
    @StringRes val title: Int,
    val items: List<BaseSection>,
    val icon: ImageVector,
    val id: String = UUID.randomUUID().toString(),
)

data class Genre(
    val name: String,
    val color: Color
) : BaseSection()

abstract class BaseSection()


fun ApiResponse.Failure.Error<*>.handleSpotifyAuthError(launcher: ActivityResultLauncher<Int>?): String {
    if (statusCode.code == 401 || statusCode.code == 400) {
        launcher?.launch(SPOTIFY_SIGN_IN)
    }
    return errorBody?.string().toString()
}

class ToastBus {
    companion object {
        val listeners: MutableList<() -> Context?> = mutableListOf()

        fun showToast(msg: String) {
            val anyContext = listeners.mapNotNull {
                it.invoke()
            }.firstOrNull()
            if (anyContext != null)
                Toast.makeText(anyContext, msg, Toast.LENGTH_SHORT).show()
        }

        fun addToastContextHandler(contextGetter: () -> Context) {
            listeners.add(contextGetter)
        }

        fun removeToastContextHandler(contextGetter: () -> Context) {
            listeners.remove(contextGetter)
        }
    }
}