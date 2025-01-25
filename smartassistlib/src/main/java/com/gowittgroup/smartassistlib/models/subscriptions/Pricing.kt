package com.gowittgroup.smartassistlib.models.subscriptions

data class Pricing(
    val priceCurrencyCode: String?,
    val priceAmountMicros: Long,
    val billingCycleCount: Int,
    val billingPeriod: String
)