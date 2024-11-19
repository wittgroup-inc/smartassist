package com.gowittgroup.smartassist.core

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModelWithStateIntentAndSideEffect<S : State, I : Intent, SE : SideEffect>: BaseViewModelWithStateAndIntent<S, I>() {


    private val _sideEffects = Channel<SE>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()


    protected fun sendSideEffect(sideEffect: SE) {
        viewModelScope.launch {
            _sideEffects.send(sideEffect)
        }
    }
}