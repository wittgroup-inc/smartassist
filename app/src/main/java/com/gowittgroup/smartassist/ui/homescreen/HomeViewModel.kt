package com.gowittgroup.smartassist.ui.homescreen

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.models.toConversation
import com.gowittgroup.smartassist.models.toConversationEntity
import com.gowittgroup.smartassist.ui.homescreen.HomeUiState.Companion.getId
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.models.*
import com.gowittgroup.smartassistlib.repositories.AnswerRepository
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepository
import com.gowittgroup.smartassistlib.repositories.SettingsRepository
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.log

typealias ConversationEntity = com.gowittgroup.smartassistlib.db.entities.Conversation


data class HomeUiState(
    val textFieldValue: MutableState<TextFieldValue>,
    val conversations: List<Conversation>,
    val hint: String,
    val showLoading: Boolean,
    val micIcon: Boolean,
    val readAloud: MutableState<Boolean>,
    val error: MutableState<String>
) {
    companion object {
        val DEFAULT = HomeUiState(
            textFieldValue = mutableStateOf(TextFieldValue("")),
            conversations = listOf(),
            hint = "Tap and hold to speak.",
            showLoading = false,
            micIcon = false,
            readAloud = mutableStateOf(false),
            error = mutableStateOf("")
        )

        fun getId() = run { UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE }
    }
}

