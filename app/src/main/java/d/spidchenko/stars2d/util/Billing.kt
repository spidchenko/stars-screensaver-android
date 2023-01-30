package d.spidchenko.stars2d.util

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.android.billingclient.api.*
import d.spidchenko.stars2d.R
import kotlinx.coroutines.*


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
                    Logger.Log( "Already Bought Premium!: ")
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    Logger.Log("User canceled billing flow")
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
                        Logger.Log(
                            "onBillingSetupFinished: BillingResponseCode.OK"
                        )
                        MainScope().launch { premiumProduct = queryPremiumProductDetails() }
                    }
                    else ->Logger.Log("onBillingSetupFinished: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Logger.Log("onBillingServiceDisconnected")
            }
        })


    suspend fun querySuccessfulPurchases() {
        Logger.Log( "queryPurchases onResume() ")
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
            Logger.Log( "launchBuyPremiumBillingFlow: Premium product is not initialized!")
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

        Logger.Log("queryPurchases: queryProductDetails")

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(queryProductDetailsParams)
        }

        if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Logger.Log(
                "queryProductDetails: $productDetailsResult\n" +
                        "queryProductDetails: got ${productDetailsResult.productDetailsList?.size}\n" +
                        "All products: ${productDetailsResult.productDetailsList.toString()}"
            )
            return productDetailsResult.productDetailsList?.find { it.productId == PREMIUM_ID }
        } else {
            Logger.Log(
                "queryPremiumProductDetails: Can't query products." +
                        "Response code: ${productDetailsResult.billingResult.responseCode}"
            )
        }
        return null
    }


    private fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val selectedOfferToken = productDetails.oneTimePurchaseOfferDetails.toString()
        Logger.Log("launchBillingFlow: token:$selectedOfferToken")
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        Logger.Log("launchBillingFlow: $billingResult")
    }


    private fun handlePurchase(purchase: Purchase) = mainCoroutineScope.launch {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                Logger.Log("PurchaseUpdateListener found new purchase!\nPurchase: $purchase\n" +
                            "handlePurchase: Token: ${purchase.purchaseToken}"
                )
                processPurchase(purchase)
                acknowledgePurchase(purchase)
            }
            Purchase.PurchaseState.PENDING -> {
                Logger.Log("handlePurchase: PENDING")
            }
        }
    }


    private fun processPurchase(purchase: Purchase) {
        Logger.Log("Unlocking new Purchase: $purchase")
        purchase.products.forEach {
            if (it == PREMIUM_ID) {
                unlockPremium(purchase.purchaseToken)
                Logger.Log( "unlockPurchase: PREMIUM BOUGHT!")
            }
        }
    }


    private fun unlockPremium(purchaseToken: String) {
        Logger.Log("unlockPremium: saving token to shared pref")
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(KEY_PREMIUM_TOKEN, purchaseToken)
            .apply()
        Toast.makeText(context, context.getString(R.string.premium_unlocked_message), Toast.LENGTH_LONG)
            .show()
    }


    private suspend fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
            val ackPurchaseResult = withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
            }
            Logger.Log("acknowledgePurchase: $ackPurchaseResult")
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
