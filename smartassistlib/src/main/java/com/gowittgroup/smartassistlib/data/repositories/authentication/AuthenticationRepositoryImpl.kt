package com.gowittgroup.smartassistlib.data.repositories.authentication

import com.gowittgroup.smartassistlib.data.datasources.authentication.AuthenticationDataSource
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
import com.gowittgroup.smartassistlib.models.authentication.SignUpModel
import com.gowittgroup.smartassistlib.models.authentication.User
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationDataSource: AuthenticationDataSource,
    private val settingsDataSource: SettingsDataSource
) :
    AuthenticationRepository {
    override val currentUser = authenticationDataSource.currentUser

    override fun currentUserId() = authenticationDataSource.currentUserId()

    override fun hasUser(): Boolean = authenticationDataSource.hasUser()

    override suspend fun signIn(email: String, password: String): Resource<User> =
        when (val res = authenticationDataSource.signIn(email, password)) {
            is Resource.Success -> {
                settingsDataSource.setUserId(res.data.id)
                res
            }

            else -> res
        }


    override suspend fun signUp(model: SignUpModel): Resource<User> =
        authenticationDataSource.signUp(model)

    override suspend fun fetchUserProfile(userId: String): Resource<User> =
        authenticationDataSource.fetchUserProfile(userId)

    override suspend fun signOut(): Resource<Boolean> =
        when (val res = authenticationDataSource.signOut()) {
            is Resource.Success -> {
                settingsDataSource.setUserId(null)
                res
            }

            else -> res
        }

    override suspend fun deleteAccount(): Resource<Boolean> = when (val res =
        authenticationDataSource.deleteAccount()) {
        is Resource.Success -> {
            settingsDataSource.setUserId(null)
            res
        }

        else -> res
    }

    override suspend fun sendVerificationEmail(): Resource<Boolean> =
        authenticationDataSource.sendVerificationEmail()

    override suspend fun isEmailVerified(): Resource<Boolean> =
        authenticationDataSource.isEmailVerified()

    override suspend fun updateProfile(user: User): Resource<User> =
        authenticationDataSource.updateProfile(user)

    override suspend fun resetPassword(email: String): Resource<Boolean> =
        authenticationDataSource.resetPassword(email)

    companion object {
        private val TAG = AuthenticationRepositoryImpl::class.java.simpleName
    }
}