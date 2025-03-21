package com.gowittgroup.smartassist.ui.auth.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun SignInScreenRoute(
    navigationActions: SmartAssistNavigationActions
) {
    val signInViewModel: SignInViewModel = hiltViewModel()
    val uiState by signInViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        signInViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is SignInSideEffect.SignInSuccess -> {
                    navigationActions.navigateToHome(null, null)
                }
            }
        }
    }

    SignInScreen(
        uiState = uiState,
        onEmailChange = signInViewModel::updateEmail,
        onPasswordChange = signInViewModel::updatePassword,
        onSignInClick = signInViewModel::onSignInClick,
        onSignInWithProvider = signInViewModel::signInWithProvider,
        navigateToSignUp = navigationActions.navigateToSignUp,
        onResetPasswordClick = signInViewModel::onResetPasswordClick,
        onNotificationClose = signInViewModel::onNotificationClose
    )
}