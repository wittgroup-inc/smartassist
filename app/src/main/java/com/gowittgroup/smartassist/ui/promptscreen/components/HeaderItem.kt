package com.gowittgroup.smartassist.ui.promptscreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassistlib.models.prompts.Prompts

@Composable
internal fun HeaderItem(prompts: Prompts, isExpanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onClick() })
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = prompts.category.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = prompts.category.descriptions,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp, top = 4.dp)
            )
        }
        IconButton(onClick = { onClick() }, modifier = Modifier.size(24.dp)) {
            Icon(
                if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                if (isExpanded) stringResource(R.string.ic_drop_up_dec) else stringResource(R.string.ic_drop_down_desc)
            )
        }
    }
}