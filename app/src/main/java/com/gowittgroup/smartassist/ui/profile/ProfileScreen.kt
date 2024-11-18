package com.gowittgroup.smartassist.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.AvatarPickerDialog
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.textfields.PrimaryTextField

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onSaveClick: () -> Unit,
    expandedScreen: Boolean,
    openDrawer: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    onCancel: () -> Unit,
    onAvtarSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var isEditMode by remember {
        mutableStateOf(false)
    }

    var showAvatarPicker by remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        AppBar(
            title = stringResource(R.string.profile_screen_title),
            openDrawer = openDrawer,
            isExpanded = expandedScreen,
            actions = {
                IconButton(onClick = {
                    if (isEditMode) {
                        onCancel()
                    }
                    isEditMode = !isEditMode
                }) {
                    Icon(
                        imageVector = (if (isEditMode) Icons.Filled.Close else Icons.Filled.Edit),
                        ""
                    )
                }
            }
        )
    }, content = { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.TopEnd, // Ensures the edit icon aligns to the top-right
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp)
                        .clickable { if (isEditMode) showAvatarPicker = true }
                ) {
                    // AsyncImage to load the profile picture or default image
                    AsyncImage(
                        model = uiState.photoUrl
                            ?: "https://api.dicebear.com/6.x/adventurer-neutral/svg?seed=default", // Fallback to default
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape) // Make the image circular
                            .background(Color.Gray.copy(alpha = 0.2f)), // Add a subtle background
                        contentScale = ContentScale.Crop // Ensure the image fits well in a circular shape
                    )

                    // Show edit icon only in edit mode
                    if (isEditMode) {
                        Icon(
                            imageVector = Icons.Default.Edit, // Default edit icon
                            contentDescription = "Edit Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)

                                .background(
                                    Color.DarkGray.copy(alpha = 0.7f),
                                    shape = CircleShape
                                )
                                .padding(4.dp)// Background for better visibility
                                .align(Alignment.TopEnd) // Place in the top-right corner
                        )
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                if (!isEditMode) {
                    // View Mode
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
                    ProfileField(label = stringResource(R.string.gender), value = uiState.gender)

                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    // Edit Mode
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
                    Spacer(modifier = Modifier.height(8.dp))

                    PrimaryTextField(
                        value = uiState.dateOfBirth,
                        onValueChange = { onDateOfBirthChange(it) },
                        placeholderText = stringResource(R.string.date_of_birth),
                        isEnabled = false,
                        readOnly = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    PrimaryTextField(
                        value = uiState.gender,
                        onValueChange = { },
                        placeholderText = stringResource(R.string.gender),
                        readOnly = true,
                        isEnabled = false
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PrimaryButton(
                        onClick = {
                            onSaveClick()  // Trigger any additional save action outside of the ViewModel
                        },
                        text = stringResource(R.string.save_changes),
                    )
                }
            }
        }
        if (showAvatarPicker) {
            AvatarPickerDialog(
                userId = uiState.id,
                selectedAvatar = uiState.photoUrl,
                onAvatarSelected = onAvtarSelected,
                onDismissRequest = { showAvatarPicker = false })
        }
    }


    )


}

@Composable
fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}


