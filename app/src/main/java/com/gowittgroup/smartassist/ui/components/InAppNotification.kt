package com.gowittgroup.smartassist.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import kotlinx.coroutines.delay

enum class NotificationType {
    ERROR,
    SUCCESS,
    WARNING,
    NONE
}

val WarningColor = Color(0xFFFFC107)
val SuccessColor = Color(0xFF4CAF50)
val ErrorColor = Color(0xFFD32F2F)

@Composable
internal fun AutoDismissibleInAppNotification(
    type: NotificationType = NotificationType.NONE,
    message: String = "",
    onClose: () -> Unit = {},
    autoDismissDurationMillis: Long = 3000
) {
    val animationDuration: Long = 300
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = message) {
        isVisible = true
        delay(autoDismissDurationMillis)
        isVisible = false
        delay(animationDuration)
        onClose()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = animationDuration.toInt())
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = animationDuration.toInt())
        )
    ) {
        InAppNotification(type, message, null, null)
    }
}


@Composable
internal fun InAppNotification(
    type: NotificationType = NotificationType.NONE,
    message: String = "",
    onClose: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
) {

    Surface(
        shadowElevation = 8.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 64.dp)
                .fillMaxWidth()
                .background(color = getBackgroundColor(notificationType = type))
                .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = message,
                maxLines = 3,
                style = MaterialTheme.typography.titleMedium.copy(color = getTextColor(type)),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f)
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onRetry != null) {
                    OutlinedButton(onClick = { onRetry() }) {
                        Text(
                            stringResource(R.string.retry),
                            style = TextStyle(color = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                if (onClose != null) {
                    IconButton(onClick = {
                        onClose()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            tint = getTextColor(notificationType = type)
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun getTextColor(notificationType: NotificationType): Color {
    return when (notificationType) {
        NotificationType.NONE -> MaterialTheme.colorScheme.onSurface
        NotificationType.ERROR -> Color.White
        NotificationType.SUCCESS -> Color.White
        NotificationType.WARNING -> Color.Black
    }
}

@Composable
private fun getBackgroundColor(notificationType: NotificationType): Color {
    return when (notificationType) {
        NotificationType.NONE -> MaterialTheme.colorScheme.surface
        NotificationType.ERROR -> ErrorColor
        NotificationType.SUCCESS -> SuccessColor
        NotificationType.WARNING -> WarningColor
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun InAppNotificationPreview(@PreviewParameter(ErrorNotificationProvider::class) data: InAppNotificationData) {
    SmartAssistTheme {
        InAppNotification(
            message = data.message,
            type = data.type,
            onClose = {}
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AutoFadeInAppNotificationPreview(@PreviewParameter(ErrorNotificationProvider::class) data: InAppNotificationData) {
    SmartAssistTheme {
        AutoDismissibleInAppNotification(
            message = data.message,
            type = data.type
        )
    }
}


class ErrorNotificationProvider : PreviewParameterProvider<InAppNotificationData> {
    override val values: Sequence<InAppNotificationData> = sequenceOf(
        InAppNotificationData(
            type = NotificationType.ERROR,
            message = "Network error. Please try again.",
            onRetry = { /* Retry logic */ }
        ),
        InAppNotificationData(
            type = NotificationType.SUCCESS,
            message = "Api call successful."
        ),

        InAppNotificationData(
            type = NotificationType.WARNING,
            message = "Stay alert"
        ),

        InAppNotificationData(
            type = NotificationType.NONE,
            message = "Let's play.\nLet's play.\nLet's play."
        ),

        InAppNotificationData(
            type = NotificationType.NONE,
            message = "Auto dismiss dialog"
        )
    )
}


data class InAppNotificationData(
    val type: NotificationType = NotificationType.NONE,
    val message: String = "",
    val onClose: () -> Unit = {},
    val onRetry: (() -> Unit)? = null,
    val autoDismissDurationMillis: Long = 3000,
    val isAutoDismiss: Boolean = false
)