package com.gowittgroup.smartassistlib.domain.repositories.authentication

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.authentication.SignUpModel
import com.gowittgroup.smartassistlib.models.authentication.User
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String): Resource<User>
    suspend fun signUp(model: SignUpModel): Resource<User>
    suspend fun signOut(): Resource<Boolean>
    suspend fun deleteAccount(): Resource<Boolean>
}