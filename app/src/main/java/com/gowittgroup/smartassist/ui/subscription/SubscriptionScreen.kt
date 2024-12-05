package com.gowittgroup.smartassist.ui.subscription

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.MovingColorBarLoader
import com.gowittgroup.smartassist.ui.components.Notification
import com.gowittgroup.smartassist.ui.subscription.components.ExplorePlanView
import com.gowittgroup.smartassist.ui.subscription.components.MyPlanView
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme
import com.gowittgroup.smartassistlib.util.Constants

@Composable
fun SubscriptionScreen(
    uiState: SubscriptionUiState,
    onBuyButtonClick: (ProductDetails, String) -> Unit,
    openDrawer: () -> Unit,
    isExpanded: Boolean,
    onNotificationClose: () -> Unit
) {

    var explorePlans by remember {
        mutableStateOf(false)
    }

    explorePlans = uiState.purchasedSubscriptions.isEmpty()

    Scaffold(topBar = {
        when {
            uiState.notificationState != null ->
                Notification(uiState.notificationState, onNotificationClose)

            else -> AppBar(
                title = stringResource(R.string.subscription_screen_title),
                openDrawer = openDrawer,
                isExpanded = isExpanded
            )
        }
    }, content = { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (uiState.isPurchaseInProgress) {
                MovingColorBarLoader()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    if (!explorePlans) {
                        MyPlanView(
                            uiState = uiState,
                            switchToExplorePlan = { explorePlans = true }
                        )
                    } else {
                        ExplorePlanView(
                            uiState = uiState,
                            onBuyButtonClick = onBuyButtonClick,
                            switchToMyPlanView = { explorePlans = false }
                        )
                    }

                }
            }
        }
    })
}

fun getPlanTitleForId(planId: String): String {
    return when (planId) {
        Constants.SmartPremiumPlans.SMART_DAILY -> "Daily Plan"
        Constants.SmartPremiumPlans.SMART_MONTHLY -> "Monthly Plan"
        Constants.SmartPremiumPlans.SMART_YEARLY -> "Yearly Plan"
        else -> "Unknown"
    }
}

@Preview
@Composable
private fun SubscriptionScreenPrev() {
    SmartAssistTheme {
        SubscriptionScreen(
            uiState = SubscriptionUiState(),
            onBuyButtonClick = { _, _ -> },
            openDrawer = { },
            isExpanded = false,
            onNotificationClose = {}
        )
    }
}