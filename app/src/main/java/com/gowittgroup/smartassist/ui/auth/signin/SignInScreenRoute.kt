package com.gowittgroup.smartassist.ui.auth.signin

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.components.AutoDismissibleInAppNotification
import com.gowittgroup.smartassist.ui.components.InAppNotification
import com.gowittgroup.smartassist.ui.components.NotificationType
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

data class NotificationState(
    val message: String,
    val type: NotificationType,
    val autoDismiss: Boolean = true
)

@Composable
fun SignInScreenRoute(
    navigationActions: SmartAssistNavigationActions
) {
    val signInViewModel: SignInViewModel = hiltViewModel()
    val uiState by signInViewModel.uiState.collectAsState()
    var notificationState by remember { mutableStateOf<NotificationState?>(null) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        signInViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is SignInSideEffect.ShowError -> {
                    notificationState = NotificationState(
                        message = sideEffect.message,
                        type = NotificationType.ERROR
                    )
                    Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
                }

                is SignInSideEffect.SignInSuccess -> {

                    navigationActions.navigateToHome(null, null)
                }

                SignInSideEffect.RestPasswordSuccess -> Toast.makeText(
                    context,
                    "Reset mail sent successfully, please check your email to reset password",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    if (notificationState != null) {
       notificationState?.let {
           if(it.autoDismiss){
               AutoDismissibleInAppNotification(
                   message = it.message,
                   type = it.type,
                   onClose = {
                       notificationState = null
                   }
               )
           } else {
               InAppNotification(
                   message = it.message,
                   type = it.type,
                   onClose = {
                       notificationState = null
                   }
               )
           }
       }
    }

    SignInScreen(
        uiState = uiState,
        onEmailChange = signInViewModel::updateEmail,
        onPasswordChange = signInViewModel::updatePassword,
        onSignInClick = signInViewModel::onSignInClick,
        navigateToSignUp = navigationActions.navigateToSignUp,
        onResetPasswordClick = signInViewModel::onResetPasswordClick
    )
}