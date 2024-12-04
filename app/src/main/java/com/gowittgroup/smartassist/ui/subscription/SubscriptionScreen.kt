package com.gowittgroup.smartassist.ui.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.MovingColorBarLoader
import com.gowittgroup.smartassist.ui.components.Notification
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.buttons.TertiaryButton
import com.gowittgroup.smartassist.ui.subscription.components.ExploreSubscriptionItem
import com.gowittgroup.smartassist.ui.subscription.components.MySubscriptionsItem
import com.gowittgroup.smartassistlib.util.Constants

@Composable
fun SubscriptionScreen(
    uiState: SubscriptionUiState,
    onBuyButtonClick: (ProductDetails, String) -> Unit,
    openDrawer: () -> Unit,
    isExpanded: Boolean,
    onNotificationClose: () -> Unit
) {
    var selectedSubscription by remember { mutableStateOf<ProductDetails?>(null) }
    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var selectedOfferToken by remember { mutableStateOf<String?>(null) }
    var explorePlans by remember {
        mutableStateOf(false)
    }


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
                        if (uiState.purchasedSubscriptions.isNotEmpty()) {

                            Text(
                                text = stringResource(R.string.your_subscriptions),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            LazyColumn(
                                modifier = Modifier.padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.purchasedSubscriptions) { status ->
                                    MySubscriptionsItem(status)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            TertiaryButton(
                                text = stringResource(R.string.explore_plans),
                                onClick = { explorePlans = true })

                        } else {
                            explorePlans = true
                        }
                    } else {
                        if (uiState.products.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_subscriptions_available_right_now),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.subscription_plans),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                                items(uiState.products) { subscription ->
                                    ExploreSubscriptionItem(
                                        subscription = subscription,
                                        isSelected = selectedSubscription == subscription,
                                        onPlanSelected = { planId, offerToken ->
                                            selectedSubscription = subscription
                                            selectedPlan = planId
                                            selectedOfferToken = offerToken
                                        },
                                        selectedPlan = selectedPlan
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    PrimaryButton(
                                        onClick = {
                                            val selectedProduct =
                                                uiState.products.find { it.title == selectedSubscription?.title }
                                            selectedProduct?.let { product ->
                                                selectedOfferToken?.let { offerToken ->
                                                    onBuyButtonClick(product, offerToken)
                                                }
                                            }
                                        },
                                        text = stringResource(R.string.buy_now),
                                        enabled = selectedPlan != null,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )

                                    if (uiState.purchasedSubscriptions.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        TertiaryButton(
                                            text = stringResource(R.string.my_plans),
                                            onClick = { explorePlans = false })
                                    }
                                }
                            }

                        }
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
