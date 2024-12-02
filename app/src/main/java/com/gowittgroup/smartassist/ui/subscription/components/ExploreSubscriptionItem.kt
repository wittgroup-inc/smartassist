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
import com.android.billingclient.api.ProductDetails

@Composable
internal fun ExploreSubscriptionItem(
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
                offerToken = offerDetail.offerToken
            )
        }
    }
}