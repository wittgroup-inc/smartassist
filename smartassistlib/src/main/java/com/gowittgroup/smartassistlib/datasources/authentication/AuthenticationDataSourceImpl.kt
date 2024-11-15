package com.gowittgroup.smartassistlib.datasources.authentication

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AuthenticationDataSourceImpl @Inject constructor() : AuthenticationDataSource {
    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid, it.displayName?: "") })
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String): Resource<User> {
        return suspendCancellableCoroutine { continuation ->
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result.user
                        if (user != null) {
                            Log.d(TAG, "signInWithEmailAndPassword:success")
                            val signedInUser =
                                User(id = user.uid, displayName = user.displayName ?: "")
                            continuation.resume(Resource.Success(signedInUser))
                        } else {
                            continuation.resume(Resource.Error(RuntimeException("User not found.")))
                        }
                    } else {
                        Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                        val exception = task.exception
                        if (exception is FirebaseAuthInvalidCredentialsException) {
                            continuation.resume(Resource.Error(RuntimeException("Invalid Credentials")))
                        } else {
                            continuation.resume(Resource.Error(RuntimeException("Something went wrong.")))
                        }
                    }
                }
        }
    }

    override suspend fun signUp(email: String, password: String): Resource<User> {
        return suspendCancellableCoroutine { continuation ->
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = Firebase.auth.currentUser
                        if (user != null) {
                            Log.d(TAG, "createUserWithEmail:success")
                            val newUser = User(id = user.uid, displayName = user.displayName ?: "")
                            continuation.resume(Resource.Success(newUser))
                        } else {
                            continuation.resume(Resource.Error(RuntimeException("User creation failed.")))
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        val exception = task.exception
                        if (exception is FirebaseAuthWeakPasswordException) {
                            continuation.resume(Resource.Error(RuntimeException("Weak password: ${exception.message}")))
                        } else {
                            continuation.resume(Resource.Error(RuntimeException("Something went wrong.")))
                        }
                    }
                }
        }
    }


    override suspend fun signOut(): Resource<Boolean> {
        return try {
            Firebase.auth.signOut()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteAccount(): Resource<Boolean> {
        val currentUser = Firebase.auth.currentUser
        return if (currentUser != null) {
            suspendCancellableCoroutine { continuation ->
                currentUser.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(Resource.Success(true)) // Ensure Resource<Boolean>
                        } else {
                            val exception =
                                task.exception ?: RuntimeException("Unknown error occurred.")
                            continuation.resume(Resource.Error(exception))
                        }
                    }
            }
        } else {
            Resource.Error(RuntimeException("No user is currently signed in."))
        }
    }

    companion object {
        private val TAG = AuthenticationDataSourceImpl::class.java.simpleName
    }
}