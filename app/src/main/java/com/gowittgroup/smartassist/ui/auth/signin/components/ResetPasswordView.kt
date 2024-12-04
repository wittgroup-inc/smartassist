package com.gowittgroup.smartassist.ui.auth.signin.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.auth.signin.SignInUiState
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.buttons.TertiaryButton
import com.gowittgroup.smartassist.ui.components.textfields.PrimaryTextField


@Composable
internal fun ResetPasswordView(
    modifier: Modifier = Modifier,
    uiState: SignInUiState,
    onEmailChange: (String) -> Unit,
    onResetPasswordClick: () -> Unit,
    switchToSignIn: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholderText = stringResource(R.string.email),
            keyboardType = KeyboardType.Email,
            leadingIcon = Icons.Default.Email
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        PrimaryButton(
            onClick = onResetPasswordClick,
            modifier = modifier
                .fillMaxWidth(),
            enabled = uiState.isRestPasswordEnabled,
            text = stringResource(R.string.reset_password)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        TertiaryButton(
            onClick = switchToSignIn,
            text = stringResource(R.string.sign_in)
        )
    }
}