package com.gowittgroup.smartassist.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModelWithStateAndIntent<S : State, I : Intent> : ViewModel() {
    // State management
    private val _uiState: MutableStateFlow<S> by lazy { MutableStateFlow(getDefaultState()) }
    val uiState: StateFlow<S> = _uiState


    protected abstract fun getDefaultState(): S

    // Abstract method to handle intents
    abstract fun processIntent(intent: I)

    // Utility function to update the state
    protected fun S.applyStateUpdate() {
        _uiState.update { this }
    }

    protected fun updateState(s:S) {
        _uiState.update { s }
    }
}