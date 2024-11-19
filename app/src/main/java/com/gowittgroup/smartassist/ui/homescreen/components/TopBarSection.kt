package com.gowittgroup.smartassist.ui.homescreen.components

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.gowittgroup.smartassist.services.textospeech.SmartTextToSpeech
import com.gowittgroup.smartassist.ui.components.HomeAppBar
import com.gowittgroup.smartassist.ui.homescreen.HomeUiState
import com.gowittgroup.smartassist.ui.homescreen.prepareContent
import com.gowittgroup.smartassist.ui.homescreen.shutdownTextToSpeech
import com.gowittgroup.smartassist.util.share

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBarSection(
    uiState: HomeUiState,
    onSpeakerIconClick: (on: Boolean) -> Unit,
    textToSpeech: MutableState<SmartTextToSpeech>,
    context: Context,
    navigateToSettings: () -> Unit,
    openDrawer: () -> Unit,
    topAppBarState: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    isExpanded: Boolean
) {
    HomeAppBar(
        actions = {
            Menu(
                conversations = uiState.conversations,
                readAloudInitialValue = uiState.readAloud,
                onSpeakerIconClick = { isOn ->
                    onSpeakerIconClick(isOn)
                    if (isOn) {
                        shutdownTextToSpeech(textToSpeech.value)
                        textToSpeech.value = SmartTextToSpeech().apply { initialize(context) }
                    } else {
                        shutdownTextToSpeech(textToSpeech.value)
                    }
                },
                onShareIconClick = {
                    val shareText = prepareContent(uiState.conversations)
                    context.share(shareText.toString(), "Chat History", "Share With")
                },
                onSettingsIconClick = {
                    navigateToSettings()
                })
        }, openDrawer = openDrawer,
        topAppBarState = topAppBarState,
        scrollBehavior = scrollBehavior,
        isExpanded = isExpanded
    )
}