package com.gowittgroup.smartassist.ui.faqscreen

import android.os.Bundle
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.analytics.FakeAnalytics
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.LoadingScreen
import com.gowittgroup.smartassist.ui.faqscreen.components.FaqItem


@Composable
fun FaqScreen(
    uiState: FaqUiState,
    isExpanded: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics
) {

    logUserEntersEvent(smartAnalytics)

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


private fun logUserEntersEvent(smartAnalytics: SmartAnalytics) {
    val bundle = Bundle()
    bundle.putString(SmartAnalytics.Param.SCREEN_NAME, "faq_screen")
    smartAnalytics.logEvent(SmartAnalytics.Event.USER_ON_SCREEN, bundle)
}


@Preview
@Composable
fun FaqScreenPreview() {
    FaqScreen(
        uiState = FaqUiState(),
        isExpanded = false,
        openDrawer = { },
        smartAnalytics = FakeAnalytics()
    )
}
