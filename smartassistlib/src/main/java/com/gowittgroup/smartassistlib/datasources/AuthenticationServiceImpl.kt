package com.gowittgroup.smartassistlib.datasources

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gowittgroup.smartassistlib.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationServiceImpl @Inject constructor() : AuthenticationService {
    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) })
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Log.d(TAG, "signInWithEmailAndPassword:success")
            } else {
                Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                if(task.exception is FirebaseAuthInvalidCredentialsException){

                } else {

                }
            }
        }.await()
    }

    override suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    if (task.exception is FirebaseAuthWeakPasswordException) {
                        // Show a specific error message about the weak password
                        // ...
                    } else {
                        // Show a generic error message
                        // ...
                    }
                }
            }
            .await()
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }

    companion object {
        private val TAG = AuthenticationServiceImpl::class.java.simpleName
    }
}