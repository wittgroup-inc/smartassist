package com.gowittgroup.smartassist.ui.subscription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gowittgroup.smartassist.util.copyTextToClipboard
import com.gowittgroup.smartassist.util.toFormattedDate
import com.gowittgroup.smartassistlib.models.subscriptions.Subscription
import com.gowittgroup.smartassistlib.models.subscriptions.getProductName

@Composable
fun SubscriptionStatusItem(subscription: Subscription) {
    val purchaseDate = subscription.purchaseTime.toFormattedDate()
    val expiryDate = subscription.expiryTime?.toFormattedDate() ?: "N/A"
    val activeStatus = if (subscription.isActive) "Active" else "Inactive"
    val activeColor =
        if (subscription.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

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
            LabeledTitle(label = "Product", value = subscription.getProductName())
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
            LabeledText(label = "Subscription ID", value = subscription.subscriptionId, enableCopy = true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSubscriptionStatusItemActivePrev() {
    val sampleData =
        Subscription(
            productId = "product_123",
            purchaseTime = System.currentTimeMillis() - 86_400_000,
            expiryTime = System.currentTimeMillis() + 86_400_000,
            isActive = true,
            subscriptionId = "ikbmkenaccofeadfcbfhgknc.AO-J1Oxxt6wSXjwR_uANhqVVxTvujLG9nfhpaAtKcabcXWZxpGS58nSBu6Eza3Z_wgc3P5M6nUjdKUrAozIo_cTeFjRnsYb5jZsFUHoB4J0S4s7EbgltLdw"
        )

    SubscriptionStatusItem(sampleData)
}

@Composable
fun LabeledText(modifier: Modifier = Modifier, label: String, value: String, enableCopy: Boolean = false) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        if(enableCopy){
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { copyTextToClipboard(context, value) }) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "")
            }
        }
    }
}

@Composable
fun LabeledTitle(modifier: Modifier = Modifier, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
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
        Subscription(
            productId = "product_456",
            purchaseTime = System.currentTimeMillis() - 2 * 86_400_000,
            expiryTime = System.currentTimeMillis() - 86_400_000,
            isActive = false,
            subscriptionId = "sub_002"
        )

    SubscriptionStatusItem(sampleData)
}
