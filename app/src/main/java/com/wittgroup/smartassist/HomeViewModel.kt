package com.wittgroup.smartassist

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wittgroup.smartassistlib.datasources.AI
import com.wittgroup.smartassistlib.datasources.ChatGpt
import com.wittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val ai: AI = ChatGpt()

    private val _homeModel = MutableLiveData(HomeModel.DEFAULT)
    val homeModel: LiveData<HomeModel> = _homeModel

    fun loadAnswer(query: String) {
        viewModelScope.launch {
            _homeModel.value = homeModel.value?.copy(showLoading = true)
            when (val result = ai.getAnswer(query)) {
                is Resource.Error -> Log.d("", "")
                is Resource.Loading -> Log.d("", "")
                is Resource.Success -> _homeModel.value =
                    _homeModel.value?.let { model ->
                        model.copy(
                            answer = result.data,
                            row = mutateList(model.row, listOf(Conversation(false, result.data.trim())))
                        )
                    }
            }
            _homeModel.value = _homeModel.value?.copy(showLoading = false)
        }
    }

    fun ask(question: String) {
        loadAnswer(question)
        _homeModel.value =
            _homeModel.value?.let { model -> model.copy(row = mutateList(model.row, listOf(Conversation(true, question))), micIcon = false) }
        _homeModel.value?.textFieldValue?.value = TextFieldValue("")
    }

    fun beginnigSpeach() {
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

    fun mutateList(toList: List<Conversation>, fromList: List<Conversation>) = toList.toMutableList().apply { addAll(fromList) }
}


data class HomeModel(
    val textFieldValue: MutableState<TextFieldValue>,
    val row: List<Conversation>,
    val hint: String,
    val question: String,
    val answer: String,
    val showLoading: Boolean,
    val micIcon: Boolean
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
                micIcon = false
            )
    }
}

data class Conversation(val isQuestion: Boolean, val data: String)
