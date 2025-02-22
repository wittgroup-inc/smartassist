package com.gowittgroup.smartassist.ui.summary

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassist.util.isImage
import com.gowittgroup.smartassist.util.isPdf
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

    private fun processDocuments(context: Context, uris: List<Uri>) {
        uiState.value.copy(processingIsInProgress = true).applyStateUpdate()
        viewModelScope.launch {
            val pdfUris = uris.filter { it.isPdf(context) }
            val imageUris = uris.filter { it.isImage(context) }

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
                is Resource.Error -> updateErrorToUiState(res.exception.message ?: "")

                is Resource.Success -> res.data.buffer().collect { data ->
                    SmartLog.d(TAG, "Collect: $data")
                    handleQueryResultStream(completeReplyBuilder, data)
                }
            }

        }
    }

    override fun getDefaultState(): SummaryUiState = SummaryUiState()

    override fun processIntent(intent: SummaryIntent) {
        when (intent) {
            is SummaryIntent.FilesSelected -> uiState.value.copy(selectedFiles = intent.selectedFiles)
                .applyStateUpdate()

            is SummaryIntent.ProcessFiles -> processDocuments(
                context = intent.context,
                uiState.value.selectedFiles
            )

            is SummaryIntent.RemoveFileFromList -> uiState.value.copy(
                selectedFiles = removeFile(
                    intent.uri
                )
            ).applyStateUpdate()
        }
    }

    private fun removeFile(uri: Uri): List<Uri> {
        val mutableList = uiState.value.selectedFiles.toMutableList()
        mutableList.remove(uri)
        return mutableList
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
                    processingIsInProgress = false,
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
            processingIsInProgress = false,
            error = message
        ).applyStateUpdate()
    }

    companion object {
        private val TAG: String = SummaryViewModel::class.java.simpleName
    }
}