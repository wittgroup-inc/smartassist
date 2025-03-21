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
import com.gowittgroup.smartassistlib.mappers.toProductList
import com.gowittgroup.smartassistlib.models.subscriptions.Product
import com.gowittgroup.smartassistlib.models.subscriptions.Subscription
import com.gowittgroup.smartassistlib.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.coroutines.resume

sealed class Event {
    sealed class PurchaseStatus {
        data class Success(val message: String) : Event()
        data class Error(val message: String) : Event()
    }
}

data class CurrentPurchaseDetail(val offerToken: String, val durationInDays: Int)

class SubscriptionDatasourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authenticationDataSource: AuthenticationDataSource,
    private val firestore: FirebaseFirestore
) : SubscriptionDataSource, PurchasesUpdatedListener {

    private var productDetailsCache: List<ProductDetails> = listOf()

    private var currentPurchaseDetail: CurrentPurchaseDetail? = null

    private val _events = MutableSharedFlow<Event>() // Event emitter
    override val events: SharedFlow<Event> = _events

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val connectionMutex = Mutex()
    private var billingConnectionDeferred: CompletableDeferred<Unit>? = null

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    override suspend fun getAvailableSubscriptions(skuList: List<String>): Resource<List<Product>> {
        try {
            ensureBillingConnected()
        } catch (e: Exception) {
            return Resource.Error(
                RuntimeException("Could not query subscriptions: ${e.message}")
            )
        }
        return queryProductDetails(skuList)
    }

    private suspend fun queryProductDetails(skuList: List<String>): Resource<List<Product>> {
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
                    productDetailsCache = productDetailsList
                    val customProductDetailsList = productDetailsList.toProductList()
                    continuation.resume(Resource.Success(customProductDetailsList))
                } else {
                    productDetailsCache = listOf()
                    SmartLog.e(TAG, "Failed to query subscriptions: ${billingResult.debugMessage}")
                    continuation.resume(
                        Resource.Error(
                            RuntimeException("Failed to query subscriptions: ${billingResult.debugMessage}")
                        )
                    )
                }
            }
        }
    }

    override suspend fun purchaseSubscription(
        activity: Activity,
        product: Product,
        offerToken: String
    ): Resource<Boolean> {
        try {
            ensureBillingConnected()
        } catch (e: Exception) {
            return Resource.Error(
                RuntimeException("Could not proceed with purchase: ${e.message}")
            )
        }
        return processPurchase(activity, product, offerToken)
    }

    private suspend fun processPurchase(
        activity: Activity,
        product: Product,
        offerToken: String
    ): Resource<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            productDetailsCache.firstOrNull { it.productId == product.productId }
                ?.let { productDetails ->
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                            listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(offerToken)
                                    .build()
                            )
                        ).build()
                    val result = billingClient.launchBillingFlow(activity, billingFlowParams)
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        currentPurchaseDetail =
                            CurrentPurchaseDetail(
                                offerToken,
                                getDurationInDays(productDetails, offerToken)
                            )
                        continuation.resume(Resource.Success(true))
                    } else {
                        continuation.resume(Resource.Error(RuntimeException("Error launching billing flow: ${result.debugMessage}")))
                    }
                } ?: continuation.resume(Resource.Error(RuntimeException("Invalid Product")))
        }
    }

    private fun getDurationInDays(
        productDetails: ProductDetails,
        offerToken: String
    ): Int {
        val offerDetail =
            productDetails.subscriptionOfferDetails?.first { it.offerToken == offerToken }
        offerDetail?.let {
            val duration = it.pricingPhases.pricingPhaseList.firstOrNull()?.billingPeriod ?: ""
            return when (duration) {
                Constants.SubscriptionDurationCode.ONE_DAY -> 1
                Constants.SubscriptionDurationCode.ONE_MONTH -> 30
                Constants.SubscriptionDurationCode.ONE_YEAR -> 365
                else -> 0
            }
        }
        return 0
    }

    override suspend fun handlePurchaseUpdate(): Resource<Boolean> {
        try {
            ensureBillingConnected()
        } catch (e: Exception) {
            return Resource.Error(
                RuntimeException("Could not proceed ${e.message}")
            )
        }
        return checkForActivePurchases()
    }

    private suspend fun checkForActivePurchases(): Resource<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (purchases.isNullOrEmpty()) {
                        continuation.resume(Resource.Error(RuntimeException("No active purchases found.")))
                    } else {
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


    override suspend fun getMySubscriptions(): Resource<List<Subscription>> {
        try {
            ensureBillingConnected()
        } catch (e: Exception) {
            return Resource.Error(
                RuntimeException("Failed to query subscriptions: ${e.message}")
            )
        }

        return suspendCancellableCoroutine { continuation ->
            billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val subscriptions = purchases.map { purchase ->
                        Subscription(
                            productId = purchase.skus.firstOrNull() ?: "Unknown",
                            subscriptionId = purchase.purchaseToken,
                            purchaseTime = purchase.purchaseTime,
                            expiryTime = null,
                            isActive = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                        )
                    }

                    mergeSubscriptionsWithFirestore(subscriptions) { updatedSubscriptions ->
                        continuation.resume(Resource.Success(updatedSubscriptions))
                    }
                } else {
                    continuation.resume(
                        Resource.Error(
                            RuntimeException("Failed to query subscriptions: ${billingResult.debugMessage}")
                        )
                    )
                }
            }
        }
    }

    private fun mergeSubscriptionsWithFirestore(
        subscriptions: List<Subscription>,
        onComplete: (List<Subscription>) -> Unit
    ) {
        val userId = authenticationDataSource.currentUserId()
        if (userId.isEmpty()) {
            SmartLog.e(TAG, "User is not logged in. Cannot fetch Firestore data.")
            onComplete(subscriptions)
            return
        }

        firestore.collection(Constants.SUBSCRIPTION_COLLECTION_PATH)
            .document(userId)
            .collection(Constants.SUBSCRIPTION_SUB_COLLECTION_PATH)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val firestoreSubscriptions = querySnapshot.documents.associate { document ->
                    val subscriptionId = document.id
                    val subscriptionData = Subscription(
                        productId = document.getString(Constants.SubscriptionDataKey.PRODUCT_ID)
                            ?: "Unknown",
                        subscriptionId = subscriptionId,
                        purchaseTime = document.getString(Constants.SubscriptionDataKey.PURCHASE_DATE)
                            ?.toLong() ?: 0L,
                        expiryTime = document.getString(Constants.SubscriptionDataKey.EXPIRY_DATE)
                            ?.toLong() ?: 0L,
                        isActive = (document.getString(Constants.SubscriptionDataKey.STATUS)
                            ?: Constants.SubscriptionStatusValue.INACTIVE) == Constants.SubscriptionStatusValue.ACTIVE
                    )
                    subscriptionId to subscriptionData
                }

                val updatedSubscriptions = subscriptions.map { subscription ->
                    val firestoreSubscription = firestoreSubscriptions[subscription.subscriptionId]
                    firestoreSubscription?.let {
                        subscription.copy(
                            expiryTime = it.expiryTime
                        )
                    } ?: subscription
                }

                onComplete(updatedSubscriptions)
            }
            .addOnFailureListener { e ->
                SmartLog.e(TAG, "Failed to fetch Firestore data: ${e.message}")
                onComplete(subscriptions)
            }
    }

    /**
     * This callback is responsible to get purchase Acknowledgement and Save data to firestore
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    handlePurchase(purchase, currentPurchaseDetail?.durationInDays ?: 0)
                }
            }
        } else if (billingResult.responseCode != BillingClient.BillingResponseCode.USER_CANCELED) {
            SmartLog.e(TAG, "Purchase failed: ${billingResult.debugMessage}")
        }

        currentPurchaseDetail = null
    }

    private fun handlePurchase(purchase: Purchase, duration: Int) {
        val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                SmartLog.d(TAG, "Purchase acknowledged successfully.")

                val productId = purchase.skus.firstOrNull() ?: "Unknown"
                val subscriptionId = purchase.purchaseToken
                val purchaseTime = purchase.purchaseTime.toString()
                val expiryTime = calculateExpiryDate(
                    purchase.purchaseTime,
                    duration
                )


                scope.launch(Dispatchers.Main) {
                    val result = saveSubscription(
                        subscriptionId = subscriptionId,
                        productId = productId,
                        purchaseTime = purchaseTime,
                        expiryTime = expiryTime
                    )
                    if (result is Resource.Success) {
                        SmartLog.d(TAG, "Subscription saved successfully.")
                        _events.emit(Event.PurchaseStatus.Success("Subscriptions purchased successfully."))
                    } else {
                        SmartLog.e(TAG, "Failed to save subscription: $result")
                    }
                }

            } else {
                SmartLog.e(TAG, "Acknowledging purchase failed: ${billingResult.debugMessage}")
                scope.launch(Dispatchers.Main) {
                    _events.emit(Event.PurchaseStatus.Error("Purchase failed"))
                }
            }
        }
    }

    private suspend fun saveSubscription(
        subscriptionId: String,
        productId: String,
        purchaseTime: String,
        expiryTime: String
    ): Resource<Boolean> {
        return saveSubscriptionToFirestore(
            subscriptionId = subscriptionId,
            productId = productId,
            purchaseTime = purchaseTime,
            expiryTime = expiryTime
        )
    }

    private suspend fun saveSubscriptionToFirestore(
        subscriptionId: String,
        productId: String,
        purchaseTime: String,
        expiryTime: String
    ): Resource<Boolean> {
        val userId = authenticationDataSource.currentUserId()
        if (userId.isEmpty()) {
            return Resource.Error(RuntimeException("User is not logged in."))
        }

        val subscriptionData = hashMapOf(
            Constants.SubscriptionDataKey.SUBSCRIPTION_ID to subscriptionId,
            Constants.SubscriptionDataKey.PRODUCT_ID to productId,
            Constants.SubscriptionDataKey.STATUS to Constants.SubscriptionStatusValue.ACTIVE,
            Constants.SubscriptionDataKey.PURCHASE_DATE to purchaseTime,
            Constants.SubscriptionDataKey.EXPIRY_DATE to expiryTime
        )

        return suspendCancellableCoroutine { continuation ->
            firestore.collection(Constants.SUBSCRIPTION_COLLECTION_PATH)
                .document(userId)
                .collection(Constants.SUBSCRIPTION_SUB_COLLECTION_PATH)
                .document(subscriptionId)
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
        connectionMutex.withLock {
            if (!billingClient.isReady) {
                billingConnectionDeferred?.let { ongoingDeferred ->
                    ongoingDeferred.await()
                    return
                }

                val deferred = CompletableDeferred<Unit>()
                billingConnectionDeferred = deferred

                try {
                    billingClient.startConnection(object : BillingClientStateListener {
                        override fun onBillingServiceDisconnected() {
                            SmartLog.e(TAG, "Billing service disconnected.")
                            billingConnectionDeferred = null // Clear the deferred
                        }

                        override fun onBillingSetupFinished(billingResult: BillingResult) {
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                deferred.complete(Unit) // Signal successful connection
                            } else {
                                deferred.completeExceptionally(
                                    RuntimeException("Failed to connect to Billing service: ${billingResult.debugMessage}")
                                )
                            }
                            billingConnectionDeferred = null // Clear the deferred
                        }
                    })

                    deferred.await()
                } catch (e: Exception) {
                    billingConnectionDeferred = null
                    throw e
                }
            }
        }
    }

    private fun calculateExpiryDate(purchaseTimeMillis: Long, duration: Int): String {
        val expiryMillis = purchaseTimeMillis + duration * 24 * 60 * 60 * 1000
        return expiryMillis.toString()
    }

    companion object {
        private val TAG = SubscriptionDatasourceImpl::class.java.simpleName
    }
}
