package com.gowittgroup.smartassistlib.domain.repositories.subscription

import android.app.Activity
import com.gowittgroup.smartassistlib.data.datasources.subscription.Event
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.models.subscriptions.Product
import com.gowittgroup.smartassistlib.models.subscriptions.Subscription
import kotlinx.coroutines.flow.SharedFlow

interface SubscriptionRepository {

    val events: SharedFlow<Event>

    suspend fun getAvailableSubscriptions(skuList: List<String>): Resource<List<Product>>

    suspend fun purchaseSubscription(
        activity: Activity,
        product: Product,
        offerToken: String
    ): Resource<Boolean>


    suspend fun handlePurchaseUpdate(): Resource<Boolean>

    suspend fun getMySubscriptions(): Resource<List<Subscription>>

    suspend fun hasActiveSubscription(): Resource<Boolean>
}