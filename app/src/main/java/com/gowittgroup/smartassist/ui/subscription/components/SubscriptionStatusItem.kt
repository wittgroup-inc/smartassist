package com.gowittgroup.smartassist.ui.subscription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.util.toFormattedDate
import com.gowittgroup.smartassistlib.models.subscriptions.SubscriptionStatus

@Composable
fun SubscriptionStatusItem(subscriptionStatus: SubscriptionStatus) {
    val purchaseDate = subscriptionStatus.purchaseTime.toFormattedDate()
    val expiryDate = subscriptionStatus.expiryTime?.toFormattedDate() ?: "N/A"
    val activeStatus = if (subscriptionStatus.isActive) "Active" else "Inactive"
    val activeColor =
        if (subscriptionStatus.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .background(color = Color.Transparent),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LabeledTile(label = "Product ID", value = subscriptionStatus.productId)
            Spacer(modifier = Modifier.height(8.dp))
            LabeledText(label = "Purchase Date", value = purchaseDate)
            Spacer(modifier = Modifier.height(8.dp))
            LabeledText(label = "Expiry Date", value = expiryDate)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Status: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = activeStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = activeColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LabeledText(label = "Subscription ID", value = subscriptionStatus.subscriptionId)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSubscriptionStatusItemActivePrev() {
    val sampleData =

        SubscriptionStatus(
            productId = "product_123",
            purchaseTime = System.currentTimeMillis() - 86_400_000,
            expiryTime = System.currentTimeMillis() + 86_400_000,
            isActive = true,
            subscriptionId = "sub_001"
        )

    SubscriptionStatusItem(sampleData)
}

@Composable
fun LabeledText(modifier: Modifier = Modifier, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun LabeledTile(modifier: Modifier = Modifier, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSubscriptionStatusItemInactivePrev() {
    val sampleData =
        SubscriptionStatus(
            productId = "product_456",
            purchaseTime = System.currentTimeMillis() - 2 * 86_400_000,
            expiryTime = System.currentTimeMillis() - 86_400_000,
            isActive = false,
            subscriptionId = "sub_002"
        )

    SubscriptionStatusItem(sampleData)
}
