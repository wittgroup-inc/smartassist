package com.gowittgroup.smartassist.ui.subscription.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton
import com.gowittgroup.smartassist.ui.components.buttons.TertiaryButton
import com.gowittgroup.smartassist.ui.subscription.SubscriptionUiState
import com.gowittgroup.smartassistlib.models.subscriptions.Product

@Composable
internal fun ExplorePlanView(
    uiState: SubscriptionUiState,
    onBuyButtonClick: (Product, String) -> Unit,
    switchToMyPlanView: () -> Unit
) {
    var selectedSubscription by remember { mutableStateOf<Product?>(null) }
    var selectedPlan by remember { mutableStateOf<String?>(null) }
    var selectedOfferToken by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            if (uiState.products.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_subscriptions_available_right_now),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = stringResource(R.string.subscription_plans),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

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
        }

        if (uiState.products.isNotEmpty()) {
            item {
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
                    enabled = selectedPlan != null
                )
            }
        }

        item {
            if (uiState.purchasedSubscriptions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                TertiaryButton(
                    text = stringResource(R.string.my_plans),
                    onClick = switchToMyPlanView
                )
            }
        }
    }
}