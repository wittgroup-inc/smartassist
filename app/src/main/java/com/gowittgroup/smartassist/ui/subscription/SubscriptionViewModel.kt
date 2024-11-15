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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionDatasource: SubscriptionDatasourceImpl
) : BaseViewModelWithStateIntentAndSideEffect<SubscriptionUiState, SubscriptionIntent, SubscriptionSideEffects>() {

    private val _availableSubscriptions =
        MutableStateFlow<Resource<List<ProductDetails>>>(Resource.Loading)
    val availableSubscriptions = _availableSubscriptions.asStateFlow()

    private val _purchaseStatus = MutableStateFlow<Resource<Boolean>>(Resource.Loading)
    val purchaseStatus = _purchaseStatus.asStateFlow()

    init {
        fetchAvailableSubscriptions()
    }

    private fun fetchAvailableSubscriptions() {
        viewModelScope.launch {
            _availableSubscriptions.value = Resource.Loading
            _availableSubscriptions.value = subscriptionDatasource.getAvailableSubscriptions(
                listOf(
                    Constants.SubscriptionSKUs.SUB_DAILY,
                    Constants.SubscriptionSKUs.SUB_MONTHLY,
                    Constants.SubscriptionSKUs.SUB_YEARLY
                )
            )
        }
    }

    fun onSubscriptionSelected(selectedSubscription: ProductDetails, context: Context) {
        viewModelScope.launch {
            _purchaseStatus.value = Resource.Loading
            _purchaseStatus.value = subscriptionDatasource.purchaseSubscription(
                activity = context as Activity,
                productDetails = selectedSubscription
            )
        }
    }

    override fun getDefaultState(): SubscriptionUiState = SubscriptionUiState()

    override fun processIntent(intent: SubscriptionIntent) {
        TODO("Not yet implemented")
    }

}
