package com.gowittgroup.smartassistlib.data.datasources.authentication

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.authentication.SignUpModel
import com.gowittgroup.smartassistlib.models.authentication.User
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

    override suspend fun signUp(model: SignUpModel): Resource<User> {
        return suspendCancellableCoroutine { continuation ->
            Firebase.auth.createUserWithEmailAndPassword(model.email, model.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = Firebase.auth.currentUser
                        if (user != null) {
                            Log.d(TAG, "createUserWithEmail:success")

                            // Create the User object to return
                            val newUser = User(id = user.uid, displayName = user.displayName ?: "")

                            // Save additional user details to Firestore
                            val userData = hashMapOf(
                                "firstName" to model.firstName,
                                "lastName" to model.lastName,
                                "dateOfBirth" to model.dateOfBirth,
                                "gender" to model.gender,
                                "email" to model.email
                            )

                            // Save to Firestore under a 'users' collection with the user's UID as document ID
                            FirebaseFirestore.getInstance().collection("users")
                                .document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    // Data saved successfully
                                    continuation.resume(Resource.Success(newUser))
                                }
                                .addOnFailureListener { e ->
                                    // Error saving user data to Firestore
                                    Log.e(TAG, "Error saving user data: ${e.message}")
                                    continuation.resume(Resource.Error(RuntimeException("Failed to save additional user data")))
                                }

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
                // Deleting user document from Firestore
                val firestore = Firebase.firestore
                val userDocRef = firestore.collection("users").document(currentUser.uid)

                // Start deleting both Firestore document and Firebase Auth user
                firestore.runTransaction { transaction ->
                    // Delete user document from Firestore
                    transaction.delete(userDocRef)
                }.addOnCompleteListener { transactionTask ->
                    if (transactionTask.isSuccessful) {
                        // After Firestore document is deleted, delete the user from Firebase Auth
                        currentUser.delete()
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    continuation.resume(Resource.Success(true)) // Successfully deleted user
                                } else {
                                    val exception = authTask.exception
                                        ?: RuntimeException("Unknown error occurred while deleting user.")
                                    continuation.resume(Resource.Error(exception))
                                }
                            }
                    } else {
                        val exception = transactionTask.exception
                            ?: RuntimeException("Unknown error occurred while deleting user data from Firestore.")
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