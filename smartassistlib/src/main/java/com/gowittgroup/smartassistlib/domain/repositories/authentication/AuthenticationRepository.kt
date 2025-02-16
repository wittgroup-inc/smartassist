package com.gowittgroup.smartassistlib.domain.repositories.authentication

import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.authentication.AuthProvider
import com.gowittgroup.smartassistlib.models.authentication.SignUpModel
import com.gowittgroup.smartassistlib.models.authentication.User
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val currentUser: Flow<User?>
    fun currentUserId(): String
    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String): Resource<User>
    suspend fun signInWithProvider(token: String, provider: AuthProvider): Resource<User>
    suspend fun signUp(model: SignUpModel): Resource<User>
    suspend fun fetchUserProfile(userId: String): Resource<User>
    suspend fun signOut(): Resource<Boolean>
    suspend fun deleteAccount(): Resource<Boolean>
    suspend fun sendVerificationEmail(): Resource<Boolean>
    suspend fun isEmailVerified(): Resource<Boolean>
    suspend fun updateProfile(user: User): Resource<User>
    suspend fun resetPassword(email: String): Resource<Boolean>
}