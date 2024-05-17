package com.gowittgroup.smartassist.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onQueryChange: (q: String) -> Unit,
    onCloseSearch: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier) {
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
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(corner = CornerSize(32.dp)),
            value = value,
            placeholder = { Text(text = stringResource(R.string.enter_your_query)) },
            onValueChange = onQueryChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            trailingIcon = {
                if (value.isNotEmpty())
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.search),
                        modifier = Modifier.clickable(
                            onClick = { onQueryChange("") }
                        )
                    )
                else
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search),
                        modifier = Modifier.clickable(
                            onClick = { onQueryChange(value) }
                        )

                    )
            },

        )
    }

}

@Preview
@Composable
private fun SearchBarPrev() {
    SearchBar(value = "", onQueryChange = {}, onCloseSearch = {})
}