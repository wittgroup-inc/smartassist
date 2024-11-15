package com.gowittgroup.smartassist.ui.subscription

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassist.ui.components.buttons.PrimaryButton

@Composable
fun SubscriptionScreen(
    uiState: SubscriptionUiState,
    onSubscriptionSelected: (ProductDetails) -> Unit
) {

    // UI with Loading, Error, and Success states
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Available Subscriptions", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        when (uiState) {
            is SubscriptionUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is SubscriptionUiState.Success -> {
                if (uiState.products.isEmpty()) {
                    Text(text = "No subscriptions available.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    SubscriptionList(uiState.products) { selectedSubscription ->
                        onSubscriptionSelected(selectedSubscription)
                    }
                }
            }
            is SubscriptionUiState.Error -> {
                Text(text = "Failed to load subscriptions: ${uiState.message}", style = MaterialTheme.typography.bodyMedium)
            }

            SubscriptionUiState.Default -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Displaying purchase result

        if (uiState is SubscriptionUiState.Success && uiState.purchaseStatus != null) {
            if (uiState.purchaseStatus) {
                Text(text = "Purchase successful!", color = Color.Green)
            }
        }
    }
}

@Composable
fun SubscriptionList(
    subscriptions: List<ProductDetails>,
    onSubscriptionSelected: (ProductDetails) -> Unit
) {
    LazyColumn {
        items(subscriptions) { subscription ->
            SubscriptionItem(subscription, onSubscriptionSelected)
        }
    }
}

@Composable
fun SubscriptionItem(
    subscription: ProductDetails,
    onSubscriptionSelected: (ProductDetails) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onSubscriptionSelected(subscription) }
        .padding(16.dp)) {
        Text(text = subscription.title, style = MaterialTheme.typography.headlineSmall)
        Text(text = subscription.description, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(onClick = { onSubscriptionSelected(subscription) }, text = "Purchase")
    }
}