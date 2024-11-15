package com.gowittgroup.smartassistlib.repositories.authentication

import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.User
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String): Resource<User>
    suspend fun signUp(email: String, password: String): Resource<User>
    suspend fun signOut(): Resource<Boolean>
    suspend fun deleteAccount():Resource<Boolean>
}