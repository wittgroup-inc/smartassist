package com.gowittgroup.smartassist.ui.auth.signin.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.auth.signin.SignInUiState
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.buttons.TertiaryButton
import com.gowittgroup.smartassist.ui.components.textfields.PrimaryTextField
import com.gowittgroup.smartassistlib.models.authentication.AuthProvider

@Composable
internal fun SignInView(
    modifier: Modifier = Modifier,
    uiState: SignInUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onSignInWithProvider: (String, AuthProvider) -> Unit,
    switchToResetPassword: () -> Unit,
    navigateToSignUp: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholderText = stringResource(R.string.email),
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        PrimaryTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholderText = stringResource(R.string.password),
            leadingIcon = Icons.Default.Lock,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        PrimaryButton(
            onClick = onSignInClick,
            modifier = modifier
                .fillMaxWidth(),
            enabled = uiState.isSignInEnabled,
            text = stringResource(R.string.sign_in)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        GoogleSignInButton { token ->
            onSignInWithProvider(token, AuthProvider.GOOGLE)
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        TertiaryButton(
            onClick = switchToResetPassword,
            text = stringResource(R.string.forgot_password)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Text(
            text = stringResource(R.string.sign_up_description),
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium
        )
        TertiaryButton(
            onClick = { navigateToSignUp() },
            text = stringResource(R.string.sign_up)
        )
    }
}