package com.gowittgroup.smartassistlib.models.subscriptions

data class Offer(
    val offerId: String?,
    val basePlanId: String,
    val offerToken: String,
    val pricingList: List<Pricing>
)

