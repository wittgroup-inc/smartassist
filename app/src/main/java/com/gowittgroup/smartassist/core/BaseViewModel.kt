package com.gowittgroup.smartassist.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
import com.gowittgroup.smartassistlib.models.authentication.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel(private val authRepository: AuthenticationRepository) :
    ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect {
                SmartLog.d("Pawan>>> Base", "user arrived......$it")
                if (it != null) {
                    _currentUser.value = it
                }
            }
        }
    }
}