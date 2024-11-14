package com.gowittgroup.smartassist.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassistlib.datasources.AuthenticationService
import com.gowittgroup.smartassistlib.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel constructor(private val authService: AuthenticationService) :
    ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        viewModelScope.launch {
            authService.currentUser.collect {
                Log.d("Pawan>>> Base", "user arrived......$it")
                if (it != null) {
                    _currentUser.value = it
                }
            }
        }
    }
}