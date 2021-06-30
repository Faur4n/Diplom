package com.fauran.diplom.main.home.recommendations.widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.fauran.diplom.main.home.list_items.MusicItem

@Composable
fun ExpandableItem(
    label : String,
    modifier : Modifier = Modifier,
    withArrow : Boolean = true,
    content : @Composable ColumnScope.() -> Unit
){
    val (isOpen,setIsOpen) = remember{ mutableStateOf(false) }
    val angle: Float by animateFloatAsState(
        targetValue = if (isOpen) 180f else 0F,
        animationSpec = spring()
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp),modifier = modifier.animateContentSize().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    setIsOpen(!isOpen)
                }
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f)
            )
            if(withArrow){
                Icon(Icons.Filled.KeyboardArrowDown, null,
                    modifier = Modifier.rotate(angle)
                )
            }
        }
        if(isOpen){
            content()
        }
    }

}