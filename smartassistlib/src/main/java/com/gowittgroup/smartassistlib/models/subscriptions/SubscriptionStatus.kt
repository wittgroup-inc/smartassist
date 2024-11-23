package com.gowittgroup.smartassistlib.models.subscriptions

data class SubscriptionStatus(
    val productId: String,
    val purchaseTime: Long,
    val expiryTime: Long?,
    val isActive: Boolean,
    val subscriptionId: String
)