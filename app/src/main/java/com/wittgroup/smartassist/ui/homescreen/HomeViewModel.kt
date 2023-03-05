package com.wittgroup.smartassist.ui.homescreen

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.wittgroup.smartassistlib.models.Resource
import com.wittgroup.smartassistlib.models.successOr
import com.wittgroup.smartassistlib.repositories.AnswerRepository
import com.wittgroup.smartassistlib.repositories.SettingsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel(private val answerRepository: AnswerRepository, private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _homeModel = MutableLiveData(HomeModel.DEFAULT)
    val homeModel: LiveData<HomeModel> = _homeModel

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            val readAloudDeferred = async { settingsRepository.getReadAloud() }
            val readAloud = readAloudDeferred.await().successOr(false)
            _homeModel.value = _homeModel.value?.copy(readAloud = readAloud)
        }
    }

    private fun loadAnswer(query: String, speak: ((content: String) -> Unit)? = null) {
        viewModelScope.launch {
            _homeModel.value = homeModel.value?.copy(showLoading = true)
            when (val result = answerRepository.getAnswer(query)) {
                is Resource.Error -> Log.d("", "")
                is Resource.Loading -> Log.d("", "")
                is Resource.Success ->
                    _homeModel.value = _homeModel.value?.let { model ->
                        if (model.readAloud) speak?.let { it(result.data.trim()) }
                        model.copy(
                            answer = result.data,
                            row = mutateList(model.row, listOf(Conversation(false, result.data.trim())))
                        )
                    }

            }
            _homeModel.value = _homeModel.value?.copy(showLoading = false)
        }
    }

    fun ask(question: String, speak: ((content: String) -> Unit)? = null) {
        loadAnswer(question, speak)
        _homeModel.value =
            _homeModel.value?.let { model -> model.copy(row = mutateList(model.row, listOf(Conversation(true, question))), micIcon = false) }
        _homeModel.value?.textFieldValue?.value = TextFieldValue("")
    }

    fun beginningSpeech() {
        _homeModel.value = homeModel.value?.copy(hint = "Listening")
        _homeModel.value?.textFieldValue?.value = TextFieldValue("")
    }

    fun updateIsTyping(position: Int, isTyping: Boolean) {
        _homeModel.value = _homeModel.value?.let { model -> model.copy(row = updateList(model.row, position, isTyping)) }
    }

    private fun updateList(list: List<Conversation>, position: Int, isTyping: Boolean): List<Conversation> {
        val mutableList = list.toMutableList()
        val newValue = mutableList[position].copy(isTyping = isTyping)
        mutableList[position] = newValue
        return mutableList
    }

    fun startListening() {
        _homeModel.value = homeModel.value?.copy(micIcon = true)
    }

    fun stopListening() {
        _homeModel.value = homeModel.value?.copy(micIcon = false)
        _homeModel.value = homeModel.value?.copy(hint = "Tap and hold to speak.")
    }

    fun setReadAloud(isOn: Boolean) {
        _homeModel.value = homeModel.value?.copy(readAloud = isOn)
    }

    private fun mutateList(toList: List<Conversation>, fromList: List<Conversation>) = toList.toMutableList().apply { addAll(fromList) }

    companion object {
        fun provideFactory(
            answerRepository: AnswerRepository,
            settingsRepository: SettingsRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(answerRepository, settingsRepository) as T
            }
        }
    }
}


data class HomeModel(
    val textFieldValue: MutableState<TextFieldValue>,
    val row: List<Conversation>,
    val hint: String,
    val question: String,
    val answer: String,
    val showLoading: Boolean,
    val micIcon: Boolean,
    val readAloud: Boolean
) {
    companion object {
        val DEFAULT =
            HomeModel(
                textFieldValue = mutableStateOf(TextFieldValue("")),
                row = listOf(),
                hint = "Tap and hold to speak.",
                question = "",
                answer = "",
                showLoading = false,
                micIcon = false,
                readAloud = false
            )
    }


}

data class Conversation(val isQuestion: Boolean, val data: String, val isTyping: Boolean = true)
