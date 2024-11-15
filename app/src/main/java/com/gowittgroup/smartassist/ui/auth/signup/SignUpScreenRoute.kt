package com.gowittgroup.smartassist.ui.auth.signup

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
                    // Handle navigation action here
                    navigationActions.navigateToHome(null, null)
                }
            }
        }
    }

    SignUpScreen(
        uiState = uiState,
        onEmailChange = signUpViewModel::updateEmail,
        onPasswordChange = signUpViewModel::updatePassword,
        onConfirmPasswordChange = signUpViewModel::updateConfirmPassword,
        onSignUpClick = signUpViewModel::onSignUpClick,
        navigateToSignIn = navigationActions.navigateToSignIn
    )
}