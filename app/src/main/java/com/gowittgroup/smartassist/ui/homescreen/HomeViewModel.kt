package com.gowittgroup.smartassist.ui.homescreen

import android.os.Bundle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.core.BaseViewModelWithStateAndIntent
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.models.toConversation
import com.gowittgroup.smartassist.models.toConversationEntity
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.homescreen.HomeUiState.Companion.getId
import com.gowittgroup.smartassist.ui.homescreen.components.PromptMode
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.models.StreamResource
import com.gowittgroup.smartassistlib.domain.models.inProgressOr
import com.gowittgroup.smartassistlib.domain.models.initiatedOr
import com.gowittgroup.smartassistlib.domain.models.startedOr
import com.gowittgroup.smartassistlib.domain.models.successOr
import com.gowittgroup.smartassistlib.domain.repositories.ai.AnswerRepository
import com.gowittgroup.smartassistlib.domain.repositories.banner.BannerRepository
import com.gowittgroup.smartassistlib.domain.repositories.converstationhistory.ConversationHistoryRepository
import com.gowittgroup.smartassistlib.domain.repositories.settings.SettingsRepository
import com.gowittgroup.smartassistlib.models.ai.AiTools
import com.gowittgroup.smartassistlib.models.prompts.Prompts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


typealias ConversationEntity = com.gowittgroup.smartassistlib.db.entities.Conversation

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val answerRepository: AnswerRepository,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: ConversationHistoryRepository,
    private val bannerRepository: BannerRepository,
    private val networkUtil: NetworkUtil,
    private val translations: HomeScreenTranslations,
    private val savedStateHandle: SavedStateHandle,
    private val analytics: SmartAnalytics
) : BaseViewModelWithStateAndIntent<HomeUiState, HomeIntent>() {

    override fun getDefaultState(): HomeUiState = HomeUiState.DEFAULT

    override fun processIntent(intent: HomeIntent) {
    }

    private lateinit var history: ConversationHistory
    private var isFistMessage: Boolean = true
    private var system = ""

    private val prompt: String? = savedStateHandle["prompt"]
    private val id: String? = savedStateHandle["id"]
    private val conversationHistoryId = id?.toLong()

    private var startCommandModePending: (() -> Unit)? = null

    init {
        loadConversation()
        loadPrompt(prompt)
        loadBanner()
        refreshAll()
        loadTemplates()
    }

    private fun loadPrompt(prompt: String?) {
        if (prompt != null && prompt != "none") {
            val prompts = prompt.split(Prompts.JOINING_DELIMITER)
            system = prompts[0]
            uiState.value.textFieldValue.value = TextFieldValue(prompts[1])
        } else {
            uiState.value.textFieldValue.value = TextFieldValue("")
        }
    }

    private fun loadConversation() {
        if (conversationHistoryId == null || conversationHistoryId == -1L) {
            history = ConversationHistory(conversationId = getId(), conversations = mutableListOf())
        } else {
            isFistMessage = false
            loadConversations(conversationHistoryId)
        }
    }

    private fun loadConversations(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            history = historyRepository.getConversationById(id)
                .successOr(
                    ConversationHistory(
                        conversationId = getId(),
                        conversations = mutableListOf()
                    )
                )
            viewModelScope.launch(Dispatchers.Main) {
                uiState.value.copy(
                    conversations = addToConversationList(
                        uiState.value.conversations,
                        history.conversations.map(ConversationEntity::toConversation)
                    )
                ).applyStateUpdate()
            }
        }
    }

    private fun loadBanner() {
        viewModelScope.launch {
            bannerRepository.getBanner().successOr(MutableSharedFlow(1)).collect {
                uiState.value.copy(
                    banner = it
                ).applyStateUpdate()
            }
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            val readAloudDeferred = async { settingsRepository.getReadAloud() }
            val readAloud = readAloudDeferred.await().successOr(false)

            val handsFreeModeDeferred = async { settingsRepository.getHandsFreeMode() }
            val handsFreeMode = handsFreeModeDeferred.await().successOr(false)

            uiState.value.readAloud.value = readAloud
            uiState.value.handsFreeMode.value = handsFreeMode
        }
    }

    fun setHandsFreeMode() {
        viewModelScope.launch {
            settingsRepository.toggleHandsFreeMode(true)
            refreshAll()
        }
    }

    fun fetchClarifying(userInput: String) {
        viewModelScope.launch {
            answerRepository.getClarifyingQuestions(userInput)
                .successOr(emptyFlow())
                .collect { clarifyingQuestions ->
                    uiState.value.copy(
                        clarifyingQuestion = clarifyingQuestions
                    ).applyStateUpdate()
                }
        }
    }

    fun togglePromptMode() {
        val mode = when (uiState.value.promptMode) {
            PromptMode.NORMAL -> PromptMode.ASSIST
            PromptMode.ASSIST -> PromptMode.TEMPLATE
            PromptMode.TEMPLATE -> PromptMode.NORMAL
        }
        uiState.value.copy(promptMode = mode).applyStateUpdate()
    }


    fun loadTemplates() {
        viewModelScope.launch {
            answerRepository.getTemplates()
                .successOr(emptyFlow())   // unwrap into Flow<List<Template>>
                .collect { templates ->
                    uiState.value.copy(
                        templates = templates
                    ).applyStateUpdate()
                }
        }
    }

    fun onTemplateSelected(templateId: String) {
        uiState.value.copy(selectedTemplate = uiState.value.templates.first { it.id == templateId })
            .applyStateUpdate()
    }

    fun onSuggestionClick(prompt: String) {
        uiState.value.textFieldValue.value = TextFieldValue(prompt)
    }

     fun buildPrompt(
        answers: Map<String, String>
    ) {
        uiState.value.selectedTemplate?.id?.let {
            viewModelScope.launch {
                answerRepository.assemblePrompt(it, answers)
                    .successOr(emptyFlow())
                    .collect { promptAssembly ->
                        uiState.value.textFieldValue.value = TextFieldValue(promptAssembly.assembledPrompt)
                    }
            }
        }
    }

    private fun loadAnswer(
        question: Conversation,
        speak: ((content: String) -> Unit)? = null
    ) {
        if (!networkUtil.isDeviceOnline()) {
            updateErrorToUiState(question, translations.noInternetConnectionMessage())
            return
        }
        viewModelScope.launch {
            updateLoadingToUiState(question)
            val lastIndex = uiState.value.conversations.size - 1

            when (val result = answerRepository.getReply(
                uiState.value.conversations.subList(0, lastIndex)
                    .map(Conversation::toConversationEntity)
            )) {
                is Resource.Error -> updateErrorToUiState(
                    question,
                    translations.unableToGetReply()
                )

                is Resource.Success -> onGetReplySuccess(result, question, speak)
            }
        }
    }

    fun handsFreeModeStartListening(startRecognizer: () -> Unit) {
        startListening { startRecognizer() }

        uiState.value.copy(speechRecognizerState = SpeechRecognizerState.Listening)
            .applyStateUpdate()
        SmartLog.d(TAG, "Set to ${uiState.value.speechRecognizerState}")
    }

    fun handsFreeModeStopListening(stopRecognizer: () -> Unit) {
        stopListening { stopRecognizer() }
        uiState.value.copy(speechRecognizerState = SpeechRecognizerState.Idle).applyStateUpdate()
        SmartLog.d(TAG, "Set to ${uiState.value.speechRecognizerState}")
    }

    fun setCommandMode(setCommandMode: () -> Unit) {
        stopListening { setCommandMode() }
        uiState.value.copy(speechRecognizerState = SpeechRecognizerState.Command).applyStateUpdate()
        SmartLog.d(TAG, "Set to ${uiState.value.speechRecognizerState}")
    }

    fun setCommandModeAfterReply(setCommandMode: () -> Unit) {
        startCommandModePending = setCommandMode
    }

    fun releaseCommandMode(releaseCommandMode: () -> Unit) {
        releaseCommandMode()
        uiState.value.copy(speechRecognizerState = SpeechRecognizerState.Idle).applyStateUpdate()
        SmartLog.d(TAG, "Set to ${uiState.value.speechRecognizerState}")
    }


    private fun updateLoadingToUiState(
        question: Conversation
    ) {
        uiState.value.copy(
            showLoading = true,
            conversations = updateConversationIsLoading(
                uiState.value.conversations,
                question.referenceId,
                true
            )
        ).applyStateUpdate()
    }


    private suspend fun onGetReplySuccess(
        result: Resource.Success<Flow<StreamResource<String>>>,
        question: Conversation,
        speak: ((content: String) -> Unit)?
    ) {
        val completeReplyBuilder: StringBuilder = StringBuilder()
        result.data.buffer().collect { data ->
            SmartLog.d(TAG, "Collect: $data")
            handleQueryResultStream(completeReplyBuilder, question, data, speak)
        }
    }

    private fun updateErrorToUiState(
        question: Conversation,
        message: String
    ) {
        SmartLog.d(TAG, "Something went wrong")
        uiState.value.error.value = message
        uiState.value.copy(
            showLoading = false,
            conversations = updateConversationIsLoading(
                uiState.value.conversations,
                question.referenceId,
                false
            )
        ).applyStateUpdate()
    }


    fun resetErrorMessage() {
        uiState.value.error.value = ""
    }

    private fun handleQueryResultStream(
        completeReplyBuilder: StringBuilder,
        query: Conversation,
        data: StreamResource<String>,
        speak: ((content: String) -> Unit)? = null
    ) {
        when (data) {
            is StreamResource.Error -> updateErrorToUiState(
                query,
                translations.unableToGetReply()
            )

            is StreamResource.Initiated -> onStreamInitiated(query, data)

            is StreamResource.StreamStarted ->
                onStreamStarted(query, data, completeReplyBuilder)

            is StreamResource.StreamInProgress ->
                onStreamInProgress(query, data, completeReplyBuilder)

            is StreamResource.StreamCompleted -> {
                onStreamCompleted(query, completeReplyBuilder.toString(), speak)
            }

            else -> {}
        }
    }


    private fun onStreamCompleted(
        question: Conversation,
        completeReply: String,
        speak: ((content: String) -> Unit)? = null
    ) {
        SmartLog.d(TAG, "Complete Reply: $completeReply")
        uiState.value.copy(conversations = uiState.value.conversations.find { it.id == question.referenceId }
            ?.let { conversation ->
                updateConversation(
                    uiState.value.conversations,
                    conversation.copy(isTyping = false, isLoading = false, data = completeReply)
                )
            } ?: uiState.value.conversations).applyStateUpdate()


        addToHistory(question, speak, completeReply)

        if (uiState.value.readAloud.value) {
            speak?.let { speakModule -> speakModule(completeReply) }
        }

        if (uiState.value.handsFreeMode.value) {
            startCommandModePending?.let { setCommandMode { it() } }
                .also { startCommandModePending = null }
        }
    }

    private fun addToHistory(
        question: Conversation,
        speak: ((content: String) -> Unit)?,
        completeReply: String
    ) {
        val currentHistory = mutableListOf<ConversationEntity>()
        if (isFistMessage) {
            isFistMessage = false
            currentHistory.add(
                ConversationEntity(
                    id = UUID.randomUUID().toString(),
                    data = system,
                    forSystem = true
                )
            )
        }
        currentHistory.addAll(
            listOf(
                question.toConversationEntity(),
                uiState.value.conversations.first { it.id == question.referenceId }
                    .toConversationEntity()
            )
        )
        history = history.copy(
            conversations = addToConversationEntityList(
                history.conversations, currentHistory
            )
        )
        if (uiState.value.readAloud.value) {
            speak?.let { speakModule -> speakModule(completeReply) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.saveConversationHistory(history)
        }
    }

    private fun onStreamInitiated(
        question: Conversation,
        data: StreamResource.Initiated,
    ) {
        logReplyReceivedEvent(analytics, data.initiatedOr(AiTools.NONE))

        uiState.value.copy(
            showLoading = false,
            conversations = uiState.value.conversations.find { it.id == question.referenceId }
                ?.let { conversation ->
                    updateConversation(
                        uiState.value.conversations,
                        conversation.copy(replyFrom = data.initiatedOr(AiTools.NONE))
                    )
                }
                ?: uiState.value.conversations
        ).applyStateUpdate()
    }

    private fun onStreamStarted(
        question: Conversation,
        data: StreamResource.StreamStarted<String>,
        stringBuilder: StringBuilder
    ) {
        uiState.value.copy(
            showLoading = false,
            conversations = uiState.value.conversations.find { it.id == question.referenceId }
                ?.let { conversation ->
                    updateConversation(
                        uiState.value.conversations,
                        conversation.copy(isTyping = true, isLoading = false)
                    )
                }
                ?: uiState.value.conversations
        ).applyStateUpdate()

        viewModelScope.launch {
            SmartLog.d(TAG, "complete OnStarted: ${data.data}")
            uiState.value.conversations.find { it.id == question.referenceId }?.stream?.emit(
                data.startedOr(
                    ""
                )
            )
        }
        stringBuilder.append(data.startedOr(""))
    }

    private fun onStreamInProgress(
        question: Conversation,
        data: StreamResource.StreamInProgress<String>,
        stringBuilder: StringBuilder
    ) {
        SmartLog.d(TAG, "complete Inprogress: ${data.data}")
        stringBuilder.append(data.inProgressOr(""))
        viewModelScope.launch {

            uiState.value.conversations.find { it.id == question.referenceId }?.stream?.emit(
                stringBuilder.toString()
            )
        }
    }

    fun updateHint(hint: String) {
        uiState.value.copy(hint = hint).applyStateUpdate()
    }

    fun ask(q: String? = null, speak: ((content: String) -> Unit)? = null) {
        val query: String = q ?: uiState.value.textFieldValue.value.text
        var question = Conversation(
            id = UUID.randomUUID().toString(),
            isQuestion = true,
            data = query,
            stream = MutableStateFlow(query)
        )
        val answer = Conversation(
            id = UUID.randomUUID().toString(),
            isQuestion = false,
            stream = MutableStateFlow(""),
            isLoading = true,
            referenceId = question.id
        )

        question = question.copy(referenceId = answer.id)


        val conversations = mutableListOf<Conversation>()
        if (isFistMessage) {
            conversations.add(
                Conversation(
                    id = UUID.randomUUID().toString(),
                    stream = MutableStateFlow(system),
                    forSystem = true
                )
            )
        }
        conversations.add(question)
        conversations.add(answer)
        uiState.value.copy(
            conversations = addToConversationList(
                uiState.value.conversations, conversations
            ),
            micIcon = false
        ).applyStateUpdate()

        loadAnswer(question, speak)
        uiState.value.textFieldValue.value = TextFieldValue("")
    }

    fun beginningSpeech() {
        uiState.value.copy(hint = translations.listening()).applyStateUpdate()
        uiState.value.textFieldValue.value = TextFieldValue("")
    }

    fun startListening(startRecognizer: () -> Unit) {
        startRecognizer()
        uiState.value.copy(micIcon = true).applyStateUpdate()
    }

    fun stopListening(stopRecognizer: () -> Unit) {
        stopRecognizer()
        uiState.value.copy(micIcon = false, hint = translations.startTyping()).applyStateUpdate()
    }

    fun setReadAloud(isOn: Boolean) {
        uiState.value.readAloud.value = isOn
    }

    private fun addToConversationList(toList: List<Conversation>, fromList: List<Conversation>) =
        toList.toMutableList().apply { addAll(fromList) }

    private fun addToConversationEntityList(
        toList: List<ConversationEntity>,
        fromList: List<ConversationEntity>
    ) = toList.toMutableList().apply { addAll(fromList) }

    private fun updateConversationIsLoading(
        conversations: List<Conversation>,
        id: String,
        isLoading: Boolean
    ): List<Conversation> {
        try {
            val updatedConversation =
                conversations.first { it.id == id }.copy(isLoading = isLoading)
            val index = conversations.indexOfFirst { it.id == id }
            val mutableConversations = conversations.toMutableList()
            mutableConversations[index] = updatedConversation
            return mutableConversations
        } catch (e: NoSuchElementException) {
            SmartLog.d(TAG, "Unable to update status element not found")
        }
        return conversations
    }

    fun closeHandsFreeAlert() {
        uiState.value.copy(showHandsFreeAlertIsClosed = true).applyStateUpdate()
    }

    private fun updateConversation(
        conversations: List<Conversation>,
        conversation: Conversation
    ): List<Conversation> {
        try {
            val index = conversations.indexOfFirst { it.id == conversation.id }
            val mutableConversations = conversations.toMutableList()
            mutableConversations[index] = conversation
            return mutableConversations
        } catch (e: NoSuchElementException) {
            SmartLog.d(TAG, "Unable to update ReferenceId element not found")
        }
        return conversations
    }

    companion object {
        private val TAG: String = HomeViewModel::class.java.simpleName
    }

    fun oddEven(num: Int): String {
        if (num % 2 == 0) return "EVEN"
        return "ODD"
    }
}

private fun logReplyReceivedEvent(smartAnalytics: SmartAnalytics, replyFrom: AiTools) {
    val bundle = Bundle()
    bundle.putString(
        SmartAnalytics.Param.ITEM_NAME,
        replyFrom.displayName
    )
    smartAnalytics.logEvent(SmartAnalytics.Event.REPLY_RECEIVED, bundle)
}

sealed class SpeechRecognizerState {
    data object Listening : SpeechRecognizerState()
    data object Command : SpeechRecognizerState()
    data object Idle : SpeechRecognizerState()
}



