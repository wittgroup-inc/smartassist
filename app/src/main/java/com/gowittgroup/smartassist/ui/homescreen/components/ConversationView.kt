package com.gowittgroup.smartassist.ui.homescreen.components

import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.ui.components.SimpleMarkdown
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassist.util.lightBackgroundColor
import com.gowittgroup.smartassist.util.share
import com.gowittgroup.smartassistlib.models.ai.AiTools
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "ConversationView"

@Composable
internal fun ConversationView(
    modifier: Modifier,
    list: List<Conversation>,
    listState: LazyListState,
    onCopy: (text: String) -> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier.fillMaxHeight(),
        state = listState
    ) {
        itemsIndexed(
            items = list,
            itemContent = { index, item ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (item.isQuestion) lightBackgroundColor() else Color.Transparent)
                        .padding(top = 8.dp, bottom = 8.dp)

                ) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = if (item.isQuestion) R.drawable.ic_user else R.drawable.ic_bot),
                                contentDescription = if (item.isQuestion) stringResource(R.string.user_ic_desc) else stringResource(
                                    R.string.bot_icon_desc
                                ),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),

                                )
                            if (item.replyFrom != AiTools.NONE) {
                                Text(
                                    text = item.replyFrom.displayName,
                                    style = TextStyle(
                                        fontSize = 8.sp
                                    )
                                )
                            }

                        }


                        val textModifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 16.dp)

                        val rememberedText = item.stream.collectAsState()
                        val showCursor = remember { mutableStateOf(true) }

                        showCursor.value = item.isLoading && !item.isQuestion

                        val showPen = !item.isQuestion && item.isTyping

                        if (showCursor.value) {
                            Cursor(cursorColor = MaterialTheme.colorScheme.primary)
                        } else {
                            SimpleMarkdown(
                                if (showPen) rememberedText.value + "✍\uD83C\uDFFC" else rememberedText.value,
                                if (!item.isQuestion) textModifier.pointerInput(Unit) {
                                    detectTapGestures(onDoubleTap = {
                                        onCopy(rememberedText.value)
                                    })
                                } else textModifier)
                        }


                    }
                    if (!item.isQuestion && item.data.isNotBlank()) {
                        ConversationBottomSection(onCopy, item, context)
                        HorizontalDivider(thickness = 10.dp, color = Color.Transparent)
                    }
                }

            })
    }
}

@Composable
private fun ConversationBottomSection(
    onCopy: (text: String) -> Unit,
    item: Conversation,
    context: Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        val modifier = Modifier
            .size(32.dp)
            .border(
                BorderStroke(1.dp, color = Color.Black),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)

        IconButton(
            onClick = {
                onCopy(item.data)

            }
        ) {
            Icon(
                Icons.Default.ContentCopy,

                modifier = modifier,
                contentDescription = stringResource(R.string.copy)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            onClick = {
                context.share(
                    item.data, "Reply",
                    "Choose"
                )
            }
        ) {
            Icon(
                Icons.Default.Share,
                modifier = modifier,
                contentDescription = stringResource(R.string.share)
            )
        }
    }
}

@Composable
fun Cursor(cursorColor: Color = Color.Black) {
    val cursorWidth = 2.dp
    val cursorHeight = 16.dp
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

    Box(
        modifier = Modifier
            .padding(start = 8.dp, top = 4.dp)
            .size(cursorWidth, cursorHeight)
    ) {
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


@Preview("ConversationViewPreview")
@Preview("ConversationViewPreview contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ConversationViewPreview() {
    SmartAssistTheme {
        ConversationView(
            modifier = Modifier,
            listState = LazyListState(),
            onCopy = {},
            list = listOf(
                Conversation(
                    id = "-2",
                    stream = MutableStateFlow("What is photo synthesis?"),
                    isQuestion = true
                ),
                Conversation(
                    id = "-1",
                    stream = MutableStateFlow("Photosynthesis is a process of.")
                )

            )
        )
    }

}


