package com.gowittgroup.smartassist.ui.auth.signup

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.models.SignUpModel
import com.gowittgroup.smartassistlib.repositories.authentication.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) : BaseViewModelWithStateIntentAndSideEffect<SignUpUiState, SignUpIntent, SignUpSideEffect>() {

    override fun getDefaultState(): SignUpUiState = SignUpUiState()

    override fun processIntent(intent: SignUpIntent) {
        // Implement based on specific intents if needed.
    }

    // Validation Functions

    private fun isEmailValid(email: String): Boolean {
        // Basic email validation using Regex
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
    }

    private fun isPasswordStrong(password: String): Boolean {
        // Basic password strength validation
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { "!@#\$%^&*()-_=+[]{}|;:,.<>?".contains(it) }
    }

    // Update Email
    fun updateEmail(newEmail: String) {
        uiState.value.copy(email = newEmail, emailError = if (isEmailValid(newEmail)) null else "Invalid email format")
            .applyStateUpdate()
        updateFormValidity()
    }

    // Update Password
    fun updatePassword(newPassword: String) {
        uiState.value.copy(password = newPassword, passwordError = if (isPasswordStrong(newPassword)) null else "Password is too weak, Password must be at least 8 characters, with upper and lowercase letters, a number, and a special character.")
            .applyStateUpdate()
        updateFormValidity()
    }

    // Update Confirm Password
    fun updateConfirmPassword(newConfirmPassword: String) {
        uiState.value.copy(confirmPassword = newConfirmPassword, confirmPasswordError = if (newConfirmPassword == uiState.value.password) null else "Passwords do not match")
            .applyStateUpdate()
        updateFormValidity()
    }

    // Update First Name
    fun updateFirstName(newFirstName: String) {
        uiState.value.copy(firstName = newFirstName, firstNameError = if (newFirstName.isNotBlank()) null else "First Name is required")
            .applyStateUpdate()
        updateFormValidity()
    }

    // Update Last Name
    fun updateLastName(newLastName: String) {
        uiState.value.copy(lastName = newLastName, lastNameError = if (newLastName.isNotBlank()) null else "Last Name is required")
            .applyStateUpdate()
        updateFormValidity()
    }

    // Update Date of Birth
    fun updateDateOfBirth(newDateOfBirth: String) {
        uiState.value.copy(dateOfBirth = newDateOfBirth, dateOfBirthError = if (newDateOfBirth.isNotBlank()) null else "Date of Birth is required")
            .applyStateUpdate()
        updateFormValidity()
    }

    // Update Gender
    fun updateGender(newGender: String) {
        uiState.value.copy(gender = newGender, genderError = if (newGender.isNotBlank()) null else "Gender is required")
            .applyStateUpdate()
        updateFormValidity()
    }

    // Update Terms Checkbox
    fun updateTermsChecked(isChecked: Boolean) {
        uiState.value.copy(isTermsAccepted = isChecked)
            .applyStateUpdate()
        updateFormValidity()
    }

    // Function to update form validity
    private fun updateFormValidity() {
        val isFormValid = uiState.value.run {
            emailError == null && passwordError == null && confirmPasswordError == null &&
                    firstNameError == null && lastNameError == null && dateOfBirthError == null &&
                    genderError == null && isTermsAccepted
        }

        uiState.value.copy(isSignUpEnabled = isFormValid).applyStateUpdate()
    }

    // Handle SignUp Button Click
    fun onSignUpClick() {
        viewModelScope.launch {
            // Ensure the passwords match before proceeding
            if (uiState.value.password != uiState.value.confirmPassword) {
                throw Exception("Passwords do not match")
            }

            // Sign up using the repository
            val res = authRepository.signUp(
                SignUpModel(
                    email = uiState.value.email,
                    password = uiState.value.password,
                    firstName = uiState.value.firstName,
                    lastName = uiState.value.lastName,
                    dateOfBirth = uiState.value.dateOfBirth,
                    gender = uiState.value.gender
                )
            )

            when (res) {
                is Resource.Success -> sendSideEffect(SignUpSideEffect.SignUpSuccess)
                is Resource.Error -> sendSideEffect(
                    SignUpSideEffect.SignUpFailed(
                        res.exception.message ?: "Something went wrong."
                    )
                )
                is Resource.Loading -> {}
            }
        }
    }
}

