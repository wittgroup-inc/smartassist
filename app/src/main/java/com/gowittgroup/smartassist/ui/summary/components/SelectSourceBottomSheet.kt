package com.gowittgroup.smartassist.ui.summary.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectSourceBottomSheet(onDismiss: () -> Unit, onScan: () -> Unit, onGallery: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        SelectSourceBottomSheetContent(onGallery, onScan)
    }
}

@Composable
private fun SelectSourceBottomSheetContent(onGallery: () -> Unit, onScan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    ) {
        Text("Choose Documents", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        HorizontalDivider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OptionItem(label = "Select from Gallery", onClick = onGallery)
            HorizontalDivider(modifier = Modifier.padding(0.dp))
            OptionItem(label = "Scan Document", onClick = onScan)
        }
    }
}

@Composable
private fun OptionItem(label: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }
        .padding(vertical = 16.dp, horizontal = 16.dp)
       ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = "")
    }
}

@Preview
@Composable
private fun SelectSourceBottomSheetContentPreview() {
    SmartAssistTheme {
        SelectSourceBottomSheetContent(
            onScan = {},
            onGallery = {}
        )
    }
}