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

    init {
        if (conversationHistoryId == null) {
            history = ConversationHistory(conversationId = getId(), conversations = mutableListOf())
        } else {
            loadConversations(conversationHistoryId)
        }
        if (prompt != null && prompt != "none") {
            ask(prompt, null)
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

    private fun toConversation(conversation: ConversationEntity): Conversation =
        Conversation(
            isQuestion = conversation.isQuestion,
            data = MutableStateFlow(conversation.data),
            isTyping = false
        )


    fun refreshAll() {
        viewModelScope.launch {
            val readAloudDeferred = async { settingsRepository.getReadAloud() }
            val readAloud = readAloudDeferred.await().successOr(false)
            _uiState.value?.readAloud?.value = readAloud
        }
    }

    private fun loadAnswer(state: MutableLiveData<HomeUiState>, query: String, speak: ((content: String) -> Unit)? = null) {
        state.value = state.value?.let { state ->
            val newConversation = Conversation(isQuestion = false, data = MutableStateFlow(""), isLoading = true)
            state.copy(conversations = addToConversationList(state.conversations, listOf(newConversation)))
        }
        if (!networkUtil.isDeviceOnline()) {
            state.value = state.value?.let { it ->
                it.copy(conversations = updateLastConversationLoadingStatus(it.conversations, false))
            }
            state.value?.error?.value = translations.noInternetConnectionMessage()
            return
        }
        viewModelScope.launch {
            state.value = state.value?.copy(showLoading = true)
            when (val result = answerRepository.getReply(query)) {
                is Resource.Error -> {
                    Log.d(TAG, "Something went wrong")
                    state.value?.error?.value = translations.unableToGetReply()
                }
                is Resource.Success -> {
                    val completeReplyBuilder: StringBuilder = StringBuilder()
                    result.data.collect { data ->
                        Log.d(TAG, "Collect: $data")
                        handleQueryResultStream(completeReplyBuilder, state, query, data, speak)
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
        query: String,
        data: StreamResource<String>,
        speak: ((content: String) -> Unit)? = null
    ) {
        when (data) {
            is StreamResource.Error -> {
                Log.d(TAG, "Unable to get reply. Something went wrong")
                state.value?.error?.value = translations.unableToGetReply()
                state.value = state.value?.let { it ->
                    it.copy(conversations = updateLastConversationLoadingStatus(it.conversations, false))
                }
            }
            is StreamResource.StreamStarted -> {
                onStreamStarted(state, data, completeReplyBuilder)
            }

            is StreamResource.StreamInProgress -> {
                onStreamInProgress(state, data, completeReplyBuilder)
            }

            is StreamResource.StreamCompleted -> onStreamCompleted(state, query, completeReplyBuilder.toString(), speak)
        }
    }

    private fun updateLastConversationLoadingStatus(conversations: List<Conversation>, isLoading: Boolean): List<Conversation> {
        val updatedConversation = conversations.last().copy(isLoading = isLoading)
        val newConversations = conversations.toMutableList()
        newConversations.removeLast()
        newConversations.add(updatedConversation)
        return newConversations
    }

    private fun onStreamCompleted(
        homeUiState: MutableLiveData<HomeUiState>,
        query: String,
        completeReply: String,
        speak: ((content: String) -> Unit)? = null
    ) {
        homeUiState.value = homeUiState.value?.let { it ->
            it.copy(conversations = updateLastConversationLoadingStatus(it.conversations, false))
        }
        homeUiState.value?.let { state ->
            state.conversations.last().data.value = completeReply
            history = history.copy(
                // save to history
                conversations = addToConversationEntityList(
                    history.conversations, listOf(
                        ConversationEntity(isQuestion = true, data = query),
                        ConversationEntity(isQuestion = false, data = completeReply)
                    )
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
        // Log.d(TAG, "StreamStarted : ${data.startedOr("")}")
        // state.value?.conversations?.last()?.data?.emit(data.startedOr(""))
        stringBuilder.append(data.startedOr(""))
    }

    private fun onStreamInProgress(
        state: MutableLiveData<HomeUiState>,
        data: StreamResource.StreamInProgress<String>,
        stringBuilder: StringBuilder
    ) {
        //Log.d(TAG, "StreamInProgress: ${data.inProgressOr("")}")
        //state.value?.conversations?.last()?.data?.emit(data.inProgressOr(""))
        stringBuilder.append(data.inProgressOr(""))
    }

    fun ask(question: String, speak: ((content: String) -> Unit)? = null) {
        _uiState.value =
            _uiState.value?.let { state ->
                state.copy(
                    conversations = addToConversationList(
                        state.conversations,
                        listOf(Conversation(isQuestion = true, data = MutableStateFlow(question)))
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






