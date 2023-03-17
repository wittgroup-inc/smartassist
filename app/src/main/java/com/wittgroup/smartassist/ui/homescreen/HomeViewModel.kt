package com.wittgroup.smartassist.ui.homescreen

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.wittgroup.smartassist.models.Conversation
import com.wittgroup.smartassist.ui.homescreen.HomeUiState.Companion.getId
import com.wittgroup.smartassistlib.db.entities.ConversationHistory
import com.wittgroup.smartassistlib.models.*
import com.wittgroup.smartassistlib.repositories.AnswerRepository
import com.wittgroup.smartassistlib.repositories.ConversationHistoryRepository
import com.wittgroup.smartassistlib.repositories.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

typealias ConversationEntity = com.wittgroup.smartassistlib.db.entities.Conversation


data class HomeUiState(
    val textFieldValue: MutableState<TextFieldValue>,
    val conversations: List<Conversation>,
    val hint: String,
    val showLoading: Boolean,
    val micIcon: Boolean,
    val readAloud: MutableState<Boolean>
) {
    companion object {
        val DEFAULT =
            HomeUiState(
                textFieldValue = mutableStateOf(TextFieldValue("")),
                conversations = listOf(),
                hint = "Tap and hold to speak.",
                showLoading = false,
                micIcon = false,
                readAloud = mutableStateOf(false)
            )

        fun getId() = run { UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE }
    }

}

class HomeViewModel(
    private val answerRepository: AnswerRepository,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: ConversationHistoryRepository,
    private val conversationHistoryId: String?
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
        refreshAll()
    }

    private fun loadConversations(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            history = historyRepository.getConversationById(id.toLong())
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
        viewModelScope.launch {
            state.value = state.value?.copy(showLoading = true)
            when (val result = answerRepository.getReply(query)) {
                is Resource.Error -> Log.d("", "")
                is Resource.Success -> {
                    val completeReplyBuilder: StringBuilder = StringBuilder()
                    state.value = state.value?.let { state ->
                        val newConversation = Conversation(isQuestion = false, data = MutableStateFlow(""))
                        state.copy(conversations = addToConversationList(state.conversations, listOf(newConversation)))
                    }
                    result.data.collect { data -> handleQueryResultStream(completeReplyBuilder, state, query, data, speak) }
                }
                is Resource.Loading -> Log.d(TAG, "Loading")
            }
        }
    }

    private fun handleQueryResultStream(
        completeReplyBuilder: StringBuilder,
        state: MutableLiveData<HomeUiState>,
        query: String,
        data: StreamResource<String>,
        speak: ((content: String) -> Unit)? = null
    ) {
        when (data) {
            is StreamResource.Error -> Log.d(TAG, "Error")
            is StreamResource.StreamStarted -> onStreamStarted(state, data, completeReplyBuilder)
            is StreamResource.StreamInProgress -> onStreamInProgress(state, data, completeReplyBuilder)
            is StreamResource.StreamCompleted -> onStreamCompleted(state, query, completeReplyBuilder.toString(), speak)
        }
    }

    private fun onStreamCompleted(
        homeUiState: MutableLiveData<HomeUiState>,
        query: String,
        completeReply: String,
        speak: ((content: String) -> Unit)? = null
    ) {
        homeUiState.value?.let { state ->
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
        Log.d(TAG, "StreamStarted")
        state.value?.conversations?.last()?.data?.value = data.startedOr("")
        stringBuilder.append(data.startedOr(""))
    }

    private fun onStreamInProgress(
        state: MutableLiveData<HomeUiState>,
        data: StreamResource.StreamInProgress<String>,
        stringBuilder: StringBuilder
    ) {
        state.value?.conversations?.last()?.data?.value = data.inProgressOr("")
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
        _uiState.value = uiState.value?.copy(hint = "Listening")
        _uiState.value?.textFieldValue?.value = TextFieldValue("")
    }

    fun startListening() {
        _uiState.value = uiState.value?.copy(micIcon = true)
    }

    fun stopListening() {
        _uiState.value = uiState.value?.copy(micIcon = false)
        _uiState.value = uiState.value?.copy(hint = "Tap and hold to speak.")
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
            conversationId: String?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(answerRepository, settingsRepository, historyRepository, conversationId) as T
            }
        }

        private val TAG: String = HomeViewModel::class.java.simpleName
    }
}



