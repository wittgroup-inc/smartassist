package com.gowittgroup.smartassist.ui.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.textfields.PrimaryTextField
import com.gowittgroup.smartassist.ui.profile.ProfileUiState

@Composable
internal fun EditMode(
    uiState: ProfileUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    onGenderChange: (String)-> Unit,
    onSaveClick: () -> Unit
) {
    Column {
        PrimaryTextField(
            value = uiState.firstName,
            onValueChange = { onFirstNameChange(it) },
            placeholderText = stringResource(R.string.first_name)
        )
        Spacer(modifier = Modifier.height(8.dp))

        PrimaryTextField(
            value = uiState.lastName,
            onValueChange = { onLastNameChange(it) },
            placeholderText = stringResource(R.string.last_name)
        )
        Spacer(modifier = Modifier.height(8.dp))

        PrimaryTextField(
            value = uiState.email,
            onValueChange = { },
            placeholderText = stringResource(R.string.email),
            readOnly = true,
            isEnabled = false
        )
        Spacer(modifier = Modifier.padding(8.dp))
        DateOfBirthPicker(
            value = uiState.dateOfBirth,
            onValueChange = onDateOfBirthChange,
            placeholderText = stringResource(R.string.date_of_birth)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        GenderRadioGroup(
            value = uiState.gender,
            onValueChange = onGenderChange,
            options = listOf("Male", "Female", "Other"),
            placeholderText = stringResource(R.string.gender)
        )
        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            onClick = {
                onSaveClick()
            },
            text = stringResource(R.string.save_changes),
        )
    }

}