package com.gowittgroup.smartassist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.util.lightBackgroundColor

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onQueryChange: (q: String) -> Unit,
    onCloseSearch: () -> Unit
) {


        TextField(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(32.dp)),
            value = value,
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = lightBackgroundColor(),
                unfocusedContainerColor = lightBackgroundColor()
            ),
            placeholder = { Text(text = stringResource(R.string.enter_your_query)) },
            onValueChange = onQueryChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
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

            )

}

@Preview
@Composable
private fun SearchBarPrev() {
    SearchBar(value = "", onQueryChange = {}, onCloseSearch = {})
}