package com.gowittgroup.smartassist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.util.lightBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onQueryChange: (q: String) -> Unit,
    onCloseSearch: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = value,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        interactionSource = interactionSource,
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = innerTextField,
            interactionSource = interactionSource,
            enabled = true,
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = lightBackgroundColor(),
                unfocusedContainerColor = lightBackgroundColor()
            ),
            shape = RoundedCornerShape(corner = CornerSize(32.dp)),
            visualTransformation = VisualTransformation.None,
            singleLine = true,
            placeholder = {
                Text(
                    text = stringResource(R.string.enter_your_query),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(0.dp)
                )
            },
            leadingIcon = {
                IconButton(
                    onClick = onCloseSearch
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(
                            R.string.close_search
                        )
                    )
                }
            },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.search),
                        modifier = Modifier.clickable(
                            onClick = { onQueryChange("") }
                        )
                    )
                }
            },
            contentPadding = PaddingValues(4.dp)
        )
    }
}

@Preview
@Composable
private fun SearchBarPrev() {
    SearchBar(value = "", onQueryChange = {}, onCloseSearch = {})
}