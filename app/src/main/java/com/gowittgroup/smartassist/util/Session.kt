package com.gowittgroup.smartassist.util

import androidx.compose.runtime.mutableStateOf
import com.gowittgroup.smartassistlib.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Session {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun updateCurrentUser(user: User?) {
        _currentUser.value = user
    }

    var subscriptionStatus: Boolean = false
    var userHasClosedTheBanner: Boolean = false
}