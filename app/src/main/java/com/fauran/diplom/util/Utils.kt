package com.fauran.diplom.util

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.fauran.diplom.local.Preferences
import com.fauran.diplom.local.Preferences.updatePreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

inline fun <T> tryOrNull(action: () -> T) =
    try {
        action()
    } catch (_: Exception) {
        null
    }


fun Context.showToast(msg : String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}

fun getMatColor(context: Context,typeColor: String): Color {
    var returnColor = android.graphics.Color.BLACK
    val arrayId = context.resources.getIdentifier(
        "mdcolor_$typeColor",
        "array",
        context.packageName
    )

    if (arrayId != 0) {
        val colors = context.resources.obtainTypedArray(arrayId)
        val index = (Math.random() * colors.length()).toInt()
        returnColor = colors.getColor(index, android.graphics.Color.BLACK)
        colors.recycle()
    }
    return Color(returnColor)
}

suspend fun saveSpotifyToken(context: Context, token: String) {
    context.updatePreferences(Preferences.SpotifyToken, token)
}
suspend fun saveVkToken(context: Context, token: String) {
    context.updatePreferences(Preferences.SpotifyToken, token)
}
val isSpotifyUser get() = Firebase.auth.currentUser?.uid?.startsWith("spotify") == true

val isVkUser get() = Firebase.auth.currentUser?.uid?.startsWith("VK") == true


inline fun <reified T> List<*>.ifListOf(block: (List<T>) -> Unit) {
    if (isNotEmpty() && first() is T) {
        kotlin.runCatching {
            map {
                it as T
            }
        }.onSuccess {
            block(it)
        }
    }
}