class HomeViewModel(
    private val answerRepository: AnswerRepository,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: ConversationHistoryRepository,
    private val conversationHistoryId: Long?,
    private val prompt: String?,
    private val networkUtil: NetworkUtil,
    private val translations: HomeScreenTranslations
) : ViewModel() {

    private val _uiState = MutableLiveData(HomeUiState.DEFAULT)
    val uiState: LiveData<HomeUiState> = _uiState
    private lateinit var history: ConversationHistory
    private var isFistMessage: Boolean = true
    private var system = ""

    init {
        loadConversation()
        loadPrompt(prompt)
        refreshAll()
       }

    private fun loadPrompt(prompt: String?) {
        if (prompt != null && prompt != "none") {
            val prompts = prompt.split(Prompts.JOINING_DELIMITER)
            system = prompts[0]
            _uiState.value?.textFieldValue?.value = TextFieldValue(prompts[1])
        } else {
            _uiState.value?.textFieldValue?.value = TextFieldValue("")
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
                _uiState.value = _uiState.value?.let { state ->
                    state.copy(
                        conversations = addToConversationList(
                            state.conversations,
                            history.conversations.map(ConversationEntity::toConversation)
                        )
                    )
                }
            }
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            val readAloudDeferred = async { settingsRepository.getReadAloud() }
            val readAloud = readAloudDeferred.await().successOr(false)
            _uiState.value?.readAloud?.value = readAloud
        }
    }

    private fun loadAnswer(
        state: MutableLiveData<HomeUiState>,
        question: Conversation,
        speak: ((content: String) -> Unit)? = null
    ) {
        if (!networkUtil.isDeviceOnline()) {
            updateErrorToUiState(state, question, translations.noInternetConnectionMessage())
            return
        }
        viewModelScope.launch {



            updateLoadingToUiState(state, question)
            val lastIndex = state.value?.let { it.conversations.size - 1 } ?: 0

            when (val result = answerRepository.getReply(
                state.value?.conversations?.subList(0, lastIndex)?.map(Conversation::toConversationEntity)
                    ?: emptyList()
            )) {
                is Resource.Error -> updateErrorToUiState(
                    state,
                    question,
                    translations.unableToGetReply()
                )

                is Resource.Success -> onGetReplySuccess(result, state, question, speak)
                is Resource.Loading -> Log.d(TAG, "Loading")
            }
        }
    }

    private fun updateLoadingToUiState(
        state: MutableLiveData<HomeUiState>,
        question: Conversation
    ) {
        state.value = state.value?.let {
            it.copy(
                showLoading = true,
                conversations = updateConversationIsLoading(
                    it.conversations,
                    question.referenceId,
                    true
                )
            )
        }
    }


    private suspend fun onGetReplySuccess(
        result: Resource.Success<Flow<StreamResource<String>>>,
        state: MutableLiveData<HomeUiState>,
        question: Conversation,
        speak: ((content: String) -> Unit)?
    ) {
        val completeReplyBuilder: StringBuilder = StringBuilder()
        result.data.buffer().collect { data ->
            Log.d(TAG, "Collect: $data")
            handleQueryResultStream(completeReplyBuilder, state, question, data, speak)
        }
    }

    private fun updateErrorToUiState(
        state: MutableLiveData<HomeUiState>,
        question: Conversation,
        message: String
    ) {
        Log.d(TAG, "Something went wrong")
        state.value?.error?.value = message
        state.value = state.value?.let {
            it.copy(
                showLoading = false,
                conversations = updateConversationIsLoading(
                    it.conversations,
                    question.referenceId,
                    false
                )
            )
        }
    }


    fun resetErrorMessage() {
        _uiState.value?.error?.value = ""
    }

    private fun handleQueryResultStream(
        completeReplyBuilder: StringBuilder,
        state: MutableLiveData<HomeUiState>,
        query: Conversation,
        data: StreamResource<String>,
        speak: ((content: String) -> Unit)? = null
    ) {
        when (data) {
            is StreamResource.Error -> updateErrorToUiState(state, query, translations.unableToGetReply())

            is StreamResource.StreamStarted ->
                onStreamStarted(state, query, data, completeReplyBuilder)

            is StreamResource.StreamInProgress ->
                onStreamInProgress(state, query, data, completeReplyBuilder)

            is StreamResource.StreamCompleted -> {

                onStreamCompleted(state, query, completeReplyBuilder.toString(), speak)
            }

            else -> {}
        }
    }


    private fun onStreamCompleted(
        homeUiState: MutableLiveData<HomeUiState>,
        question: Conversation,
        completeReply: String,
        speak: ((content: String) -> Unit)? = null
    ) {
        Log.d(TAG, "Complete Reply: $completeReply")
        homeUiState.value = homeUiState.value?.let { it ->
            it.copy(conversations = it.conversations.find { it.id == question.referenceId }
                ?.let { conversation ->
                    updateConversation(
                        it.conversations,
                        conversation.copy(isTyping = false, isLoading = false, data = completeReply)
                    )
                } ?: it.conversations)
        }

        addToHistory(homeUiState, question, speak, completeReply)
        homeUiState.value?.let { state ->
            if (state.readAloud.value) {
                speak?.let { speakModule -> speakModule(completeReply) }
            }
        }
    }

    private fun addToHistory(
        homeUiState: MutableLiveData<HomeUiState>,
        question: Conversation,
        speak: ((content: String) -> Unit)?,
        completeReply: String
    ) {
        homeUiState.value?.let { state ->
            // save to history
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
                    uiState.value!!.conversations.first { it.id == question.referenceId }
                        .toConversationEntity()
                )
            )
            history = history.copy(
                conversations = addToConversationEntityList(
                    history.conversations, currentHistory
                )
            )
            if (state.readAloud.value) {
                speak?.let { speakModule -> speakModule(completeReply) }
            }
            viewModelScope.launch(Dispatchers.IO) {
                historyRepository.saveConversationHistory(history)
            }
        }
    }

    private fun onStreamStarted(
        state: MutableLiveData<HomeUiState>,
        question: Conversation,
        data: StreamResource.StreamStarted<String>,
        stringBuilder: StringBuilder
    ) {
        state.value = state.value?.let { it ->
            it.copy(
                showLoading = false,
                conversations = it.conversations.find { it.id == question.referenceId }
                    ?.let { conversation ->
                        updateConversation(
                            it.conversations,
                            conversation.copy(isTyping = true, isLoading = false)
                        )
                    }
                    ?: it.conversations
            )
        }
        viewModelScope.launch {
            Log.d(TAG, "complete OnStarted: ${data.data}")
            state.value?.conversations?.find { it.id == question.referenceId }?.stream?.emit(
                data.startedOr(
                    ""
                )
            )
        }
        stringBuilder.append(data.startedOr(""))
    }

    private fun onStreamInProgress(
        state: MutableLiveData<HomeUiState>,
        question: Conversation,
        data: StreamResource.StreamInProgress<String>,
        stringBuilder: StringBuilder
    ) {
        Log.d(TAG, "complete Inprogress: ${data.data}")
        stringBuilder.append(data.inProgressOr(""))
        viewModelScope.launch {

            state.value?.conversations?.find { it.id == question.referenceId }?.stream?.emit(
                stringBuilder.toString()
            )
        }
    }

    fun ask(query: String, speak: ((content: String) -> Unit)? = null) {
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

        _uiState.value = _uiState.value?.let { state ->
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
            state.copy(
                conversations = addToConversationList(
                    state.conversations, conversations
                ),
                micIcon = false
            )
        }
        loadAnswer(_uiState, question, speak)
        _uiState.value?.textFieldValue?.value = TextFieldValue("")
    }

    fun beginningSpeech() {
        _uiState.value = uiState.value?.copy(hint = translations.listening())
        _uiState.value?.textFieldValue?.value = TextFieldValue("")
    }

    fun startListening() {
        _uiState.value = uiState.value?.copy(micIcon = true)
    }

    fun stopListening() {
        _uiState.value =
            uiState.value?.copy(micIcon = false, hint = translations.tapAndHoldToSpeak())
    }

    fun setReadAloud(isOn: Boolean) {
        _uiState.value?.readAloud?.value = isOn
    }

    private fun addToConversationList(toList: List<Conversation>, fromList: List<Conversation>) = toList.toMutableList().apply { addAll(fromList) }

    private fun addToConversationEntityList(toList: List<ConversationEntity>, fromList: List<ConversationEntity>) = toList.toMutableList().apply { addAll(fromList) }

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
            Log.d(TAG, "Unable to update status element not found")
        }
        return conversations
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
            Log.d(TAG, "Unable to update ReferenceId element not found")
        }
        return conversations
    }

    companion object {
        fun provideFactory(
            answerRepository: AnswerRepository,
            settingsRepository: SettingsRepository,
            historyRepository: ConversationHistoryRepository,
            conversationId: Long?,
            prompt: String?,
            networkUtil: NetworkUtil,
            translations: HomeScreenTranslations
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    answerRepository,
                    settingsRepository,
                    historyRepository,
                    conversationId,
                    prompt,
                    networkUtil,
                    translations
                ) as T
            }
        }

        private val TAG: String = HomeViewModel::class.java.simpleName
    }

    fun oddEven(num: Int): String {
        if (num % 2 == 0) return "EVEN"
        return "ODD"
    }
}






