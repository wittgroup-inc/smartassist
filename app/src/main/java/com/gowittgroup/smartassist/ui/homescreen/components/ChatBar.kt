package com.gowittgroup.smartassist.ui.homescreen.components

import android.content.res.Configuration
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ChatBar(
    state: MutableState<TextFieldValue>,
    hint: String,
    modifier: Modifier,
    icon: Painter,
    actionUp: () -> Unit,
    actionDown: () -> Unit,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .heightIn(min = TextFieldDefaults.MinHeight)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = state.value, shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                onValueChange = {
                    state.value = it
                },

                colors = TextFieldDefaults.colors().copy(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                placeholder = { Text(text = hint) },
                modifier = Modifier
                    .weight(1f),
                maxLines = 4,
            )
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (state.value.text.isEmpty()) {
                    IconButton(onClick = {}, modifier = Modifier.pointerInteropFilter { event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                actionDown()
                                true
                            }

                            MotionEvent.ACTION_UP -> {
                                actionUp()
                                true
                            }

                            else -> false
                        }
                    }) {
                        Icon(
                            painter = icon,
                            contentDescription = stringResource(R.string.mic_icon_content_desc),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                } else {
                    IconButton(onClick = { onClick() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_send),
                            contentDescription = stringResource(R.string.send_icon_desc),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Preview("ChatBar contents")
@Preview("ChatBar contents (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatBar() {
    SmartAssistTheme {
        ChatBar(
            state = remember {
                mutableStateOf(TextFieldValue())
            },
            hint = "Enter text here",
            modifier = Modifier,
            icon = painterResource(id = R.drawable.ic_mic_on),
            actionUp = {},
            actionDown = { },
            onClick = {}
        )
    }
}
