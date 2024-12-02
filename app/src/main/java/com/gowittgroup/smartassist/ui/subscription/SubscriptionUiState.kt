package com.gowittgroup.smartassist.ui.subscription


import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassistlib.models.subscriptions.SubscriptionStatus

data class SubscriptionUiState(
    val notificationState: NotificationState? = null,
    val isLoading: Boolean = false,
    val isPurchaseInProgress: Boolean = false,
    val purchasedSubscriptions: List<SubscriptionStatus> = listOf(),
    val products: List<ProductDetails> = emptyList(),
    val purchaseStatus: Boolean? = null
) : State