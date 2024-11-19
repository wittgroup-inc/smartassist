package com.gowittgroup.smartassist.ui.homescreen.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.models.Conversation

@Composable
fun Menu(
    readAloudInitialValue: MutableState<Boolean>,
    onSpeakerIconClick: (on: Boolean) -> Unit,
    onSettingsIconClick: () -> Unit,
    onShareIconClick: () -> Unit,
    conversations: List<Conversation>
) {

    val volumeOn = remember {
        readAloudInitialValue
    }

    var showShareOption by remember {
        mutableStateOf(false)
    }

    showShareOption = conversations.size > 1

    if (showShareOption) {
        IconButton(onClick = {
            onShareIconClick()
        }) {
            Icon(Icons.Default.Share, "")
        }
    }

    IconButton(onClick = {
        volumeOn.value = !readAloudInitialValue.value
        onSpeakerIconClick(volumeOn.value)
    }) {
        Icon(
            painterResource(if (volumeOn.value) R.drawable.ic_volume_on else R.drawable.ic_volume_off),
            ""
        )
    }

    IconButton(onClick = {
        onSettingsIconClick()
    }) {
        Icon(Icons.Default.Settings, "")
    }
}