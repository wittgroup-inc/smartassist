package com.gowittgroup.smartassist.ui.faqscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.BuildConfig
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.LoadingScreen





@Composable
fun FaqScreen(
    uiState: FaqUiState,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics,
    refreshErrorMessage: () -> Unit
) {

    logUserEntersEvent(smartAnalytics)
    ErrorView(uiState.error).also { refreshErrorMessage() }

    Scaffold(topBar = {
        AppBar(
            title = stringResource(R.string.faq_screen_title),
            openDrawer = openDrawer,
            isExpanded = isExpanded
        )
    }, content = { padding ->
        if (uiState.loading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {

                items(uiState.faqs, key = { it.ques }) { faq ->
                    FaqItem(faq)
                }


            }
        }

    }
    )
}

@Composable
private fun FaqItem(faq: Faq) {

    Column {
        Text(
            text = "Q: ${faq.ques}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        )
        Text(
            text = "A: ${faq.ans}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        )
        HorizontalDivider()
    }

}


private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "settings_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}


@Composable
fun ErrorView(message: String) {
    var showError by remember { mutableStateOf(false) }
    showError = message.isNotEmpty()
    val context = LocalContext.current
    if (showError) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}


@Preview
@Composable
fun FaqScreenPreview() {
    FaqScreen(
        uiState = FaqUiState(),
        isExpanded = false,
        openDrawer = { },
        smartAnalytics = FakeAnalytics(),
        refreshErrorMessage = { }
    )
}
