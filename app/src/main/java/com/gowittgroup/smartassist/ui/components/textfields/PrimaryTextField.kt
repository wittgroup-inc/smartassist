package com.gowittgroup.smartassist.ui.components.textfields

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    leadingIcon: ImageVector? = null,
    contentDescription: String = "",
    isSingleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imeAction: ImeAction = ImeAction.Default,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    readOnly: Boolean = false,
    error: String = "",
    isEnabled: Boolean = true
) {
    Column {
        OutlinedTextField(
            value = value,
            enabled = isEnabled,
            onValueChange = onValueChange,
            singleLine = isSingleLine,
            modifier = modifier
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            placeholder = { Text(text = placeholderText) },

            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = contentDescription
                    )
                }
            } else null,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = imeAction,
                keyboardType = keyboardType
            ),
            keyboardActions = keyboardActions,
            readOnly = readOnly,
            isError = error.isNotBlank()
        )
    }

    if (error.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        ErrorText(error = error)
    }
}

@Composable
fun ErrorText(modifier: Modifier = Modifier, error: String) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = error,
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
    )
}

