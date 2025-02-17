package com.gowittgroup.smartassist.ui.summary

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.gowittgroup.core.logger.SmartLog
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

@Composable
fun DocumentProcessingScreen() {
    var summary by remember { mutableStateOf("Select documents to summarize...") }
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        FilePickerScreen { uris ->
            selectedUris = uris
        }

        Button(onClick = {
            if (selectedUris.isNotEmpty()) {
                val pdfUris = selectedUris.filter { it.toString().endsWith(".pdf") }
                val imageUris = selectedUris.filter { !it.toString().endsWith(".pdf") }

                extractTextFromPdf(context, pdfUris) { pdfText ->
                    extractTextFromImages(context, imageUris) { imageText ->
                        val fullText = pdfText + "\n" + imageText
                        summarizeText(fullText) { result ->
                            summary = result
                        }
                    }
                }
            }
        }) {
            Text("Summarize Documents")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(summary, fontSize = 16.sp, modifier = Modifier.padding(8.dp))
    }

}

fun extractTextFromImages(context: Context, imageUris: List<Uri>, onResult: (String) -> Unit) {
    val extractedTexts = mutableListOf<String>()
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    imageUris.forEachIndexed { index, uri ->
        val image = InputImage.fromFilePath(context, uri)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                extractedTexts.add(visionText.text)
                if (index == imageUris.size - 1) {
                    onResult(extractedTexts.joinToString("\n")) // Combine all pages
                }
            }
            .addOnFailureListener { e ->
                SmartLog.e("OCR", "Failed to extract text from image: $uri", e)
            }
    }
}

fun extractTextFromPdf(context: Context, pdfUris: List<Uri>, onResult: (String) -> Unit) {
    val extractedTexts = mutableListOf<String>()

    pdfUris.forEachIndexed { index, uri ->
        val inputStream = context.contentResolver.openInputStream(uri)
        val pdfDocument = PDDocument.load(inputStream)
        val pdfStripper = PDFTextStripper()

        val text = pdfStripper.getText(pdfDocument)
        extractedTexts.add(text)

        pdfDocument.close()

        if (index == pdfUris.size - 1) {
            onResult(extractedTexts.joinToString("\n")) // Combine all pages
        }
    }
}

//fun convertPdfToImages(context: Context, pdfUri: Uri, onResult: (List<Bitmap>) -> Unit) {
//    val inputStream = context.contentResolver.openInputStream(pdfUri)
//    val pdfDocument = PDDocument.load(inputStream)
//    val renderer = PDFRenderer(pdfDocument)
//
//    val images = mutableListOf<Bitmap>()
//    for (i in 0 until minOf(pdfDocument.numberOfPages, 10)) { // Max 10 pages
//        val image = renderer.renderImageWithDPI(i, 300f, ImageType.RGB)
//        images.add(image)
//    }
//
//    pdfDocument.close()
//    onResult(images)
//}

fun summarizeText(text: String, onResult: (String) -> Unit) {
    val apiKey = "YOUR_OPENAI_API_KEY"
    val url = "https://api.openai.com/v1/chat/completions"

    val json = """
        {
            "model": "gpt-4",
            "messages": [
                {"role": "system", "content": "Summarize the following document in simple language with key points."},
                {"role": "user", "content": "$text"}
            ]
        }
    """.trimIndent()
}


