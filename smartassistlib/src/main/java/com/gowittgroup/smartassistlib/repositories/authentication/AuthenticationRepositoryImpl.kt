package com.gowittgroup.smartassistlib.repositories.authentication

import com.gowittgroup.smartassistlib.datasources.authentication.AuthenticationDataSource
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.User
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(private val authenticationDataSource: AuthenticationDataSource) :
    AuthenticationRepository {
    override val currentUser = authenticationDataSource.currentUser

    override val currentUserId = authenticationDataSource.currentUserId

    override fun hasUser(): Boolean = authenticationDataSource.hasUser()

    override suspend fun signIn(email: String, password: String): Resource<User> =
        authenticationDataSource.signIn(email, password)


    override suspend fun signUp(email: String, password: String): Resource<User> =
        authenticationDataSource.signUp(email, password)


    override suspend fun signOut(): Resource<Boolean> = authenticationDataSource.signOut()

    override suspend fun deleteAccount(): Resource<Boolean> = authenticationDataSource.deleteAccount()

    companion object {
        private val TAG = AuthenticationRepositoryImpl::class.java.simpleName
    }
}