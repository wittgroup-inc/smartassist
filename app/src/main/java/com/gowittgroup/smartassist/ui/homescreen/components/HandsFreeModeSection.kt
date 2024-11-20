package com.gowittgroup.smartassist.ui.homescreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.ui.homescreen.HomeUiState
import com.gowittgroup.smartassist.ui.homescreen.SpeechRecognizerState

private const val TAG = "HandsFreeModeSection"

@Composable
internal fun HandsFreeModeSection(uiState: HomeUiState) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxWidth()
    ) {
        SmartLog.d(TAG, "${uiState.speechRecognizerState}")
        when (uiState.speechRecognizerState) {
            SpeechRecognizerState.Listening -> {
                SmartLog.d(TAG, "HandsFreeMode Start listening")
                ComposeLottieAnimation()
            }


            SpeechRecognizerState.Command -> {

                val message = buildAnnotatedString {
                    append("Say ")
                    withStyle(
                        style = MaterialTheme.typography.titleMedium.toSpanStyle()
                            .copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            )
                    ) {
                        append("Okay buddy")
                    }
                    append(", and then ask your query.")
                }
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            else -> {}
        }
    }
}