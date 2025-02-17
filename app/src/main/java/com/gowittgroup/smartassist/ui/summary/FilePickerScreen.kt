package com.gowittgroup.smartassist.ui.summary

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun FilePickerScreen(onFilesSelected: (List<Uri>) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            onFilesSelected(uris.take(10)) // Limit to 10 files
        }
    }

    Column {
        Button(onClick = { launcher.launch(arrayOf("application/pdf", "image/*")) }) {
            Text("Select Documents (Up to 10)")
        }
    }
}
