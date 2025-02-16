package com.gowittgroup.smartassistlib.data.datasources.authentication

import android.net.Uri
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.authentication.AuthProvider
import com.gowittgroup.smartassistlib.models.authentication.SignUpModel
import com.gowittgroup.smartassistlib.models.authentication.User
import com.gowittgroup.smartassistlib.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume

class AuthenticationDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthenticationDataSource {

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid, it.displayName ?: "") })
                }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }


    override fun currentUserId(): String = firebaseAuth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String): Resource<User> {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            if (user.isEmailVerified) {
                                SmartLog.d(TAG, "signInWithEmailAndPassword:success")
                                val signedInUser =
                                    User(id = user.uid, displayName = user.displayName ?: "")
                                continuation.resume(Resource.Success(signedInUser))
                            } else {
                                SmartLog.e(TAG, "Email is not verified.")
                                firebaseAuth.signOut()
                                continuation.resume(
                                    Resource.Error(RuntimeException("Email is not verified. Please check your mail and verify."))
                                )
                            }
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

    override suspend fun signInWithProvider(token: String, provider: AuthProvider): Resource<User> {
        return try {
            val credential = when (provider) {
                AuthProvider.GOOGLE -> GoogleAuthProvider.getCredential(token, null)
                AuthProvider.FACEBOOK -> FacebookAuthProvider.getCredential(token)
                else -> throw IllegalArgumentException("Unsupported provider")
            }

            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user?.let {
                User(
                    id = it.uid,
                    email = it.email ?: "",
                    displayName = it.displayName ?: "",
                    photoUrl = it.photoUrl?.toString()
                )
            }

            if (user != null) {
                updateFirestoreUser(user)
            }
            user?.let { Resource.Success(it) } ?: Resource.Error(RuntimeException("User not found"))
        } catch (e: Exception) {
            Resource.Error(RuntimeException(e.localizedMessage ?: "Sign-in failed"))
        }
    }

    private suspend fun updateFirestoreUser(user: User) {
        val userDocRef = firestore.collection(Constants.USER_COLLECTION_PATH).document(user.id)

        val docSnapshot = userDocRef.get().await()

        if (!docSnapshot.exists()) {
            userDocRef.set(user).await()
        } else {
            val storedUser = docSnapshot.toObject(User::class.java)
        }
    }

    override suspend fun signUp(model: SignUpModel): Resource<User> {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(model.email, model.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            SmartLog.d(TAG, "createUserWithEmail:success")


                            val displayName = getDisplayName(model.firstName, model.lastName)


                            val profileUpdates = userProfileChangeRequest {
                                this.displayName = displayName
                            }

                            user.updateProfile(profileUpdates)
                                .addOnCompleteListener { profileTask ->
                                    if (profileTask.isSuccessful) {

                                        user.sendEmailVerification()
                                            .addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    SmartLog.d(TAG, "Email verification sent.")


                                                    val userData = hashMapOf(
                                                        Constants.UserDataKey.FIRST_NAME to model.firstName,
                                                        Constants.UserDataKey.LAST_NAME to model.lastName,
                                                        Constants.UserDataKey.EMAIL to model.email
                                                    )

                                                    firestore
                                                        .collection(Constants.USER_COLLECTION_PATH)
                                                        .document(user.uid)
                                                        .set(userData)
                                                        .addOnSuccessListener {

                                                            if (!user.isEmailVerified) {
                                                                firebaseAuth.signOut()
                                                            }

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

    private fun getDisplayName(firstName: String, lastName: String) =
        "$firstName $lastName".trim()


    override suspend fun signOut(): Resource<Boolean> {
        return try {
            firebaseAuth.signOut()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteAccount(): Resource<Boolean> {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser != null) {
            suspendCancellableCoroutine { continuation ->
                val firestore = firestore
                val userDocRef =
                    firestore.collection(Constants.USER_COLLECTION_PATH).document(currentUser.uid)

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


    override suspend fun sendVerificationEmail(): Resource<Boolean> {
        val currentUser = firebaseAuth.currentUser
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


    override suspend fun isEmailVerified(): Resource<Boolean> {
        return Resource.Success(firebaseAuth.currentUser?.isEmailVerified ?: false)
    }

    override suspend fun updateProfile(
        user: User
    ): Resource<User> {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser != null) {
            suspendCancellableCoroutine { continuation ->

                val profileUpdates = userProfileChangeRequest {
                    this.displayName = getDisplayName(user.firstName, user.lastName)
                    this.photoUri = user.photoUrl?.let { Uri.parse(it) }
                }

                currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {

                            val firestore = firestore
                            val userDocRef = firestore.collection(Constants.USER_COLLECTION_PATH)
                                .document(currentUser.uid)
                            val userData = mapOf(
                                Constants.UserDataKey.FIRST_NAME to user.firstName,
                                Constants.UserDataKey.LAST_NAME to user.lastName,
                                Constants.UserDataKey.DATE_OF_BIRTH to user.dateOfBirth,
                                Constants.UserDataKey.GENDER to user.gender,
                                Constants.UserDataKey.EMAIL to user.email
                            )
                            userDocRef.update(userData)
                                .addOnCompleteListener { firestoreTask ->
                                    if (firestoreTask.isSuccessful) {

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


    override suspend fun resetPassword(email: String): Resource<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.sendPasswordResetEmail(email)
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


    override suspend fun fetchUserProfile(userId: String): Resource<User> {
        return try {

            val firebaseUser = firebaseAuth.currentUser

            if (firebaseUser == null || firebaseUser.uid != userId) {
                return Resource.Error(RuntimeException("Authenticated user not found or mismatched."))
            }


            val userProfileRef =
                firestore.collection(Constants.USER_COLLECTION_PATH).document(userId)
            val documentSnapshot = userProfileRef.get().await()

            if (documentSnapshot.exists()) {

                val firestoreUser = documentSnapshot.toObject(User::class.java)
                if (firestoreUser != null) {
                    val mergedUser = firestoreUser.copy(
                        id = firebaseUser.uid,
                        displayName = firebaseUser.displayName ?: firestoreUser.displayName,
                        email = firebaseUser.email ?: firestoreUser.email,
                        photoUrl = firebaseUser.photoUrl?.toString()
                    )
                    Resource.Success(mergedUser)
                } else {
                    Resource.Error(RuntimeException("User profile data is invalid."))
                }
            } else {
                Resource.Error(RuntimeException("User profile not found in Firestore."))
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    companion object {
        private val TAG = AuthenticationDataSourceImpl::class.java.simpleName
    }
}