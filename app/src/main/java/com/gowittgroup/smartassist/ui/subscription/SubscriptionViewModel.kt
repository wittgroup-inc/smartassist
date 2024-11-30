package com.gowittgroup.smartassist.ui.subscription

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.subscription.SubscriptionRepository
import com.gowittgroup.smartassistlib.models.subscriptions.SubscriptionStatus
import com.gowittgroup.smartassistlib.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : BaseViewModelWithStateIntentAndSideEffect<SubscriptionUiState, SubscriptionIntent, SubscriptionSideEffects>() {

    init {
        fetchSubscriptionStatus()
        fetchAvailableSubscriptions()
    }

    private fun fetchSubscriptionStatus() {
        viewModelScope.launch {
            uiState.value.copy(isLoading = true).applyStateUpdate()
            when (val res = subscriptionRepository.getSubscriptionStatus()) {
                is Resource.Error -> {
                    uiState.value.copy(isLoading = false).applyStateUpdate()
                    sendSideEffect(
                        SubscriptionSideEffects.ShowError(
                            res.exception.message ?: "Something went wrong."
                        )
                    )
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
                    sendSideEffect(
                        SubscriptionSideEffects.ShowError(
                            res.exception.message ?: "Something went wrong."
                        )
                    )
                }

            }
        }
    }

    fun onSubscriptionSelected(
        selectedSubscription: ProductDetails,
        offerToken: String,
        context: Context
    ) {
        viewModelScope.launch {
            uiState.value.copy(isPurchaseInProgress = true).applyStateUpdate()
            val res = subscriptionRepository.purchaseSubscription(
                activity = context as Activity,
                productDetails = selectedSubscription,
                offerToken = offerToken
            )

            when (res) {
                is Resource.Success -> {
                    uiState.value.copy(
                        isPurchaseInProgress = false,
                        purchaseStatus = res.data
                    ).applyStateUpdate()

                    if(res.data){
                        sendSideEffect(
                            SubscriptionSideEffects.PurchaseSuccess
                        )
                    } else {
                        sendSideEffect(
                            SubscriptionSideEffects.ShowError(
                                "Purchase failed"
                            )
                        )
                    }
                }


                is Resource.Error -> {
                    uiState.value.copy(isPurchaseInProgress = false).applyStateUpdate()
                    sendSideEffect(
                        SubscriptionSideEffects.ShowError(
                            res.exception.message ?: "Something went wrong."
                        )
                    )
                }
            }
        }
    }

    override fun getDefaultState(): SubscriptionUiState = SubscriptionUiState()

    override fun processIntent(intent: SubscriptionIntent) {
        //TODO Not yet implemented
    }

}

val sampleData = listOf(
    SubscriptionStatus(
        productId = "product_123",
        purchaseTime = System.currentTimeMillis() - 86_400_000, // 1 day ago
        expiryTime = System.currentTimeMillis() + 86_400_000, // 1 day later
        isActive = true,
        subscriptionId = "sub_001"
    ),
    SubscriptionStatus(
        productId = "product_456",
        purchaseTime = System.currentTimeMillis() - 2 * 86_400_000, // 2 days ago
        expiryTime = System.currentTimeMillis() - 86_400_000, // Expired
        isActive = false,
        subscriptionId = "sub_002"
    )
)
