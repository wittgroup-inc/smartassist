package com.gowittgroup.smartassistlib.datasources

import com.gowittgroup.smartassistlib.models.User
import kotlinx.coroutines.flow.Flow

interface AuthenticationService {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun signOut()
    suspend fun deleteAccount()
}