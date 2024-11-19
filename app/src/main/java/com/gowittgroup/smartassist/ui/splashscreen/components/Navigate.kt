package com.gowittgroup.smartassist.ui.splashscreen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.util.Session

@Composable
internal fun Navigate(
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    navigateToSignIn: () -> Unit = {}
) {
    val currentUser = Session.currentUser.collectAsState()
    SmartLog.d("Pawan >> Splash", "Navigate ${currentUser.value}")
    if (currentUser.value != null) {
        navigateToHome(null, null)
    } else {
        navigateToSignIn()
    }
}