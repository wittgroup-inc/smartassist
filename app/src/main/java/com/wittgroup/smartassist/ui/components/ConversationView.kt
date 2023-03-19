package com.wittgroup.smartassist.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.models.Conversation

private const val TAG = "ConversationView"

@Composable
fun ConversationView(modifier: Modifier, list: List<Conversation>, listState: LazyListState) {

    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        state = listState
    ) {
        itemsIndexed(
            items = list,
            itemContent = { index, item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(if (item.isQuestion) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                        .padding(top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = if (item.isQuestion) R.drawable.ic_user else R.drawable.ic_bot),
                        contentDescription = if (item.isQuestion) stringResource(R.string.user_ic_desc) else stringResource(R.string.bot_icon_desc),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(start = 16.dp)
                    )

                    val textModifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 16.dp)

                    val textStyle = MaterialTheme.typography.bodyMedium

                    val rememberedText = remember { mutableStateOf("") }
                    val showCursor = remember { mutableStateOf(true) }
                    LaunchedEffect(item.data) {
                        item.data.collect { token ->
                            if (showCursor.value && token.isNotBlank()) showCursor.value = false
                            rememberedText.value += " $token"
                        }
                    }
                    if (showCursor.value && !item.isQuestion) {
                        Cursor(cursorColor = MaterialTheme.colorScheme.primary)
                    } else {
                        SimpleMarkdown(rememberedText.value, textModifier)
                    }
                }
            })
    }
}

@Composable
fun Cursor(cursorColor: Color = Color.Black) {
    val cursorWidth = 2.dp
    val cursorHeight = 16.dp
    val cursorColor = cursorColor
    val cursorAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        cursorAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(modifier = Modifier
        .padding(start = 8.dp, top = 4.dp)
        .size(cursorWidth, cursorHeight)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = cursorColor,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = 0f, y = size.height),
                strokeWidth = cursorWidth.toPx(),
                cap = StrokeCap.Round,
                alpha = cursorAnim.value
            )
        }
    }
}


