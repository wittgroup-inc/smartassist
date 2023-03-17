package com.wittgroup.smartassist.ui.homescreen

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.wittgroup.smartassist.models.Conversation
import com.wittgroup.smartassist.ui.homescreen.HomeModel.Companion.getId
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

class HomeViewModel(
    private val answerRepository: AnswerRepository,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: ConversationHistoryRepository,
    private val conversationHistoryId: String?
) : ViewModel() {

    private val _homeModel = MutableLiveData(HomeModel.DEFAULT)
    val homeModel: LiveData<HomeModel> = _homeModel
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
                _homeModel.value = _homeModel.value?.let { model ->
                    model.copy(
                        conversations = addToConversationList(model.conversations, history.conversations.map(::toConversation))
                    )
                }
            }
        }
    }

    fun toConversation(conversation: ConversationEntity): Conversation =
        Conversation(
            isQuestion = conversation.isQuestion,
            data = MutableStateFlow(conversation.data),
            isTyping = false
        )


    fun refreshAll() {
        viewModelScope.launch {
            val readAloudDeferred = async { settingsRepository.getReadAloud() }
            val readAloud = readAloudDeferred.await().successOr(false)
            _homeModel.value?.readAloud?.value = readAloud
        }
    }

    private fun loadAnswer(query: String, speak: ((content: String) -> Unit)? = null) {
        viewModelScope.launch {
            _homeModel.value = homeModel.value?.copy(showLoading = true)
            when (val result = answerRepository.getReply(query)) {
                is Resource.Error -> Log.d("", "")
                is Resource.Success -> {
                    val completeReply: StringBuilder = StringBuilder()
                    _homeModel.value = _homeModel.value?.let { model ->
                        val newConversation = Conversation(
                            isQuestion = false,
                            data = MutableStateFlow("")
                        )
                        model.copy(conversations = addToConversationList(model.conversations, listOf(newConversation)))
                    }

                    _homeModel.value = _homeModel.value?.copy(showLoading = false)
                    result.data.collect { data ->
                        when (data) {
                            is StreamResource.Error -> Log.d("", "Error")
                            is StreamResource.StreamStarted -> {
                                Log.d("Fuck!! view", "StreamStarted")
                                _homeModel.value?.conversations?.last()?.data?.value = data.startedOr("")
                                completeReply.append(data.startedOr(""))
                            }
                            is StreamResource.StreamInProgress -> {
                                Log.d("Fuck!! view", "Text -> $data")
                                _homeModel.value?.conversations?.last()?.data?.value = data.inProgressOr("")
                                completeReply.append(data.inProgressOr(""))
                            }
                            is StreamResource.StreamCompleted -> {
                                _homeModel.value?.let { model ->
                                    history = history.copy(
                                        // save to history
                                        conversations = addToConversationEntityList(
                                            history.conversations, listOf(
                                                ConversationEntity(isQuestion = true, data = query),
                                                ConversationEntity(isQuestion = false, data = completeReply.toString())
                                            )
                                        )
                                    )
                                    if (model.readAloud.value) {
                                        speak?.let { speakModule -> speakModule(completeReply.toString()) }
                                    }
                                    viewModelScope.launch(Dispatchers.IO) {
                                        historyRepository.saveConversationHistory(history)
                                    }
                                }
                            }
                        }
                    }
                }
                is Resource.Loading -> Log.d("", "Loading")
            }
        }
    }

    fun ask(question: String, speak: ((content: String) -> Unit)? = null) {
        _homeModel.value =
            _homeModel.value?.let { model ->
                model.copy(
                    conversations = addToConversationList(
                        model.conversations,
                        listOf(Conversation(isQuestion = true, data = MutableStateFlow(question)))
                    ),
                    micIcon = false
                )
            }
        loadAnswer(question, speak)
        _homeModel.value?.textFieldValue?.value = TextFieldValue("")
    }

    fun beginningSpeech() {
        _homeModel.value = homeModel.value?.copy(hint = "Listening")
        _homeModel.value?.textFieldValue?.value = TextFieldValue("")
    }

    fun startListening() {
        _homeModel.value = homeModel.value?.copy(micIcon = true)
    }

    fun stopListening() {
        _homeModel.value = homeModel.value?.copy(micIcon = false)
        _homeModel.value = homeModel.value?.copy(hint = "Tap and hold to speak.")
    }

    fun setReadAloud(isOn: Boolean) {
        _homeModel.value?.readAloud?.value = isOn
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
    }
}


data class HomeModel(
    val textFieldValue: MutableState<TextFieldValue>,
    val conversations: List<Conversation>,
    val hint: String,
    val showLoading: Boolean,
    val micIcon: Boolean,
    val readAloud: MutableState<Boolean>
) {
    companion object {
        val DEFAULT =
            HomeModel(
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