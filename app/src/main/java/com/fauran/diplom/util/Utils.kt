package com.fauran.diplom.util

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.fauran.diplom.local.Preferences
import com.fauran.diplom.local.Preferences.updatePreferences


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
