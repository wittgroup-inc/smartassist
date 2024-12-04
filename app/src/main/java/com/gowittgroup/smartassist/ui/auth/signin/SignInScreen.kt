package com.gowittgroup.smartassist.ui.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.auth.signin.components.Notification
import com.gowittgroup.smartassist.ui.auth.signin.components.ResetPasswordView
import com.gowittgroup.smartassist.ui.auth.signin.components.SignInView
import com.gowittgroup.smartassist.ui.components.MovingColorBarLoader

@Composable
fun SignInScreen(
    uiState: SignInUiState,
    navigateToSignUp: () -> Unit,
    onSignInClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNotificationClose: () -> Unit
) {
    var isRestPassword by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        if (uiState.isLoading) {
            MovingColorBarLoader()
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp, 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.illustration_signin),
                contentDescription = "Auth image",
                modifier = modifier
                    .width(240.dp)
                    .padding(16.dp, 4.dp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
            if (isRestPassword) {
                ResetPasswordView(
                    uiState = uiState,
                    onEmailChange = onEmailChange,
                    onResetPasswordClick = onResetPasswordClick,
                    modifier = modifier,
                    switchToSignIn = { isRestPassword = false }
                )
            } else {
                SignInView(
                    uiState = uiState,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onSignInClick = onSignInClick,
                    modifier = modifier,
                    switchToResetPassword = {
                        isRestPassword = true
                    },
                    navigateToSignUp = navigateToSignUp
                )
            }
        }
        Notification(
            uiState.notificationState,
            onNotificationClose,
            switchToSignIn = { isRestPassword = false }
        )
    }
}





