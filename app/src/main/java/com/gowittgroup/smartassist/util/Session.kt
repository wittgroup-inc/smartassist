package com.gowittgroup.smartassist.util

import com.gowittgroup.smartassistlib.datasources.authentication.AuthenticationDataSourceImpl
import com.gowittgroup.smartassistlib.models.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object Session {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun updateCurrentUser(user: User?) {
        _currentUser.value = user
    }

    init {
        GlobalScope.launch {
            AuthenticationDataSourceImpl().currentUser.collect {
                updateCurrentUser(user = it)
            }
        }
    }

    var subscriptionStatus: Boolean = false
    var userHasClosedTheBanner: Boolean = false
}