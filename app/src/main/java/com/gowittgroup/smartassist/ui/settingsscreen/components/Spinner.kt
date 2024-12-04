package com.gowittgroup.smartassist.ui.settingsscreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.settingsscreen.SpinnerItem

@Composable
internal fun <T> Spinner(
    items: List<SpinnerItem<T>>,
    selectedItem: SpinnerItem<T>,
    toolTip: String?,
    onSelection: (selection: T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    var showToolTip by remember { mutableStateOf(false) }

    Column {
        Box(
            Modifier
                .clickable(onClick = { expanded = true })
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = selectedItem.displayName.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )

            Icon(
                Icons.Default.Info,
                contentDescription = stringResource(R.string.info_icon_content_desc),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 40.dp)
                    .clickable(onClick = { showToolTip = !showToolTip }),
            )

            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.drop_down_arrow_content_desc),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        if (showToolTip) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.onBackground)

            ) {
                toolTip?.let {
                    Text(
                        text = toolTip,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    )
                }

            }

        }


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)


        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onSelection(items[selectedIndex].data)
                }, text = {
                    Column {
                        Text(
                            item.displayName.uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        HorizontalDivider()
                    }
                })
            }
        }

    }
}