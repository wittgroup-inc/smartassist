package com.gowittgroup.smartassist.ui.summary

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.domain.repositories.ai.AnswerRepository
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val answerRepository: AnswerRepository
) :
    BaseViewModelWithStateIntentAndSideEffect<SummaryUiState, SummaryIntent, SummarySideEffect>() {

    fun processDocuments(context: Context, uris: List<Uri>) {
        viewModelScope.launch {
            val pdfUris = uris.filter { isPdf(context, it) }
            val imageUris = uris.filter { isImage(context, it) }

            val pdfText = extractTextFromPdf(context, pdfUris)
            val imageText = extractTextFromImages(context, imageUris)
            val fullText = "${pdfText}\n${imageText}".trim()
            summarizeText(fullText)
        }
    }

    private suspend fun extractTextFromImages(context: Context, imageUris: List<Uri>): String =
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

    private suspend fun extractTextFromPdf(context: Context, pdfUris: List<Uri>): String =
        withContext(Dispatchers.IO) {
            val extractedTexts = mutableListOf<String>()
            PDFBoxResourceLoader.init(context)

            pdfUris.forEach { uri ->
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    PDDocument.load(inputStream).use { pdfDocument ->
                        val pdfStripper = PDFTextStripper()
                        extractedTexts.add(pdfStripper.getText(pdfDocument))
                    }
                }
            }
            extractedTexts.joinToString("\n")
        }

    private fun summarizeText(text: String) {

        viewModelScope.launch {
            val res = answerRepository.getReply(
                listOf(
                    Conversation(
                        id = "1",
                        data = "Summarize the following document in simple language with key points.",
                        forSystem = true
                    ),
                    Conversation(
                        id = "2",
                        data = text
                    )
                )
            )

            val completeReplyBuilder: StringBuilder = StringBuilder()
            when (res) {
                is Resource.Error -> {}

                is Resource.Success -> res.data.buffer().collect { data ->
                    SmartLog.d(TAG, "Collect: $data")
                    handleQueryResultStream(completeReplyBuilder, data)
                }
            }

        }
    }

    private fun isPdf(context: Context, uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri)
        return mimeType == "application/pdf"
    }

    private fun isImage(context: Context, uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri)
        return mimeType?.startsWith("image/") == true
    }

    override fun getDefaultState(): SummaryUiState = SummaryUiState()

    override fun processIntent(intent: SummaryIntent) {
        TODO("Not yet implemented")
    }

    private fun handleQueryResultStream(
        completeReplyBuilder: StringBuilder,
        data: StreamResource<String>
    ) {

        when (data) {
            is StreamResource.Error -> updateErrorToUiState(data.exception.message ?: "")

            is StreamResource.Initiated -> {}

            is StreamResource.StreamStarted -> {
                completeReplyBuilder.append(data.data)
                uiState.value.copy(
                    summary = completeReplyBuilder.toString()
                ).applyStateUpdate()
            }

            is StreamResource.StreamInProgress -> {
                completeReplyBuilder.append(data.data)
                uiState.value.copy(
                    summary = completeReplyBuilder.toString()
                ).applyStateUpdate()
            }

            is StreamResource.StreamCompleted -> {}

            else -> {}
        }
    }

    private fun updateErrorToUiState(
        message: String
    ) {
        SmartLog.d(TAG, "Something went wrong")
        uiState.value.copy(
            error = message
        ).applyStateUpdate()
    }

    companion object {
        private val TAG: String = SummaryViewModel::class.java.simpleName
    }
}