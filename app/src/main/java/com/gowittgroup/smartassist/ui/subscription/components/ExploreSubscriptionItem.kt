package com.gowittgroup.smartassist.ui.subscription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassistlib.models.subscriptions.Product

@Composable
internal fun ExploreSubscriptionItem(
    subscription: Product,
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

        subscription.offers.forEach { offer ->
            PlanItem(
                planId = offer.basePlanId,
                pricing = offer.pricingList.firstOrNull()?.let {
                    "${it.priceCurrencyCode} ${it.priceAmountMicros / 1_000_000}"
                } ?: "Pricing unavailable",
                duration = offer.pricingList.firstOrNull()?.billingPeriod
                    ?: "Duration unavailable",
                isSelected = selectedPlan == offer.basePlanId,
                onClick = { planId, offerToken ->
                    onPlanSelected(planId, offerToken)
                },
                offerToken = offer.offerToken
            )
        }
    }
}