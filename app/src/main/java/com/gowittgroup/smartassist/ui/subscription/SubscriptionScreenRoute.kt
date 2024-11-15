package com.gowittgroup.smartassist.ui.subscription

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassist.ui.analytics.SmartAnalytics
import com.gowittgroup.smartassist.ui.settingsscreen.SettingsViewModel
import com.gowittgroup.smartassistlib.models.Resource

@Composable
fun SubscriptionScreenRoute(
    expandedScreen: Boolean,
    openDrawer: () -> Unit,
    smartAnalytics: SmartAnalytics
) {

    val subscriptionViewModel: SubscriptionViewModel = hiltViewModel()

    val availableSubscriptions =
        subscriptionViewModel.availableSubscriptions.collectAsState(initial = Resource.Loading)
    val purchaseStatus =
        subscriptionViewModel.purchaseStatus.collectAsState(initial = Resource.Loading)

    val context = LocalContext.current
    SubscriptionScreen(
        availableSubscriptions,
        purchaseStatus,
        onSubscriptionSelected = { productId ->
            subscriptionViewModel.onSubscriptionSelected(
                productId, context
            )
        }
    )
}
