package com.gowittgroup.smartassistlib.models.subscriptions

import com.gowittgroup.smartassistlib.util.Constants

data class SubscriptionStatus(
    val productId: String,
    val purchaseTime: Long,
    val expiryTime: Long?,
    val isActive: Boolean,
    val subscriptionId: String
)

fun SubscriptionStatus.getProductName() = when(productId){
    Constants.SubscriptionSKUs.SMART_PREMIUM -> "Smart Premium"
    Constants.SubscriptionSKUs.BASIC_SUBSCRIPTION -> "Basic"
    else -> "Basic"
}

