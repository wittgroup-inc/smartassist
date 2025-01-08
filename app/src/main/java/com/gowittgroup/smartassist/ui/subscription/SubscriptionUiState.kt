package com.gowittgroup.smartassist.ui.subscription


import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassistlib.models.subscriptions.Product
import com.gowittgroup.smartassistlib.models.subscriptions.Subscription

data class SubscriptionUiState(
    val notificationState: NotificationState? = null,
    val isLoading: Boolean = false,
    val isPurchaseInProgress: Boolean = false,
    val purchasedSubscriptions: List<Subscription> = listOf(),
    val products: List<Product> = emptyList(),
    val purchaseStatus: Boolean? = null
) : State