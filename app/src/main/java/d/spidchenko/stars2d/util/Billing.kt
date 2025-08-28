package d.spidchenko.stars2d.util

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.android.billingclient.api.*
import d.spidchenko.stars2d.R
import kotlinx.coroutines.*
import androidx.core.content.edit


private const val PREMIUM_ID = "d.spidchenko.stars2d.inapp.premium"
private const val KEY_PREMIUM_TOKEN = "purchaseToken"

class Billing(val context: Context) {
    private val mainCoroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private var premiumProduct: ProductDetails? = null

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases?.forEach { handlePurchase(it) }
            }
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()


    fun init() =
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Logger.log("onBillingSetupFinished: BillingResponseCode.OK")
                    MainScope().launch {
                        premiumProduct = queryPremiumProductDetails()
                        querySuccessfulPurchases()
                    }
                } else {
                    Logger.log("onBillingSetupFinished: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Logger.log("onBillingServiceDisconnected")
            }
        })


    suspend fun querySuccessfulPurchases() {
        Logger.log("queryPurchases onResume() ")
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
            Logger.log("launchBuyPremiumBillingFlow: Premium product is not initialized!")
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

        Logger.log("queryPurchases: queryProductDetails")

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(queryProductDetailsParams)
        }

        if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Logger.log(
                "queryProductDetails: $productDetailsResult\n" +
                        "queryProductDetails: got ${productDetailsResult.productDetailsList?.size}\n" +
                        "All products: ${productDetailsResult.productDetailsList.toString()}"
            )
            return productDetailsResult.productDetailsList?.find { it.productId == PREMIUM_ID }
        } else {
            Logger.log(
                "queryPremiumProductDetails: Can't query products." +
                        "Response code: ${productDetailsResult.billingResult.responseCode}"
            )
        }
        return null
    }


    private fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        Logger.log("launchBillingFlow: $billingResult")
    }


    private fun handlePurchase(purchase: Purchase) = mainCoroutineScope.launch {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            Logger.log(
                "PurchaseUpdateListener found new purchase!\nPurchase: $purchase\n" +
                        "handlePurchase: Token: ${purchase.purchaseToken}"
            )
            processPurchase(purchase)
            acknowledgePurchase(purchase)
        }
    }


    private fun processPurchase(purchase: Purchase) =
        purchase.products.forEach {
            if (it == PREMIUM_ID) {
                unlockPremium(purchase.purchaseToken)
                Logger.log("unlockPurchase: PREMIUM BOUGHT!")
            }
        }


    private fun unlockPremium(purchaseToken: String) {
        val prefManager = PreferenceManager.getDefaultSharedPreferences(context)
        if (checkPremium(context)) {
            Logger.log("Premium feature has been unlocked earlier")
        } else {
            Logger.log("unlockPremium: saving token to shared pref")
            prefManager.edit {
                putString(KEY_PREMIUM_TOKEN, purchaseToken)
            }

            Toast.makeText(
                context,
                context.getString(R.string.premium_unlocked_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private suspend fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
            val ackPurchaseResult = withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
            }
            Logger.log("acknowledgePurchase: $ackPurchaseResult")
        }
    }


    companion object {
        fun checkPremium(context: Context): Boolean =
            PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_PREMIUM_TOKEN, null) != null
    }
}