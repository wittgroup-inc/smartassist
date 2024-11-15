package com.gowittgroup.smartassist.ui.subscription


import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassist.core.State

sealed class SubscriptionUiState : State {
    data object Loading : SubscriptionUiState()
    data object Default : SubscriptionUiState()
    data class Error(val message: String) : SubscriptionUiState()
    data class Success(val products: List<ProductDetails> = emptyList(), val purchaseStatus: Boolean? = null) : SubscriptionUiState()
}