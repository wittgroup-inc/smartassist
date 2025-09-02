package com.gowittgroup.smartassist.ui.summary

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassist.services.ExtractHelper
import com.gowittgroup.smartassist.util.Session
import com.gowittgroup.smartassist.util.isImage
import com.gowittgroup.smartassist.util.isPdf
import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.domain.models.initiatedOr
import com.gowittgroup.smartassistlib.domain.repositories.ai.AnswerRepository
import com.gowittgroup.smartassistlib.models.ai.AiTools
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val answerRepository: AnswerRepository,
    private val extractHelper: ExtractHelper
) :
    BaseViewModelWithStateIntentAndSideEffect<SummaryUiState, SummaryIntent, SummarySideEffect>() {

    private fun processDocuments(context: Context, uris: List<Uri>) {
        uiState.value.copy(processingIsInProgress = true).applyStateUpdate()
        viewModelScope.launch {
            val pdfUris = uris.filter { it.isPdf(context) }
            val imageUris = uris.filter { it.isImage(context) }

            val pdfText = extractHelper.extractTextFromPdf(
                context,
                pdfUris,
                limit = uiState.value.maxDocumentLimit
            )
            val imageText = extractHelper.extractTextFromImages(context, imageUris)
            val fullText = "${pdfText}\n${imageText}".trim()
            summarizeText(fullText)
        }
    }


    private fun summarizeText(text: String) {
        viewModelScope.launch {
            val res = answerRepository.getReply(
                listOf(
                    Conversation(
                        id = "1",
                        data =
                        if (uiState.value.documentType.isBlank()) {

                            SUMMARIZE_PROMPT
                        } else {
                            String.format(
                                SUMMARIZE_PROMPT_WITH_DOCUMENT_TYPE,
                                uiState.value.documentType
                            )
                        },
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
            is SummaryIntent.FilesSelected -> {
                uiState.value.copy(
                    summary = "",
                    showSummaryFooter = false,
                    selectedFiles = intent.selectedFiles
                ).applyStateUpdate()
            }

            is SummaryIntent.ProcessFiles -> processDocuments(
                context = intent.context,
                uiState.value.selectedFiles
            )

            is SummaryIntent.RemoveFileFromList -> uiState.value.copy(
                selectedFiles = removeFile(
                    intent.uri
                )
            ).applyStateUpdate()

            is SummaryIntent.DocumentTypeSelected -> uiState.value.copy(documentType = intent.type)
                .applyStateUpdate()

            SummaryIntent.Initialize -> uiState.value.copy(maxDocumentLimit = if (Session.subscriptionStatus) MAX_NUMBER_OF_FILES_LARGE else MAX_NUMBER_OF_FILES_SMALL)
                .applyStateUpdate()
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

            is StreamResource.Initiated -> {
                uiState.value.copy(aiTool = data.initiatedOr(AiTools.NONE).displayName)
                    .applyStateUpdate()
            }

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

            is StreamResource.StreamCompleted -> {
                uiState.value.copy(showSummaryFooter = uiState.value.summary.isNotBlank())
                    .applyStateUpdate()
            }

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
        private const val SUMMARIZE_PROMPT_WITH_DOCUMENT_TYPE =
            "Summarize the following %s in clear and simple language.\n" +
                    "- Capture all essential points without omitting critical details.\n" +
                    "- Keep the summary concise, focusing only on the most important information.\n" +
                    "- Present the output in clean, well-structured bullet points for quick understanding.\n" +
                    "- Eliminate unnecessary jargon, repetition, or filler.\n" +
                    "- Ensure the tone and style are appropriate for the given %s."
        private const val SUMMARIZE_PROMPT = "Summarize the following document in clear, simple language.\n" +
                "- Capture all key points without losing important details.\n" +
                "- Keep the summary concise and easy to understand.\n" +
                "- Present the output in well-structured bullet points.\n" +
                "- Remove unnecessary repetition, jargon, or filler.\n" +
                "- Focus only on the most important insights, facts, and conclusions.\n" +
                "- If helpful, adjust the tone and style to match the document type (e.g., report, email, article)."
        private const val MAX_NUMBER_OF_FILES_SMALL = 2
        private const val MAX_NUMBER_OF_FILES_LARGE = 10
    }
}