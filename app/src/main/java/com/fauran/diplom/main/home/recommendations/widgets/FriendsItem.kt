package com.fauran.diplom.main.home.recommendations.widgets

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fauran.diplom.R
import com.fauran.diplom.models.RelatedFriend
import com.fauran.diplom.ui.theme.Typography
import com.fauran.diplom.ui.theme.black
import com.fauran.diplom.ui.theme.white
import com.google.accompanist.coil.rememberCoilPainter

@ExperimentalMaterialApi
@Composable
fun FriendsItem(
    item: RelatedFriend,
    modifier: Modifier = Modifier,
) {
    val paint = rememberCoilPainter(
        request = item.photo,
        previewPlaceholder = R.drawable.ic_image
    )
    val animatedProgress = remember { Animatable(initialValue = 0.8f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring()
        )
    }
    val context = LocalContext.current
    val resources = context.resources
    Card(modifier = modifier.fillMaxSize(), elevation = 8.dp, onClick = {
        val id = item.id
        if (id != null) {
            val launcher = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("vkontakte://profile/${id}")
            )
            context.startActivity(launcher)
        }
    }, shape = RoundedCornerShape(16.dp), content = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .graphicsLayer(scaleY = animatedProgress.value, scaleX = animatedProgress.value)

        ) {
            Image(
                painter = paint,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxHeight()
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = SolidColor(black), alpha = 0.5f)
            ) {
                Text(
                    style = Typography.subtitle1,
                    text = "${item.firstName} ${item.lastName}",
                    textAlign = TextAlign.Center,
                    color = white,
                    maxLines = 2,
                )
                if (item.city != null || item.country != null) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Row() {
                        item.country?.let { country ->
                            Text(
                                style = Typography.subtitle2,
                                text = country,
                                textAlign = TextAlign.Center,
                                color = white,
                                maxLines = 2,
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        item.city?.let { city ->
                            Text(
                                style = Typography.subtitle2,
                                text = city,
                                textAlign = TextAlign.Center,
                                color = white,
                                maxLines = 2,
                            )
                        }
                    }
                }
                item.age?.let { age ->
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        style = Typography.subtitle2,
                        text = resources.getQuantityString(R.plurals.years, age, age),
                        textAlign = TextAlign.Center,
                        color = white,
                        maxLines = 2,
                    )
                }

            }

        }
    })
}