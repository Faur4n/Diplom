package com.fauran.diplom.main.home.list_items

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.models.Suggestion
import com.fauran.diplom.ui.theme.Typography
import com.google.accompanist.coil.rememberCoilPainter
import com.vk.sdk.api.users.dto.UsersUserType

@Composable
fun SuggestionCard(
    suggestion: Suggestion,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier.clickable {
            val id = suggestion.id
            if (id != null) {
                val launcher = if (suggestion.type == UsersUserType.PROFILE.name) {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("vkontakte://profile/${id}")
                    )
                }else {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("vkontakte://profile/-${id}")
                    )
                }
                context.startActivity(launcher)
            }
        }
    ) {
        Row() {
            val paint = rememberCoilPainter(request = suggestion.photo ?: R.drawable.ic_image)
            Image(
                painter = paint,
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.CenterVertically)
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                val firstName = suggestion.firstName
                val lastName = suggestion.lastName
                if (firstName != null || lastName != null) {
                    Text(text = "$firstName $lastName", style = Typography.h4)
                }
                val name = suggestion.name
                if (name != null)
                    Text(text = name, style = Typography.h6)
                val city = suggestion.city
                if (city != null) {
                    Text(text = city, style = Typography.subtitle2)
                }
                val description = suggestion.description
                if (description != null) {
                    Text(text = description, maxLines = 3, style = Typography.body1)
                }
            }
        }
    }
}