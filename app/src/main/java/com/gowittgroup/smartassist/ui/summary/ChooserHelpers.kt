package com.gowittgroup.smartassist.ui.summary

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.gowittgroup.core.logger.SmartLog

const val MAX_NUMBER_OF_FILES = 10

@Composable
fun rememberScannerLauncher(
    scannedImages: (List<Uri>) -> Unit,
    mergedPdf: (List<Uri?>) -> Unit = {}
): ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> {
    var scannedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)

            // Handle image pages
            scanResult?.pages?.let { pages ->
                scannedUris = pages.mapNotNull { it.imageUri }
            }

            // Handle PDF result
            scanResult?.pdf?.let { pdf ->
                pdfUri = pdf.uri
                SmartLog.d("PDF Scan", "PDF URI: $pdfUri, Pages: ${pdf.pageCount}")
            }

            scannedImages(scannedUris)
            mergedPdf(listOf(pdfUri))
        }
    }
}

@Composable
fun rememberDocumentScanner(limit: Int = MAX_NUMBER_OF_FILES): GmsDocumentScanner {
    val options = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setPageLimit(limit)
        .setResultFormats(
            GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
            GmsDocumentScannerOptions.RESULT_FORMAT_PDF
        )
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .build()

    return GmsDocumentScanning.getClient(options)
}


@Composable
fun rememberGalleryLauncher(onSelectFiles: (List<Uri>) -> Unit, limit: Int = MAX_NUMBER_OF_FILES) =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            onSelectFiles(uris.take(limit)) // Limit to 10 files
        }
    }
