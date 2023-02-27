package com.wittgroup.smartassist.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wittgroup.smartassist.Conversation
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.ui.theme.LightGray
import com.wittgroup.smartassist.ui.theme.Purple700

@Composable
fun ConversationView(modifier: Modifier, list: List<Conversation>) {
    LazyColumn(
        modifier = modifier.fillMaxHeight()
    ) {
        items(items = list, itemContent = { item ->
            Log.d("COMPOSE", "This get rendered $item")
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(if (item.isQuestion) LightGray else Color.Transparent)
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = if (item.isQuestion) R.drawable.ic_user else R.drawable.ic_bot),
                    contentDescription = "icon",
                    colorFilter = ColorFilter.tint(Purple700),
                    modifier = Modifier
                        .padding(start = 16.dp)
                )

                val textModifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 16.dp)
                val textStyle = TextStyle(fontSize = 16.sp)
                if (item.isQuestion) {
                    Text(
                        text = item.data,
                        style = textStyle,
                        modifier = textModifier
                    )
                } else {
                    TypingText(
                        text = item.data,
                        style = textStyle,
                        modifier = textModifier
                    )
                }

            }
        })
    }
}



