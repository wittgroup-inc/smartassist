package com.gowittgroup.smartassist.ui.summary

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun FilePickerScreen(onFilesSelected: (List<Uri>) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        val realUris = uris.mapNotNull { uri ->
            getFileFromUri(context, uri)?.absolutePath
        }
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


private fun getFileFromUri(context: Context, uri: Uri): File? {
    val fileName = getFileName(context, uri) ?: return null
    val file = File(context.cacheDir, fileName)

    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }

    return file
}

private fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
            name = cursor.getString(columnIndex)
        }
    }
    return name
}

