package com.gowittgroup.smartassist.ui.faqscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.ui.BaseViewModel
import com.gowittgroup.smartassist.util.NetworkUtil
import com.gowittgroup.smartassistlib.datasources.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Faq(val id: Int, val ques: String, val ans: String)

private val faqs = listOf(
    Faq(
        id = 1,
        ques = "What makes SmartAssist unique?",
        ans = "SmartAssist is a dedicated mobile app, offering the convenience of a streamlined and user-friendly experience."
    ),

    Faq(
        id = 2,
        ques = "How does SmartAssist differentiate itself from other platforms?",
        ans = "Unlike complex websites or multi-feature applications, SmartAssist focuses on simplicity and text-based communication."
    ),

    Faq(
        id = 3,
        ques = "Can I choose between different AI technologies with SmartAssist?",
        ans = "Yes, SmartAssist empowers users to switch seamlessly between ChatGPT or Google Gemini, providing flexibility and choice."
    ),
    Faq(
        id = 4,
        ques = "How can I input my queries with SmartAssist?",
        ans = "SmartAssist allows you to effortlessly type or speak your queries, catering to your preferred mode of communication."
    ),

    Faq(
        id = 5,
        ques = "Is there an option for audio answers in SmartAssist?",
        ans = "Yes, SmartAssist enables you to listen to the answers in audio format, ensuring a hands-free and convenient experience."
    )
)

data class FaqUiState(
    val loading: Boolean = false,
    val faqs: List<Faq> = listOf(),
    val error: String = "",
)

@HiltViewModel
class FaqViewModel @Inject constructor(
    private val authService: AuthenticationService
) : BaseViewModel(authService) {

    private val _uiState = MutableStateFlow(FaqUiState(loading = true))
    val uiState: StateFlow<FaqUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    private fun refreshAll() {

        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            var error: String = ""
            _uiState.value = _uiState.value.copy(loading = false, faqs = faqs)
        }
    }

    fun resetErrorMessage() {
        _uiState.update { it.copy(error = "") }
    }

}

