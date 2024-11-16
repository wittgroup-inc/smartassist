package com.gowittgroup.smartassist.ui.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.AppBar
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.util.Constants

@Composable
fun SubscriptionScreen(
    uiState: SubscriptionUiState,
    onPlanSelected: (ProductDetails, String) -> Unit,
    openDrawer: () -> Unit,
    isExpanded: Boolean
) {
    var selectedSubscription by remember { mutableStateOf<ProductDetails?>(null) }
    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var selectedOfferToken by remember { mutableStateOf<String?>(null) }


    Scaffold(topBar = {
        AppBar(
            title = stringResource(R.string.subscription_screen_title),
            openDrawer = openDrawer,
            isExpanded = isExpanded
        )
    }, content = { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Explore Subscription Plans",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (uiState) {
                is SubscriptionUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is SubscriptionUiState.Success -> {
                    if (uiState.products.isEmpty()) {
                        Text(
                            text = "No subscriptions available right now.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn {
                            items(uiState.products) { subscription ->
                                SubscriptionItem(
                                    subscription = subscription,
                                    isSelected = selectedSubscription == subscription,
                                    onPlanSelected = { planId, offerToken ->
                                        selectedSubscription = subscription
                                        selectedPlan = planId
                                        selectedOfferToken = offerToken
                                    },
                                    selectedPlan = selectedPlan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            onClick = {
                                val selectedProduct =
                                    uiState.products.find { it.title == selectedSubscription?.title }
                                selectedProduct?.let { product ->
                                    selectedOfferToken?.let { offerToken ->
                                        onPlanSelected(product, offerToken)
                                    }
                                }
                            },
                            text = "Buy Now",
                            enabled = selectedPlan != null,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                is SubscriptionUiState.Error -> {
                    Text(
                        text = "Failed to load subscriptions: ${uiState.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                SubscriptionUiState.Default -> {}
            }
        }
    })
}

@Composable
fun SubscriptionItem(
    subscription: ProductDetails,
    isSelected: Boolean,
    onPlanSelected: (String, String) -> Unit,
    selectedPlan: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Text(
            text = subscription.title,
            style = MaterialTheme.typography.headlineMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = subscription.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        subscription.subscriptionOfferDetails?.forEach { offerDetail ->
            PlanItem(
                planId = offerDetail.basePlanId,
                pricing = offerDetail.pricingPhases.pricingPhaseList.firstOrNull()?.let {
                    "${it.priceCurrencyCode} ${it.priceAmountMicros / 1_000_000}"
                } ?: "Pricing unavailable",
                duration = offerDetail.pricingPhases.pricingPhaseList.firstOrNull()?.billingPeriod
                    ?: "Duration unavailable",
                isSelected = selectedPlan == offerDetail.basePlanId,
                onClick = { planId, offerToken ->
                    onPlanSelected(planId, offerToken)
                },
                offerToken = offerDetail.offerToken ?: ""
            )
        }
    }
}

@Composable
fun PlanItem(
    planId: String,
    pricing: String,
    duration: String,
    isSelected: Boolean,
    onClick: (String, String) -> Unit,
    offerToken: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
            .clickable {
                onClick(planId, offerToken)
            }
            .padding(16.dp)
    ) {
        Text(
            text = getPlanTitleForId(planId),
            style = MaterialTheme.typography.titleLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row {
            Text(
                text = "Price: $pricing",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

fun getPlanTitleForId(planId: String): String {
    return when (planId) {
        Constants.SmartPremiumPlans.SMART_DAILY -> "Daily Plan"
        Constants.SmartPremiumPlans.SMART_MONTHLY -> "Monthly Plan"
        Constants.SmartPremiumPlans.SMART_YEARLY -> "Yearly Plan"
        else -> "Unknown"
    }
}
