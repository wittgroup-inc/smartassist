package com.gowittgroup.smartassist.ui.splashscreen.components

import androidx.compose.runtime.Composable
import com.gowittgroup.smartassist.util.Session

@Composable
internal fun Navigate(
    navigateToHome: (id: Long?, prompt: String?) -> Unit,
    navigateToSignIn: () -> Unit = {}
) {
    if (Session.currentUser != null) {
        navigateToHome(null, null)
    } else {
        navigateToSignIn()
    }
}