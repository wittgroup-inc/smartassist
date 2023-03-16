package com.wittgroup.smartassist.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wittgroup.smartassist.R
import com.wittgroup.smartassist.models.Conversation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                val text = item.data.collectAsState()
                Text(
                    text = text.value,
                    style = textStyle,
                    modifier = textModifier
                )
            }
        })
    }
}



