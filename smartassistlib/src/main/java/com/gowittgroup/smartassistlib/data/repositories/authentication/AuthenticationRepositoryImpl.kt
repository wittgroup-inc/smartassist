package com.gowittgroup.smartassistlib.data.repositories.authentication

import com.gowittgroup.smartassistlib.data.datasources.authentication.AuthenticationDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
import com.gowittgroup.smartassistlib.models.authentication.SignUpModel
import com.gowittgroup.smartassistlib.models.authentication.User
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(private val authenticationDataSource: AuthenticationDataSource) :
    AuthenticationRepository {
    override val currentUser = authenticationDataSource.currentUser

    override val currentUserId = authenticationDataSource.currentUserId

    override fun hasUser(): Boolean = authenticationDataSource.hasUser()

    override suspend fun signIn(email: String, password: String): Resource<User> =
        authenticationDataSource.signIn(email, password)


    override suspend fun signUp(model: SignUpModel): Resource<User> =
        authenticationDataSource.signUp(model)


    override suspend fun signOut(): Resource<Boolean> = authenticationDataSource.signOut()

    override suspend fun deleteAccount(): Resource<Boolean> = authenticationDataSource.deleteAccount()

    override suspend fun sendVerificationEmail(): Resource<Boolean> = authenticationDataSource.sendVerificationEmail()

    override suspend fun isEmailVerified(): Resource<Boolean> = authenticationDataSource.isEmailVerified()

    override suspend fun updateProfile(user: User): Resource<User> = authenticationDataSource.updateProfile(user)

    override suspend fun resetPassword(email: String): Resource<Boolean> = authenticationDataSource.resetPassword(email)

    companion object {
        private val TAG = AuthenticationRepositoryImpl::class.java.simpleName
    }
}