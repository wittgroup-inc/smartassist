package com.gowittgroup.smartassist.core

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseViewModelWithStateIntentAndSideEffect<S : State, I : Intent, SE : SideEffect>: BaseViewModelWithStateAndIntent<S, I>() {

    // Channel for side effects
    private val _sideEffects = Channel<SE>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    // Function to send a side effect
    protected fun sendSideEffect(sideEffect: SE) {
        viewModelScope.launch {
            _sideEffects.send(sideEffect)
        }
    }
}