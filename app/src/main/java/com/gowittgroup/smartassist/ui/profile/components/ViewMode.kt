package com.gowittgroup.smartassist.ui.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.profile.ProfileUiState

@Composable
internal fun ViewMode(uiState: ProfileUiState) {
    Column {
        ProfileField(
            label = stringResource(R.string.first_name),
            value = uiState.firstName
        )
        ProfileField(
            label = stringResource(R.string.last_name),
            value = uiState.lastName
        )
        ProfileField(label = stringResource(R.string.email), value = uiState.email)
        ProfileField(
            label = stringResource(R.string.date_of_birth),
            value = uiState.dateOfBirth
        )
        ProfileField(
            label = stringResource(R.string.gender),
            value = uiState.gender
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}