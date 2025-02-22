package com.gowittgroup.smartassist.ui.summary

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FilePickerScreen(modifier: Modifier = Modifier, onFilesSelected: (List<Uri>) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            onFilesSelected(uris.take(10)) // Limit to 10 files
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Button(onClick = { launcher.launch(arrayOf("application/pdf", "image/*")) }) {
            Text("Select Documents (Up to 10)")
        }
    }
}
