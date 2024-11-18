package com.gowittgroup.smartassistlib.domain.repositories.subscription

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassistlib.domain.models.Resource

interface SubscriptionRepository {

    // Fetch available subscriptions
    suspend fun getAvailableSubscriptions(skuList: List<String>): Resource<List<ProductDetails>>

    // Launch the subscription purchase flow
    suspend fun purchaseSubscription(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ): Resource<Boolean>

    // Handle purchase updates
    suspend fun handlePurchaseUpdate(): Resource<Boolean>

    // Fetch user subscription status
    suspend fun getSubscriptionStatus(): Resource<Map<String, Any>?>


}