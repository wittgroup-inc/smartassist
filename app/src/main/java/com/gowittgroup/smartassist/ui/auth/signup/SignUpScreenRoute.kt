package com.gowittgroup.smartassist.ui.auth.signup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun SignUpScreenRoute(
    navigationActions: SmartAssistNavigationActions
) {
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    val uiState by signUpViewModel.uiState.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        signUpViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is SignUpSideEffect.SignUpFailed -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }

                is SignUpSideEffect.SignUpSuccess -> {

                    Toast.makeText(
                        context,
                        "You got registered with us successfully, please check your email and verify.",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigationActions.navigateToSignIn()
                }
            }
        }
    }

    SignUpScreen(
        uiState = uiState,
        onFirstNameChange = signUpViewModel::updateFirstName,
        onLastNameChange = signUpViewModel::updateLastName,
        onEmailChange = signUpViewModel::updateEmail,
        onPasswordChange = signUpViewModel::updatePassword,
        onConfirmPasswordChange = signUpViewModel::updateConfirmPassword,
        onDateOfBirthChange = signUpViewModel::updateDateOfBirth,
        onGenderChange = signUpViewModel::updateGender,
        onTermsCheckedChange = signUpViewModel::updateTermsChecked,
        onSignUpClick = signUpViewModel::onSignUpClick,
        navigateToSignIn = navigationActions.navigateToSignIn,
        termsAndConditionClick = { url ->
            openPolicyInBrowser(context, url)
        }
    )
}


private fun openPolicyInBrowser(context: Context, url: String) {

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    // Create a chooser so the user can pick the app to open the link
    val chooser = Intent.createChooser(intent, "Open URL with")
    context.startActivity(chooser)
}