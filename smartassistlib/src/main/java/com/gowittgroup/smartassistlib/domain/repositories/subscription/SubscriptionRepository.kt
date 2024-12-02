package com.gowittgroup.smartassistlib.domain.repositories.subscription

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassistlib.data.datasources.subscription.Event
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.subscriptions.SubscriptionStatus
import kotlinx.coroutines.flow.SharedFlow

interface SubscriptionRepository {

    val events: SharedFlow<Event>

    suspend fun getAvailableSubscriptions(skuList: List<String>): Resource<List<ProductDetails>>


    suspend fun purchaseSubscription(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ): Resource<Boolean>


    suspend fun handlePurchaseUpdate(): Resource<Boolean>


    suspend fun getSubscriptionStatus(): Resource<List<SubscriptionStatus>>

}