package com.gowittgroup.smartassist.ui.auth.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.auth.components.DateOfBirthPicker
import com.gowittgroup.smartassist.ui.auth.components.GenderRadioGroup
import com.gowittgroup.smartassist.ui.auth.components.TermsAndConditionsLink
import com.gowittgroup.smartassist.ui.auth.signup.components.Notification
import com.gowittgroup.smartassist.ui.components.MovingColorBarLoader
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.buttons.TertiaryButton
import com.gowittgroup.smartassist.ui.components.textfields.PrimaryTextField


@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onTermsCheckedChange: (Boolean) -> Unit,
    onSignUpClick: () -> Unit,
    navigateToSignIn: () -> Unit,
    termsAndConditionClick: (String) -> Unit,
    onNotificationClose: () -> Unit,
    closeNotificationAndNavigateToLogin: () -> Unit
) {

    Box(modifier = Modifier.fillMaxWidth()) {
        if (uiState.isLoading) {
            MovingColorBarLoader()
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp, 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.illustration_signup),
                contentDescription = "Auth image",
                modifier = modifier
                    .width(240.dp)
                    .padding(16.dp, 4.dp)
            )
            Spacer(modifier = Modifier.padding(12.dp))
            PrimaryTextField(
                value = uiState.firstName,
                onValueChange = onFirstNameChange,
                placeholderText = stringResource(R.string.first_name),
                leadingIcon = Icons.Default.Person,
                error = uiState.firstNameError ?: ""
            )
            Spacer(modifier = Modifier.padding(8.dp))
            PrimaryTextField(
                value = uiState.lastName,
                onValueChange = onLastNameChange,
                placeholderText = stringResource(R.string.last_name),
                leadingIcon = Icons.Default.Person,
                error = uiState.lastNameError ?: ""
            )
            Spacer(modifier = Modifier.padding(8.dp))
            PrimaryTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                placeholderText = stringResource(R.string.email),
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                error = uiState.emailError ?: ""
            )
            Spacer(modifier = Modifier.padding(8.dp))
            PrimaryTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                placeholderText = stringResource(R.string.password),
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                error = uiState.passwordError ?: ""
            )
            Spacer(modifier = Modifier.padding(8.dp))
            PrimaryTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholderText = stringResource(R.string.confirm_password),
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                error = uiState.confirmPasswordError ?: ""
            )
            Spacer(modifier = Modifier.padding(8.dp))
            DateOfBirthPicker(
                value = uiState.dateOfBirth,
                onValueChange = onDateOfBirthChange,
                placeholderText = stringResource(R.string.date_of_birth),
                error = uiState.dateOfBirthError ?: ""
            )
            Spacer(modifier = Modifier.padding(8.dp))
            GenderRadioGroup(
                value = uiState.gender,
                onValueChange = onGenderChange,
                options = listOf("Male", "Female", "Other"),
                placeholderText = stringResource(R.string.gender),
                error = uiState.genderError ?: ""
            )
            Spacer(modifier = Modifier.padding(8.dp))
            TermsAndConditionsLink(termsAndConditionClick)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Checkbox(
                    checked = uiState.isTermsAccepted,
                    onCheckedChange = onTermsCheckedChange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.accept_terms_conditions),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.padding(12.dp))
            PrimaryButton(
                onClick = onSignUpClick,
                modifier = modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.sign_up),
                enabled = uiState.isSignUpEnabled
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = stringResource(R.string.sign_in_description),
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium
            )
            TertiaryButton(
                onClick = { navigateToSignIn() },
                text = stringResource(R.string.sign_in)
            )
        }
        Notification(
            uiState,
            onNotificationClose,
            closeNotificationAndNavigateToLogin
        )
    }

}




