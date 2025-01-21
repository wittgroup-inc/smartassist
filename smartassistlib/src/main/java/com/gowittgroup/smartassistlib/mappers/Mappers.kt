package com.gowittgroup.smartassistlib.mappers

import com.android.billingclient.api.ProductDetails
import com.gowittgroup.smartassistlib.db.entities.Conversation
import com.gowittgroup.smartassistlib.models.ai.Message
import com.gowittgroup.smartassistlib.models.subscriptions.Offer
import com.gowittgroup.smartassistlib.models.subscriptions.Pricing
import com.gowittgroup.smartassistlib.models.subscriptions.Product

fun ProductDetails.toProduct(): Product {
    val offers = this.subscriptionOfferDetails?.map { offerDetail ->
        Offer(
            offerId = offerDetail.offerId,
            basePlanId = offerDetail.basePlanId,
            offerToken = offerDetail.offerToken,
            pricingList = offerDetail.pricingPhases.pricingPhaseList.map { pricingPhase ->
                Pricing(
                    priceCurrencyCode = pricingPhase.priceCurrencyCode,
                    priceAmountMicros = pricingPhase.priceAmountMicros,
                    billingCycleCount = pricingPhase.billingCycleCount,
                    billingPeriod = pricingPhase.billingPeriod
                )
            }
        )
    }.orEmpty()

    return Product(
        productId = this.productId,
        productType = this.productType,
        title = this.title,
        description = this.description,
        price = this.oneTimePurchaseOfferDetails?.formattedPrice ?: "N/A",
        offers = offers
    )
}

fun List<ProductDetails>.toProductList(): List<Product> {
    return this.map { it.toProduct() }
}

fun Conversation.toMessage(): Message = with(this) {
    Message(
        role =
        if (forSystem) {
            Message.ROLE_SYSTEM
        } else if (isQuestion) {
            Message.ROLE_USER
        } else {
            Message.ROLE_ASSISTANT
        }, content = data
    )
}

fun List<Conversation>.toMessages(): List<Message> {
    return this.map { it.toMessage() }
}