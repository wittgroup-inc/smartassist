package com.gowittgroup.smartassist.ui.homescreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@Composable
internal fun HandsFreeModeNotification(
    message: String = "",
    onOk: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Surface(
        shadowElevation = 8.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
        ) {

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f)
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { onOk() }) {
                    Text(
                        stringResource(R.string.ok),
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = {
                    onCancel()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }
    }
}

@Composable
fun CustomAlertDialog(
    title: String = "",
    message: String = "",
    onOk: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = { /*TODO*/ }, properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.background)
                .padding(vertical = 16.dp)
        ) {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider(Modifier.height(1.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedButton(onClick = {
                    onCancel()
                }) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onOk() }) {
                    Text(stringResource(R.string.ok))
                }
            }
        }

    }
}


@Preview
@Composable
fun AlertDialogPreview() {
    SmartAssistTheme {
        CustomAlertDialog(title = "Title", message = "Message to show on dialog")
    }
}

@Preview
@Composable
fun HandsFreeModeNotificationPreview() {
    SmartAssistTheme {
        HandsFreeModeNotification(stringResource(id = R.string.hands_free_alert_dialog_message))
    }
}