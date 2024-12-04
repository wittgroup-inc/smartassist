package com.gowittgroup.smartassist.ui.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.ui.components.textfields.ErrorText

@Composable
internal fun GenderRadioGroup(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    placeholderText: String,
    error: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = placeholderText, style = MaterialTheme.typography.bodyMedium)

        Row {
            options.forEach { label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = value == label,
                        onClick = { onValueChange(label) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = label)
                }
            }
        }

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            ErrorText(error = error)
        }

    }
}