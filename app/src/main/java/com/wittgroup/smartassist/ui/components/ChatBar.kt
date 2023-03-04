package com.wittgroup.smartassist.ui.components

import android.content.res.Resources.Theme
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.ui.theme.LightGray
import com.wittgroup.smartassist.ui.theme.Purple700

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatBar(
    state: MutableState<TextFieldValue>,
    hint: String,
    modifier: Modifier,
    icon: Int,
    actionUp: () -> Unit,
    actionDown: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = LightGray), verticalAlignment = Alignment.CenterVertically

    ) {

        TextField(value = state.value, shape = RoundedCornerShape(0.dp), onValueChange = {
            state.value = it
        }, placeholder = { Text(text = hint) },
            modifier = Modifier
                .weight(1f)
                .background(color = Color.White),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onClick() })
        )
        if (state.value.text.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .padding(8.dp)
                    .clickable(enabled = true, onClick = { }, interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            color = Color.Gray,
                            radius = 52.dp,
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = icon),
                    contentDescription = "icon",
                    colorFilter = ColorFilter.tint(Purple700),
                    modifier = Modifier
                        .pointerInteropFilter {
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    actionDown()
                                }
                                MotionEvent.ACTION_UP -> {
                                    actionUp()
                                }
                                else -> false
                            }
                            true
                        }
                )

            }
        } else {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .padding(8.dp)
                    .clickable(enabled = true, onClick = { onClick() }, interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            color = Color.Gray,
                            radius = 52.dp,
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = "icon",
                    colorFilter = ColorFilter.tint(Purple700),
                )
            }
        }

    }
}
