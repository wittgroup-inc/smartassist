package com.gowittgroup.smartassistlib.data.datasources.subscription

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.firebase.firestore.FirebaseFirestore
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassistlib.data.datasources.authentication.AuthenticationDataSource
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SubscriptionDatasourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authenticationDataSource: AuthenticationDataSource
) : SubscriptionDataSource, PurchasesUpdatedListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun startBillingConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                SmartLog.d(TAG, "Billing service disconnected. Retrying...")
                startBillingConnection()
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    SmartLog.d(TAG, "Billing service connected successfully.")
                } else {
                    SmartLog.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                }
            }
        })
    }

    override suspend fun getAvailableSubscriptions(skuList: List<String>): Resource<List<ProductDetails>> {
        ensureBillingConnected()
        return queryProductDetails(skuList)
    }

    private suspend fun queryProductDetails(skuList: List<String>): Resource<List<ProductDetails>> {
        return suspendCancellableCoroutine { continuation ->
            val productList = skuList.map { sku ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(sku)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            }

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(Resource.Success(productDetailsList.orEmpty()))
                } else {
                    SmartLog.e(TAG, "Failed to query subscriptions: ${billingResult.debugMessage}")
                    continuation.resume(Resource.Error(RuntimeException("Failed to query subscriptions: ${billingResult.debugMessage}")))
                }
            }
        }
    }

    override suspend fun purchaseSubscription(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ): Resource<Boolean> {
        ensureBillingConnected()
        return processPurchase(activity, productDetails, offerToken)
    }

    private suspend fun processPurchase(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String
    ): Resource<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(offerToken)
                            .build()
                    )
                )
                .build()

            val result = billingClient.launchBillingFlow(activity, billingFlowParams)
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                continuation.resume(Resource.Success(true))
            } else {
                continuation.resume(Resource.Error(RuntimeException("Error launching billing flow: ${result.debugMessage}")))
            }
        }
    }

    override suspend fun handlePurchaseUpdate(): Resource<Boolean> {
        ensureBillingConnected()
        return checkForActivePurchases()
    }

    private suspend fun checkForActivePurchases(): Resource<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (purchases.isNullOrEmpty()) {
                        continuation.resume(Resource.Error(RuntimeException("No active purchases found.")))
                    } else {
                        // Return the result of handlePurchasedSubscriptions as Resource<Boolean>
                        continuation.resume(handlePurchasedSubscriptions(purchases))
                    }
                } else {
                    continuation.resume(Resource.Error(RuntimeException("Failed to query purchases: ${billingResult.debugMessage}")))
                }
            }
        }
    }

    private fun handlePurchasedSubscriptions(purchases: List<Purchase>): Resource<Boolean> {
        val hasPurchased = purchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        val isPending = purchases.any { it.purchaseState == Purchase.PurchaseState.PENDING }

        return when {
            hasPurchased -> Resource.Success(true)
            isPending -> Resource.Success(false)
            else -> Resource.Error(RuntimeException("No valid purchase state found."))
        }
    }


    override suspend fun getSubscriptionStatus(): Resource<Map<String, Any>?> {
        ensureBillingConnected()
        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val activeSubscriptions = purchases?.filter {
                        it.purchaseState == Purchase.PurchaseState.PURCHASED
                    }

                    if (activeSubscriptions.isNullOrEmpty()) {
                        continuation.resume(Resource.Success(mapOf(Constants.SubscriptionDataKey.STATUS to Constants.SubscriptionStatusValue.INACTIVE)))
                    } else {
                        continuation.resume(
                            Resource.Success(
                                mapOf(
                                    Constants.SubscriptionStatusResultKey.STATUS to Constants.SubscriptionStatusValue.ACTIVE,
                                    Constants.SubscriptionStatusResultKey.SUBSCRIPTION to activeSubscriptions
                                )
                            )
                        )
                    }
                } else {
                    continuation.resume(Resource.Error(RuntimeException("Failed to query subscriptions: ${billingResult.debugMessage}")))
                }
            }
        }
    }

    // Updating the onPurchasesUpdated method to call saveSubscription after acknowledgment.
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // Acknowledge the purchase and save the subscription
                    handlePurchase(purchase)
                }
            }
        } else if (billingResult.responseCode != BillingClient.BillingResponseCode.USER_CANCELED) {
            SmartLog.e(TAG, "Purchase failed: ${billingResult.debugMessage}")
        }
    }

    // Handling the purchase and saving it to Firestore.
    @OptIn(DelicateCoroutinesApi::class)
    private fun handlePurchase(purchase: Purchase) {
        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                SmartLog.d(TAG, "Purchase acknowledged successfully.")

                // Save the subscription after acknowledgment inside a coroutine
                val subscriptionId = purchase.purchaseToken // Or use purchase.sku if needed
                val expiryDate =
                    purchase.purchaseTime.toString() // You might want to use a more precise expiry date here

                // Using a coroutine to call the suspend function
                GlobalScope.launch(Dispatchers.Main) {
                    val result = saveSubscription(subscriptionId, expiryDate)
                    if (result is Resource.Success) {
                        SmartLog.d(TAG, "Subscription saved successfully.")
                    } else {
                        SmartLog.e(TAG, "Failed to save subscription: ${result}")
                    }
                }

            } else {
                SmartLog.e(TAG, "Acknowledging purchase failed: ${billingResult.debugMessage}")
            }
        }
    }

    // Save the subscription details to Firestore.
    private suspend fun saveSubscription(
        subscriptionId: String,
        expiryDate: String
    ): Resource<Boolean> {
        return saveSubscriptionToFirestore(subscriptionId, expiryDate)
    }

    private suspend fun saveSubscriptionToFirestore(
        subscriptionId: String,
        expiryDate: String
    ): Resource<Boolean> {
        val userId = authenticationDataSource.currentUserId
        if (userId.isEmpty()) {
            return Resource.Error(RuntimeException("User is not logged in."))
        }

        val subscriptionData = hashMapOf(
            Constants.SubscriptionDataKey.SUBSCRIPTION_ID to subscriptionId,
            Constants.SubscriptionDataKey.STATUS to Constants.SubscriptionStatusValue.ACTIVE,
            Constants.SubscriptionDataKey.EXPIRY_DATE to expiryDate
        )

        return suspendCancellableCoroutine { continuation ->
            firestore.collection(Constants.SUBSCRIPTION_COLLECTION_PATH)
                .document(userId)
                .set(subscriptionData)
                .addOnSuccessListener {
                    SmartLog.d(TAG, "Subscription saved successfully.")
                    continuation.resume(Resource.Success(true))
                }
                .addOnFailureListener { e ->
                    SmartLog.e(TAG, "Failed to save subscription: ${e.message}")
                    continuation.resume(Resource.Error(e))
                }
        }
    }

    private suspend fun ensureBillingConnected() {
        if (!billingClient.isReady) {
            suspendCancellableCoroutine<Unit> { continuation ->
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingServiceDisconnected() {
                        SmartLog.e(TAG, "Billing service disconnected.")
                    }

                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            continuation.resume(Unit)
                        } else {
                            continuation.resumeWithException(
                                RuntimeException("Failed to connect to Billing service: ${billingResult.debugMessage}")
                            )
                        }
                    }
                })
            }
        }
    }

    companion object {
        private val TAG = SubscriptionDatasourceImpl::class.java.simpleName
    }
}
