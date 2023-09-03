package com.gowittgroup.smartassist.ui.homescreen

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.gowittgroup.smartassist.models.Conversation
import com.gowittgroup.smartassist.ui.homescreen.HomeUiState.Companion.getId
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.db.entities.ConversationHistory
import com.gowittgroup.smartassistlib.models.*
import com.gowittgroup.smartassistlib.repositories.AnswerRepository
import com.gowittgroup.smartassistlib.repositories.ConversationHistoryRepository
import com.gowittgroup.smartassistlib.repositories.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

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
        val DEFAULT =
            HomeUiState(
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
        if (conversationHistoryId == null || conversationHistoryId == -1L) {
            history = ConversationHistory(conversationId = getId(), conversations = mutableListOf())
        } else {
            isFistMessage = false
            loadConversations(conversationHistoryId)
        }
        if (prompt != null && prompt != "none") {
            val prompts = prompt.split(Prompts.JOINING_DELIMITER)
            system = prompts[0]
            _uiState.value?.textFieldValue?.value = TextFieldValue(prompts[1])

        } else {
            _uiState.value?.textFieldValue?.value = TextFieldValue("")
        }
        refreshAll()
    }

    private fun loadConversations(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            history = historyRepository.getConversationById(id)
                .successOr(ConversationHistory(conversationId = getId(), conversations = mutableListOf()))
            viewModelScope.launch(Dispatchers.Main) {
                _uiState.value = _uiState.value?.let { state ->
                    state.copy(
                        conversations = addToConversationList(state.conversations, history.conversations.map(::toConversation))
                    )
                }
            }
        }
    }

    private fun toConversation(entity: ConversationEntity): Conversation = with(entity) {
        Conversation(
            id = id ?: UUID.randomUUID().toString(),
            isQuestion = isQuestion,
            data = data,
            stream = MutableStateFlow(data),
            isTyping = false,
            forSystem = forSystem,
            referenceId = referenceId ?: ""
        )
    }

    private fun toConversationEntity(conversation: Conversation): ConversationEntity = with(conversation) {
        ConversationEntity(
            id = conversation.id,
            isQuestion = isQuestion,
            data = conversation.data,
            forSystem = forSystem,
            referenceId = referenceId
        )
    }


    fun refreshAll() {
        viewModelScope.launch {
            val readAloudDeferred = async { settingsRepository.getReadAloud() }
            val readAloud = readAloudDeferred.await().successOr(false)
            _uiState.value?.readAloud?.value = readAloud
        }
    }

    private fun loadAnswer(state: MutableLiveData<HomeUiState>, question: Conversation, speak: ((content: String) -> Unit)? = null) {
        if (!networkUtil.isDeviceOnline()) {
            state.value = state.value?.let { it ->
                it.copy(conversations = updateConversationLoadingStatus(it.conversations, question.referenceId, false))
            }
            state.value?.error?.value = translations.noInternetConnectionMessage()
            return
        }
        viewModelScope.launch {
            state.value = state.value?.copy(showLoading = true)
            val lastIndex = state.value?.let { it.conversations.size - 1 } ?: 0
            when (val result = answerRepository.getReply(
                state.value?.conversations?.subList(0, lastIndex)?.map(::toConversationEntity) ?: emptyList()
            )) {
                is Resource.Error -> {
                    Log.d(TAG, "Something went wrong")
                    state.value?.error?.value = translations.unableToGetReply()
                }
                is Resource.Success -> {
                    val completeReplyBuilder: StringBuilder = StringBuilder()
                    result.data.collect { data ->
                        Log.d(TAG, "Collect: $data")
                        handleQueryResultStream(completeReplyBuilder, state, question, data, speak)
                    }
                }
                is Resource.Loading -> Log.d(TAG, "Loading")
            }
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
            is StreamResource.Error -> {
                Log.d(TAG, "Unable to get reply. Something went wrong")
                state.value?.error?.value = translations.unableToGetReply()
                state.value = state.value?.let { it ->
                    it.copy(conversations = updateConversationLoadingStatus(it.conversations, query.referenceId, false))
                }
            }
            is StreamResource.StreamStarted -> {
                onStreamStarted(state, data, completeReplyBuilder)
            }

            is StreamResource.StreamInProgress -> {
                onStreamInProgress(state, query, data, completeReplyBuilder)
            }

            is StreamResource.StreamCompleted -> onStreamCompleted(state, query, completeReplyBuilder.toString(), speak)
        }
    }

    private fun updateConversationLoadingStatus(conversations: List<Conversation>, id: String, isLoading: Boolean): List<Conversation> {
        try {
            val updatedConversation = conversations.first { it.id == id }.copy(isLoading = isLoading)
            val index = conversations.indexOfFirst { it.id == id }
            val mutableConversations = conversations.toMutableList()
            mutableConversations[index] = updatedConversation
            return mutableConversations
        } catch (e: NoSuchElementException) {
            Log.d(TAG, "Unable to update status element not found")
        }
        return conversations
    }

    private fun updateConversation(conversations: List<Conversation>, conversation: Conversation): List<Conversation> {
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

    private fun updateConversationData(conversations: List<Conversation>, id: String, data: String): List<Conversation> {
        try {
            val updatedConversation = conversations.first { it.id == id }.copy(data = data)
            val index = conversations.indexOfFirst { it.id == id }
            val newConversations = conversations.toMutableList()
            newConversations[index] = updatedConversation
            return newConversations
        } catch (e: NoSuchElementException) {
            Log.d(TAG, "Unable to update data element not found")
        }
        return conversations
    }

    private fun onStreamCompleted(
        homeUiState: MutableLiveData<HomeUiState>,
        question: Conversation,
        completeReply: String,
        speak: ((content: String) -> Unit)? = null
    ) {
        homeUiState.value = homeUiState.value?.let { it ->
            it.copy(conversations = updateConversationLoadingStatus(it.conversations, question.referenceId, false))
        }

        homeUiState.value = homeUiState.value?.let { it ->
            it.copy(conversations = updateConversationData(it.conversations, question.referenceId, completeReply))
        }

        homeUiState.value?.let { state ->
            // save to history
            val currentHistory = mutableListOf<ConversationEntity>()
            if (isFistMessage) {
                isFistMessage = false
                currentHistory.add(
                    ConversationEntity(id = UUID.randomUUID().toString(), data = system, forSystem = true)
                )
            }
            currentHistory.addAll(
                listOf(toConversationEntity(question), toConversationEntity(uiState.value!!.conversations.last()))
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
        data: StreamResource.StreamStarted<String>,
        stringBuilder: StringBuilder
    ) {
        state.value = state.value?.copy(showLoading = false)
        viewModelScope.launch {
            state.value?.conversations?.last()?.stream?.emit(data.startedOr(""))
        }
        stringBuilder.append(data.startedOr(""))
    }

    private fun onStreamInProgress(
        state: MutableLiveData<HomeUiState>,
        question: Conversation,
        data: StreamResource.StreamInProgress<String>,
        stringBuilder: StringBuilder
    ) {
        state.value = state.value?.let { it ->
            it.copy(conversations = updateConversationLoadingStatus(it.conversations, question.referenceId, false))
        }
        stringBuilder.append(data.inProgressOr(""))
        viewModelScope.launch {
            state.value?.conversations?.last()?.stream?.emit(stringBuilder.toString())
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

        _uiState.value =
            _uiState.value?.let { state ->
                val conversations = mutableListOf<Conversation>()
                if (isFistMessage) {
                    conversations.add(Conversation(id = UUID.randomUUID().toString(), stream = MutableStateFlow(system), forSystem = true))
                }
                conversations.add(question)
                conversations.add(answer)
                state.copy(
                    conversations = addToConversationList(
                        state.conversations,
                        conversations
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
        _uiState.value = uiState.value?.copy(micIcon = false)
        _uiState.value = uiState.value?.copy(hint = translations.tapAndHoldToSpeak())
    }

    fun setReadAloud(isOn: Boolean) {
        _uiState.value?.readAloud?.value = isOn
    }

    private fun addToConversationList(toList: List<Conversation>, fromList: List<Conversation>) = toList.toMutableList().apply { addAll(fromList) }
    private fun addToConversationEntityList(toList: List<ConversationEntity>, fromList: List<ConversationEntity>) =
        toList.toMutableList().apply { addAll(fromList) }

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
                return HomeViewModel(answerRepository, settingsRepository, historyRepository, conversationId, prompt, networkUtil, translations) as T
            }
        }

        private val TAG: String = HomeViewModel::class.java.simpleName
    }
}






