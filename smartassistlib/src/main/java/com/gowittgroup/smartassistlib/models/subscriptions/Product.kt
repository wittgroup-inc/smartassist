package com.gowittgroup.smartassistlib.models.subscriptions

data class Product(
    val productId: String,
    val productType: String,
    val title: String,
    val description: String,
    val price: String,
    val offers: List<Offer>
)
