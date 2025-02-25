package com.gowittgroup.smartassist.services

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.gowittgroup.core.logger.SmartLog
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Copyright © 2025 WITT Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class ExtractHelper @Inject constructor() {
    suspend fun extractTextFromImages(context: Context, imageUris: List<Uri>): String =
        withContext(Dispatchers.IO) {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val extractedTexts = mutableListOf<String>()

            imageUris.forEach { uri ->
                val image = InputImage.fromFilePath(context, uri)
                try {
                    val visionText = recognizer.process(image).await()
                    extractedTexts.add(visionText.text)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            extractedTexts.joinToString("\n")
        }

    suspend fun extractTextFromPdf(
        context: Context,
        pdfUris: List<Uri>,
        limit: Int = MAX_PAGE_LIMIT
    ): String =
        withContext(Dispatchers.IO) {
            val extractedTexts = mutableListOf<String>()
            PDFBoxResourceLoader.init(context)

            pdfUris.forEach { uri ->
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val pdfDocument = PDDocument.load(inputStream)
                    val pdfStripper = PDFTextStripper()
                    val extractedText = pdfStripper.getText(pdfDocument)

                    if (extractedText.isBlank()) {
                        val deferredText = CompletableDeferred<String>()
                        extractTextFromPdfImages(
                            context = context, pdfUri = uri,
                            limit = limit, onResult = { textFromImages ->
                                deferredText.complete(textFromImages)
                            })
                        extractedTexts.add(deferredText.await()) // Wait for OCR to finish
                    } else {
                        extractedTexts.add(extractedText)
                    }

                    pdfDocument.close() // Close only after processing
                }
            }
            extractedTexts.joinToString("\n")
        }

    private fun extractTextFromPdfImages(
        context: Context,
        pdfUri: Uri,
        onResult: (String) -> Unit,
        limit: Int,
    ) {
        convertPdfToImages(
            context = context, pdfUri = pdfUri, limit = limit, onResult =
            { images ->
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val extractedTexts = mutableListOf<String>()

                images.forEachIndexed { index, bitmap ->
                    val image = InputImage.fromBitmap(bitmap, 0)
                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            extractedTexts.add(visionText.text)
                            if (index == images.size - 1) {
                                onResult(extractedTexts.joinToString("\n")) // Merge text
                            }
                        }
                        .addOnFailureListener { e ->
                            SmartLog.e("OCR", "Failed to extract text", e)
                        }
                }
            })
    }

    private fun convertPdfToImages(
        context: Context,
        pdfUri: Uri,
        onResult: (List<Bitmap>) -> Unit,
        limit: Int
    ) {
        context.contentResolver.openInputStream(pdfUri)?.use { inputStream ->
            PDDocument.load(inputStream).use { pdfDocument -> // Use `use {}` to ensure safe closure
                val renderer = PDFRenderer(pdfDocument)

                val images = mutableListOf<Bitmap>()
                for (i in 0 until minOf(pdfDocument.numberOfPages, limit)) {
                    val image = renderer.renderImageWithDPI(i, 300f, ImageType.RGB)
                    images.add(image)
                }

                onResult(images)
            }
        }
    }


    companion object {
        private const val MAX_PAGE_LIMIT = 10
    }
}