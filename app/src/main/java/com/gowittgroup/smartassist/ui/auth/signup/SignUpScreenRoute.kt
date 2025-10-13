package com.gowittgroup.smartassist.ui.auth.signup

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
                is SignUpSideEffect.NavigateToLogin -> {
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
        onTermsCheckedChange = signUpViewModel::updateTermsChecked,
        onSignUpClick = signUpViewModel::onSignUpClick,
        navigateToSignIn = navigationActions.navigateToSignIn,
        termsAndConditionClick = { url ->
            openPolicyInBrowser(context, url)
        },
        onNotificationClose = signUpViewModel::onNotificationClose,
        closeNotificationAndNavigateToLogin = signUpViewModel::closeNotificationAndNavigateToLogin
    )
}

private fun openPolicyInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val chooser = Intent.createChooser(intent, "Open URL with")
    context.startActivity(chooser)
}