package com.gowittgroup.smartassist.ui.subscription

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    LaunchedEffect(Unit) {
        subscriptionViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is SubscriptionSideEffects.ShowError -> {
                    Toast.makeText(context, sideEffect.error, Toast.LENGTH_SHORT).show()
                }

                SubscriptionSideEffects.PurchaseSuccess -> {
                    Toast.makeText(context, "Purchase Successful", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    SubscriptionScreen(
        uiState = uiState.value,
        onPlanSelected = { productDetails, offerToken ->
            subscriptionViewModel.onSubscriptionSelected(
                productDetails, offerToken, context
            )
        },
        openDrawer = openDrawer,
        isExpanded = expandedScreen
    )
}
