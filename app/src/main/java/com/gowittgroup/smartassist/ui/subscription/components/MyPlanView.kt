package com.gowittgroup.smartassist.ui.subscription.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.components.buttons.TertiaryButton
import com.gowittgroup.smartassist.ui.subscription.SubscriptionUiState

@Composable
internal fun MyPlanView(
    uiState: SubscriptionUiState,
    switchToExplorePlan: () -> Unit
) {
    if (uiState.purchasedSubscriptions.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = stringResource(R.string.your_subscriptions),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(uiState.purchasedSubscriptions) { status ->
                MySubscriptionsItem(status)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                TertiaryButton(
                    text = stringResource(R.string.explore_plans),
                    onClick = switchToExplorePlan
                )
            }
        }
    }
}