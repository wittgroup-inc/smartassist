package com.gowittgroup.smartassist.ui.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.buttons.TertiaryButton
import com.gowittgroup.smartassist.ui.components.textfields.PrimaryTextField


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SignInScreen(
    uiState: SignInUiState,
    navigateToSignUp: () -> Unit,
    onSignInClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val email = uiState.email
    val password = uiState.password

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.illustration_signin),
            contentDescription = "Auth image",
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        PrimaryTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholderText = stringResource(R.string.email),
            leadingIcon = Icons.Default.Email
        )

        PrimaryTextField(
            value = password,
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
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            text = stringResource(R.string.sign_in)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Text(
            text = stringResource(R.string.sign_up_description),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )

        TertiaryButton(onClick = { navigateToSignUp() }, text = stringResource(R.string.sign_up))
    }
}
