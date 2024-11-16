package com.gowittgroup.smartassist.ui.components.textfields

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClickablePrimaryTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClick: ()-> Unit,
    placeholderText: String,
    leadingIcon: ImageVector,
    contentDescription: String = "",
    isSingleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    readOnly: Boolean = false,
    error: String = "",
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = isSingleLine,
            modifier = modifier
                .fillMaxWidth(),
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                onClick()
                            }
                        }
                    }
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            placeholder = { Text(text = placeholderText) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = contentDescription
                )
            },
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            readOnly = readOnly,
            isError = error.isNotBlank()
        )
    }

    if (error.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        ErrorText(error)
    }
}