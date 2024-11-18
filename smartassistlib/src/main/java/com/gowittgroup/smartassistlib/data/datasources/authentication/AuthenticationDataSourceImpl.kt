package com.gowittgroup.smartassistlib.data.datasources.authentication

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.authentication.SignUpModel
import com.gowittgroup.smartassistlib.models.authentication.User
import com.gowittgroup.smartassistlib.util.Constants
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
                    this.trySend(auth.currentUser?.let { User(it.uid, it.displayName ?: "") })
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
                            SmartLog.d(TAG, "signInWithEmailAndPassword:success")
                            val signedInUser =
                                User(id = user.uid, displayName = user.displayName ?: "")
                            continuation.resume(Resource.Success(signedInUser))
                        } else {
                            continuation.resume(Resource.Error(RuntimeException("User not found.")))
                        }
                    } else {
                        SmartLog.e(TAG, "signInWithEmailAndPassword:failure", task.exception)
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
                            SmartLog.d(TAG, "createUserWithEmail:success")

                            // Combine firstName and lastName to form displayName
                            val displayName = getDisplayName(model.firstName, model.lastName)

                            // Update the user's profile to set the display name
                            val profileUpdates = userProfileChangeRequest {
                                this.displayName = displayName
                            }

                            user.updateProfile(profileUpdates)
                                .addOnCompleteListener { profileTask ->
                                    if (profileTask.isSuccessful) {
                                        // Send email verification
                                        user.sendEmailVerification()
                                            .addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    SmartLog.d(TAG, "Email verification sent.")

                                                    // Save additional user details to Firestore

                                                    val userData = hashMapOf(
                                                        Constants.UserDataKey.FIRST_NAME to model.firstName,
                                                        Constants.UserDataKey.LAST_NAME to model.lastName,
                                                        Constants.UserDataKey.DATE_OF_BIRTH to model.dateOfBirth,
                                                        Constants.UserDataKey.GENDER to model.gender,
                                                        Constants.UserDataKey.EMAIL to model.email
                                                    )

                                                    FirebaseFirestore.getInstance()
                                                        .collection(Constants.USER_COLLECTION_PATH)
                                                        .document(user.uid)
                                                        .set(userData)
                                                        .addOnSuccessListener {
                                                            // Return the created User object
                                                            val newUser = User(
                                                                id = user.uid,
                                                                displayName = displayName
                                                            )
                                                            continuation.resume(
                                                                Resource.Success(
                                                                    newUser
                                                                )
                                                            )
                                                        }
                                                        .addOnFailureListener { e ->
                                                            SmartLog.e(
                                                                TAG,
                                                                "Error saving user data: ${e.message}"
                                                            )
                                                            continuation.resume(
                                                                Resource.Error(
                                                                    RuntimeException("Failed to save additional user data")
                                                                )
                                                            )
                                                        }
                                                } else {
                                                    SmartLog.e(
                                                        TAG,
                                                        "Failed to send email verification: ${emailTask.exception}"
                                                    )
                                                    continuation.resume(
                                                        Resource.Error(
                                                            RuntimeException("Failed to send email verification.")
                                                        )
                                                    )
                                                }
                                            }
                                    } else {
                                        SmartLog.e(
                                            TAG,
                                            "Failed to update profile: ${profileTask.exception}"
                                        )
                                        continuation.resume(Resource.Error(RuntimeException("Failed to set display name.")))
                                    }
                                }
                        } else {
                            continuation.resume(Resource.Error(RuntimeException("User creation failed.")))
                        }
                    } else {
                        SmartLog.e(TAG, "createUserWithEmail:failure", task.exception)
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

    private fun getDisplayName(firstName: String, lastName:String) =
        "$firstName $lastName".trim()


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
                val firestore = Firebase.firestore
                val userDocRef = firestore.collection(Constants.USER_COLLECTION_PATH).document(currentUser.uid)

                firestore.runTransaction { transaction ->
                    transaction.delete(userDocRef)
                }.addOnCompleteListener { transactionTask ->
                    if (transactionTask.isSuccessful) {
                        currentUser.delete()
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    continuation.resume(Resource.Success(true))
                                } else {
                                    val exception = authTask.exception
                                        ?: RuntimeException("Unknown error occurred while deleting user.")
                                    continuation.resume(Resource.Error(exception))
                                }
                            }
                    } else {
                        val exception = transactionTask.exception
                            ?: RuntimeException("Unknown error occurred while deleting user data.")
                        continuation.resume(Resource.Error(exception))
                    }
                }
            }
        } else {
            Resource.Error(RuntimeException("No user is currently signed in."))
        }
    }

    // New Method: Send Email Verification
    override suspend fun sendVerificationEmail(): Resource<Boolean> {
        val currentUser = Firebase.auth.currentUser
        return if (currentUser != null) {
            suspendCancellableCoroutine { continuation ->
                currentUser.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(Resource.Success(true))
                        } else {
                            continuation.resume(
                                Resource.Error(
                                    task.exception
                                        ?: Exception("Failed to send email verification.")
                                )
                            )
                        }
                    }
            }
        } else {
            Resource.Error(Exception("No user is currently signed in."))
        }
    }

    // New Method: Check if Email is Verified
    override suspend fun isEmailVerified(): Resource<Boolean> {
        return Resource.Success(Firebase.auth.currentUser?.isEmailVerified ?: false)
    }

    override suspend fun updateProfile(
        user: User
    ): Resource<User> {
        val currentUser = Firebase.auth.currentUser
        return if (currentUser != null) {
            suspendCancellableCoroutine { continuation ->
                // Update Firebase Authentication profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(getDisplayName(user.firstName, user.lastName))
                    .setPhotoUri(user.photoUrl?.let { Uri.parse(it) })
                    .build()

                currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Update additional fields in Firestore
                            val firestore = Firebase.firestore
                            val userDocRef = firestore.collection(Constants.USER_COLLECTION_PATH).document(currentUser.uid)
                            val userData = mapOf(
                                Constants.UserDataKey.FIRST_NAME to user.firstName,
                                Constants.UserDataKey.LAST_NAME to user.lastName,
                                Constants.UserDataKey.DATE_OF_BIRTH to user.dob,
                                Constants.UserDataKey.GENDER to user.gender,
                                Constants.UserDataKey.EMAIL to user.email
                            )
                            userDocRef.update(userData)
                                .addOnCompleteListener { firestoreTask ->
                                    if (firestoreTask.isSuccessful) {
                                        // Return updated User object
                                        val updatedUser = User(
                                            id = currentUser.uid,
                                            displayName = currentUser.displayName ?: "",
                                            photoUrl = currentUser.photoUrl?.toString()
                                        )
                                        continuation.resume(Resource.Success(updatedUser))
                                    } else {
                                        continuation.resume(
                                            Resource.Error(
                                                firestoreTask.exception
                                                    ?: Exception("Failed to update Firestore data.")
                                            )
                                        )
                                    }
                                }
                        } else {
                            continuation.resume(
                                Resource.Error(
                                    authTask.exception ?: Exception("Failed to update profile.")
                                )
                            )
                        }
                    }
            }
        } else {
            Resource.Error(Exception("No user is currently signed in."))
        }
    }


    // New Method: Reset Password
    override suspend fun resetPassword(email: String): Resource<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Resource.Success(true))
                    } else {
                        continuation.resume(
                            Resource.Error(
                                task.exception ?: Exception("Failed to send reset password email.")
                            )
                        )
                    }
                }
        }
    }

    companion object {
        private val TAG = AuthenticationDataSourceImpl::class.java.simpleName
    }
}