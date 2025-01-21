package com.gowittgroup.smartassist.ui.subscription

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassist.ui.components.NotificationType
import com.gowittgroup.smartassist.util.Session
import com.gowittgroup.smartassistlib.data.datasources.subscription.Event
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.settings.SettingsRepository
import com.gowittgroup.smartassistlib.domain.repositories.subscription.SubscriptionRepository
import com.gowittgroup.smartassistlib.models.subscriptions.Product
import com.gowittgroup.smartassistlib.models.subscriptions.Subscription
import com.gowittgroup.smartassistlib.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val settingsRepository: SettingsRepository
) : BaseViewModelWithStateIntentAndSideEffect<SubscriptionUiState, SubscriptionIntent, SubscriptionSideEffects>() {

    init {
        fetchMySubscriptions()
        fetchAvailableSubscriptions()
        observePurchaseStatus()
    }

    private fun fetchMySubscriptions() {
        viewModelScope.launch {
            uiState.value.copy(isLoading = true).applyStateUpdate()
            when (val res = subscriptionRepository.getMySubscriptions()) {
                is Resource.Error -> {
                    uiState.value.copy(isLoading = false).applyStateUpdate()
                    publishErrorState(res.exception.message ?: "Something went wrong.")
                }

                is Resource.Success -> {
                    uiState.value.copy(isLoading = false, purchasedSubscriptions = res.data)
                        .applyStateUpdate()
                }
            }
        }

    }

    private fun fetchAvailableSubscriptions() {
        viewModelScope.launch {

            uiState.value.copy(isLoading = true).applyStateUpdate()

            val res = subscriptionRepository.getAvailableSubscriptions(
                listOf(
                    Constants.SubscriptionSKUs.SMART_PREMIUM,
                    Constants.SubscriptionSKUs.BASIC_SUBSCRIPTION,
                )
            )
            when (res) {
                is Resource.Success ->
                    uiState.value.copy(products = res.data, isLoading = false)
                        .applyStateUpdate()

                is Resource.Error -> {
                    uiState.value.copy(isLoading = false).applyStateUpdate()
                    publishErrorState(res.exception.message ?: "Something went wrong.")
                }
            }
        }
    }

    fun onBuySubscription(
        selectedSubscription: Product,
        offerToken: String,
        context: Context
    ) {
        viewModelScope.launch {
            uiState.value.copy(isPurchaseInProgress = true).applyStateUpdate()

            val res = subscriptionRepository.purchaseSubscription(
                activity = context as Activity,
                product = selectedSubscription,
                offerToken = offerToken
            )
            when (res) {
                is Resource.Success -> {
                    uiState.value.copy(
                        isPurchaseInProgress = false,
                        purchaseStatus = res.data
                    ).applyStateUpdate()
                }

                is Resource.Error -> {
                    uiState.value.copy(isPurchaseInProgress = false).applyStateUpdate()
                    publishErrorState(res.exception.message ?: "Something went wrong.")
                }
            }
        }
    }

    override fun getDefaultState(): SubscriptionUiState = SubscriptionUiState()

    override fun processIntent(intent: SubscriptionIntent) {
        //TODO Not yet implemented
    }

    private fun publishErrorState(message: String) {
        uiState.value.copy(
            notificationState =
            NotificationState(
                message = message,
                type = NotificationType.ERROR,
                autoDismiss = true
            )

        ).applyStateUpdate()
    }

    private fun publishPurchaseSuccessState() {
        uiState.value.copy(
            notificationState =
            NotificationState(
                message = "Subscription purchased successfully",
                type = NotificationType.SUCCESS,
                autoDismiss = true
            )
        ).applyStateUpdate()
    }

    fun onNotificationClose() {
        uiState.value.copy(
            notificationState = null
        ).applyStateUpdate()
    }

    private fun observePurchaseStatus() {
        viewModelScope.launch {
            subscriptionRepository.events.collect { event ->
                when (event) {
                    is Event.PurchaseStatus.Success -> {
                        settingsRepository.setUserSubscriptionStatus(true)
                        Session.subscriptionStatus = true
                        publishPurchaseSuccessState()
                        delay(1000)
                        fetchMySubscriptions()
                    }

                    is Event.PurchaseStatus.Error -> publishErrorState(event.message)
                }
            }
        }
    }
}

val sampleData = listOf(
    Subscription(
        productId = "product_123",
        purchaseTime = System.currentTimeMillis() - 86_400_000, // 1 day ago
        expiryTime = System.currentTimeMillis() + 86_400_000, // 1 day later
        isActive = true,
        subscriptionId = "sub_001"
    ),
    Subscription(
        productId = "product_456",
        purchaseTime = System.currentTimeMillis() - 2 * 86_400_000, // 2 days ago
        expiryTime = System.currentTimeMillis() - 86_400_000, // Expired
        isActive = false,
        subscriptionId = "sub_002"
    )
)
