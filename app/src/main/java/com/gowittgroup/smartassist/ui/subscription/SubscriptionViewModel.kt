package com.gowittgroup.smartassist.ui.subscription

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassist.util.Constants
import com.gowittgroup.smartassistlib.datasources.subscription.SubscriptionDatasourceImpl
import com.gowittgroup.smartassistlib.models.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionDatasource: SubscriptionDatasourceImpl
) : BaseViewModelWithStateIntentAndSideEffect<SubscriptionUiState, SubscriptionIntent, SubscriptionSideEffects>() {

    init {
        fetchAvailableSubscriptions()
    }

    private fun fetchAvailableSubscriptions() {
        viewModelScope.launch {

            SubscriptionUiState.Loading.applyStateUpdate()

            val res = subscriptionDatasource.getAvailableSubscriptions(
                listOf(
                    Constants.SubscriptionSKUs.SUB_DAILY,
                    Constants.SubscriptionSKUs.SUB_MONTHLY,
                    Constants.SubscriptionSKUs.SUB_YEARLY
                )
            )
            when (res) {
                is Resource.Success -> SubscriptionUiState.Success(products = res.data).applyStateUpdate()

                is Resource.Error -> SubscriptionUiState.Error(
                    res.exception.message ?: "Something went wrong."
                ).applyStateUpdate()

                Resource.Loading -> {}
            }
        }
    }

    fun onSubscriptionSelected(selectedSubscription: ProductDetails, context: Context) {
        viewModelScope.launch {
            SubscriptionUiState.Loading.applyStateUpdate()
            val res = subscriptionDatasource.purchaseSubscription(
                activity = context as Activity, productDetails = selectedSubscription
            )

            when (res) {
                is Resource.Success -> if (uiState.value is SubscriptionUiState.Success) (uiState.value as SubscriptionUiState.Success).copy(
                    purchaseStatus = res.data
                ).applyStateUpdate() else SubscriptionUiState.Success(
                    purchaseStatus = res.data
                ).applyStateUpdate()

                is Resource.Error -> SubscriptionUiState.Error(
                    res.exception.message ?: "Something went wrong."
                ).applyStateUpdate()

                Resource.Loading -> TODO()
            }
        }
    }

    override fun getDefaultState(): SubscriptionUiState = SubscriptionUiState.Default

    override fun processIntent(intent: SubscriptionIntent) {
        TODO("Not yet implemented")
    }

}
