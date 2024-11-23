package com.gowittgroup.smartassist.ui.subscription

import com.gowittgroup.smartassist.core.SideEffect

sealed class SubscriptionSideEffects : SideEffect {
    data class ShowError(val error: String) : SubscriptionSideEffects()
    data object PurchaseSuccess: SubscriptionSideEffects()
}
