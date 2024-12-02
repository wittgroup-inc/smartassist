package com.gowittgroup.smartassistlib.data.repositories.subscription

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassistlib.data.datasources.subscription.Event
import com.gowittgroup.smartassistlib.data.datasources.subscription.SubscriptionDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.subscription.SubscriptionRepository
import com.gowittgroup.smartassistlib.models.subscriptions.SubscriptionStatus
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionDataSource: SubscriptionDataSource
) : SubscriptionRepository {

    override val events: SharedFlow<Event> = subscriptionDataSource.events

    override suspend fun getAvailableSubscriptions(skuList: List<String>): Resource<List<ProductDetails>> =
        subscriptionDataSource.getAvailableSubscriptions(skuList)

    override suspend fun purchaseSubscription(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ): Resource<Boolean> =
        subscriptionDataSource.purchaseSubscription(activity, productDetails, offerToken)

    override suspend fun handlePurchaseUpdate(): Resource<Boolean> =
        subscriptionDataSource.handlePurchaseUpdate()

    override suspend fun getSubscriptionStatus(): Resource<List<SubscriptionStatus>> =
        subscriptionDataSource.getSubscriptionStatus()

    companion object {
        private val TAG = SubscriptionRepositoryImpl::class.java.simpleName
    }
}
