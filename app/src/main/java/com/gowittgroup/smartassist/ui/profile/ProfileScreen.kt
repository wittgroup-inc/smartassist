package com.gowittgroup.smartassist.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import com.gowittgroup.smartassist.ui.components.MovingColorBarLoader
import com.gowittgroup.smartassist.ui.components.Notification
import com.gowittgroup.smartassist.ui.profile.components.EditMode
import com.gowittgroup.smartassist.ui.profile.components.ViewMode

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
    modifier: Modifier = Modifier,
    onNotificationClose: () -> Unit
) {

    var isEditMode by remember {
        mutableStateOf(false)
    }

    var showAvatarPicker by remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        when {
            uiState.notificationState != null -> Notification(
                notificationState = uiState.notificationState,
                onNotificationClose = onNotificationClose
            )

            else ->
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
        }
    }, content = { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isProfileUpdateInProgress) {
                MovingColorBarLoader()
            }
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.TopEnd,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(16.dp)
                            .clickable { if (isEditMode) showAvatarPicker = true }
                    ) {

                        AsyncImage(
                            model = uiState.photoUrl
                                ?: "https://api.dicebear.com/6.x/adventurer-neutral/svg?seed=default",
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.2f)),
                            contentScale = ContentScale.Crop
                        )

                        if (isEditMode) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Icon",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)

                                    .background(
                                        Color.DarkGray.copy(alpha = 0.7f),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isEditMode) {
                        ViewMode(uiState)
                    } else {
                        EditMode(
                            uiState,
                            onFirstNameChange,
                            onLastNameChange,
                            onDateOfBirthChange,
                            onSaveClick
                        )
                    }
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
    })
}


