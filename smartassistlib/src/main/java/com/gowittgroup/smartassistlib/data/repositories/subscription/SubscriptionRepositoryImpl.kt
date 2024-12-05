package com.gowittgroup.smartassistlib.data.repositories.subscription

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassistlib.data.datasources.settings.SettingsDataSource
import com.gowittgroup.smartassistlib.data.datasources.subscription.Event
import com.gowittgroup.smartassistlib.data.datasources.subscription.SubscriptionDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.subscription.SubscriptionRepository
import com.gowittgroup.smartassistlib.models.subscriptions.Subscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionDataSource: SubscriptionDataSource,
    private val settingsDataSource: SettingsDataSource
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

    override suspend fun getMySubscriptions(): Resource<List<Subscription>> =
        subscriptionDataSource.getMySubscriptions()

    override suspend fun hasActiveSubscription(): Resource<Boolean> = withContext(Dispatchers.IO) {
        when (val res = subscriptionDataSource.getMySubscriptions()) {
            is Resource.Success -> {
                if (res.data.isNotEmpty() && res.data.any { it.isActive }) {
                    settingsDataSource.setUserSubscriptionStatus(true)
                    Resource.Success(true)
                } else {
                    settingsDataSource.setUserSubscriptionStatus(false)
                    Resource.Success(false)
                }
            }

            is Resource.Error -> res
        }
    }

    companion object {
        private val TAG = SubscriptionRepositoryImpl::class.java.simpleName
    }
}
