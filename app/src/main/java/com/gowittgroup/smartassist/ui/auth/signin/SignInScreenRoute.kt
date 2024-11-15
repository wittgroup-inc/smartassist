package com.gowittgroup.smartassist.ui.auth.signin

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun SignInScreenRoute(
    navigationActions: SmartAssistNavigationActions
) {
    val signInViewModel: SignInViewModel = hiltViewModel()
    val uiState by signInViewModel.uiState.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        signInViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is SignInSideEffect.SignInFailed -> {
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }

                is SignInSideEffect.SignInSuccess -> {
                    // Handle navigation action here
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
        navigateToSignUp = navigationActions.navigateToSignUp
    )
}