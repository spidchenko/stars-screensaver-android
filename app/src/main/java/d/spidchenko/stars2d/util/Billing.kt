package d.spidchenko.stars2d.util

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.android.billingclient.api.*
import d.spidchenko.stars2d.R
import kotlinx.coroutines.*


private const val TAG = "Billing.LOG_TAG"
private const val PREMIUM_ID = "d.spidchenko.stars2d.inapp.premium"
private const val KEY_PREMIUM_TOKEN = "purchaseToken"

class Billing(val context: Context) {
    private val mainCoroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private var premiumProduct: ProductDetails? = null

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> purchases?.forEach { handlePurchase(it) }
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    if (LoggerConfig.ON) Log.d(TAG, "Already Bought Premium!: ")
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    if (LoggerConfig.ON) Log.d(TAG, "User canceled billing flow")
                }
                else -> {}// Handle any other error codes.
            }
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()


    fun init() =
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        if (LoggerConfig.ON) Log.d(
                            TAG,
                            "onBillingSetupFinished: BillingResponseCode.OK"
                        )
                        MainScope().launch { premiumProduct = queryPremiumProductDetails() }
                    }
                    else -> Log.d(TAG, "onBillingSetupFinished: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                if (LoggerConfig.ON) Log.d(TAG, "onBillingServiceDisconnected")
            }
        })


    suspend fun querySuccessfulPurchases() {
        Log.d(TAG, "queryPurchases onResume() ")
        val params = QueryPurchasesParams
            .newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)

        val purchasesResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(params.build())
        }

        if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            purchasesResult.purchasesList.forEach { handlePurchase(it) }
        }
    }


    fun launchBuyPremiumBillingFlow(activity: Activity) {
        val premium = premiumProduct
        if (premium !== null) {
            launchBillingFlow(activity, premium)
        } else {
            Log.d(TAG, "launchBuyPremiumBillingFlow: Premium product is not initialized!")
        }
    }


    private suspend fun queryPremiumProductDetails(): ProductDetails? {
        val products = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PREMIUM_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        if (LoggerConfig.ON) Log.d(TAG, "queryPurchases: queryProductDetails")

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(queryProductDetailsParams)
        }

        if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            if (LoggerConfig.ON) Log.d(
                TAG,
                "queryProductDetails: $productDetailsResult\n" +
                        "queryProductDetails: got ${productDetailsResult.productDetailsList?.size}\n" +
                        "All products: ${productDetailsResult.productDetailsList.toString()}"
            )
            return productDetailsResult.productDetailsList?.find { it.productId == PREMIUM_ID }
        } else {
            if (LoggerConfig.ON) Log.d(
                TAG,
                "queryPremiumProductDetails: Can't query products." +
                        "Response code: ${productDetailsResult.billingResult.responseCode}"
            )
        }
        return null
    }


    private fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val selectedOfferToken = productDetails.oneTimePurchaseOfferDetails.toString()
        Log.d(TAG, "launchBillingFlow: token:$selectedOfferToken")
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (LoggerConfig.ON) Log.d(TAG, "launchBillingFlow: $billingResult")
    }


    private fun handlePurchase(purchase: Purchase) = mainCoroutineScope.launch {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                Log.d(
                    TAG, "PurchaseUpdateListener found new purchase!\nPurchase: $purchase\n" +
                            "handlePurchase: Token: ${purchase.purchaseToken}"
                )
                processPurchase(purchase)
                acknowledgePurchase(purchase)
            }
            Purchase.PurchaseState.PENDING -> {
                if (LoggerConfig.ON) Log.d(TAG, "handlePurchase: PENDING")
            }
        }
    }


    private fun processPurchase(purchase: Purchase) {
        if (LoggerConfig.ON) Log.d(TAG, "Unlocking new Purchase: $purchase")
        purchase.products.forEach {
            if (it == PREMIUM_ID) {
                unlockPremium(purchase.purchaseToken)
                if (LoggerConfig.ON) Log.d(TAG, "unlockPurchase: PREMIUM BOUGHT!")
            }
        }
    }


    private fun unlockPremium(purchaseToken: String) {
        if (LoggerConfig.ON) Log.d(TAG, "unlockPremium: saving token to shared pref")
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(KEY_PREMIUM_TOKEN, purchaseToken)
            .apply()
        Toast.makeText(context, context.getString(R.string.premium_unlocked), Toast.LENGTH_LONG)
            .show()
    }


    private suspend fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
            val ackPurchaseResult = withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
            }
            if (LoggerConfig.ON) Log.d(TAG, "acknowledgePurchase: $ackPurchaseResult")
        }
    }


    companion object {
        fun checkPremium(context: Context): Boolean =
            PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_PREMIUM_TOKEN, null)
                .isNullOrBlank()
                .not()
    }
}
