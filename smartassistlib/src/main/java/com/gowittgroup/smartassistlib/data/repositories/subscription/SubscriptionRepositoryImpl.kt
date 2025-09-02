package com.gowittgroup.smartassistlib.data.repositories.subscription

import android.app.Activity
import com.gowittgroup.smartassistlib.data.datasources.subscription.Event
import com.gowittgroup.smartassistlib.data.datasources.subscription.SubscriptionDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.subscription.SubscriptionRepository
import com.gowittgroup.smartassistlib.models.subscriptions.Product
import com.gowittgroup.smartassistlib.models.subscriptions.Subscription
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionDataSource: SubscriptionDataSource
) : SubscriptionRepository {

    override val events: SharedFlow<Event> = subscriptionDataSource.events

    override suspend fun getAvailableSubscriptions(skuList: List<String>): Resource<List<Product>> =
        subscriptionDataSource.getAvailableSubscriptions(skuList)

    override suspend fun purchaseSubscription(
        activity: Activity,
        product: Product,
        offerToken: String
    ): Resource<Boolean> =
        subscriptionDataSource.purchaseSubscription(activity, product, offerToken)

    override suspend fun handlePurchaseUpdate(): Resource<Boolean> =
        subscriptionDataSource.handlePurchaseUpdate()

    override suspend fun getMySubscriptions(): Resource<List<Subscription>> =
        subscriptionDataSource.getMySubscriptions()

    override suspend fun hasActiveSubscription(): Resource<Boolean> = subscriptionDataSource.hasActiveSubscription()

    companion object {
        private val TAG = SubscriptionRepositoryImpl::class.java.simpleName
    }
}
