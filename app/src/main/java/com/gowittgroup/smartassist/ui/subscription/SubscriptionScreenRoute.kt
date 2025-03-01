package com.gowittgroup.smartassist.ui.subscription

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics

@Composable
fun SubscriptionScreenRoute(
    expandedScreen: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics
) {

    val subscriptionViewModel: SubscriptionViewModel = hiltViewModel()

    val uiState =
        subscriptionViewModel.uiState.collectAsState()

    val context = LocalContext.current

    SubscriptionScreen(
        uiState = uiState.value,
        onBuyButtonClick = { productDetails, offerToken ->
            subscriptionViewModel.onBuySubscription(
                productDetails, offerToken, context
            )
        },
        openDrawer = openDrawer,
        isExpanded = expandedScreen,
        onNotificationClose = subscriptionViewModel::onNotificationClose
    )
}
