/*
 * Copyright (C) 2021 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goodwy.keyboard.app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.goodwy.keyboard.BuildConfig
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.apptheme.FlorisAppTheme
import com.goodwy.keyboard.app.settings.purchase.Product
import com.goodwy.keyboard.app.setup.NotificationPermissionState
import com.goodwy.keyboard.lib.FlorisLocale
import com.goodwy.keyboard.lib.android.AndroidVersion
import com.goodwy.keyboard.lib.android.hideAppIcon
import com.goodwy.keyboard.lib.android.setLocale
import com.goodwy.keyboard.lib.android.showAppIcon
import com.goodwy.keyboard.lib.android.showShortToast
import com.goodwy.keyboard.lib.compose.LocalPreviewFieldController
import com.goodwy.keyboard.lib.compose.PreviewKeyboardField
import com.goodwy.keyboard.lib.compose.ProvideLocalizedResources
import com.goodwy.keyboard.lib.compose.rememberPreviewFieldController
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.keyboard.lib.util.AppVersionUtils
import dev.patrickgold.jetpref.datastore.model.PreferenceData
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.ProvideDefaultDialogPrefStrings
import kotlinx.coroutines.launch
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.product.Product as RuProduct
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult
import java.util.ArrayList

enum class AppTheme(val id: String) {
    AUTO("auto"),
    AUTO_AMOLED("auto_amoled"),
    LIGHT("light"),
    DARK("dark"),
    AMOLED_DARK("amoled_dark");
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("LocalNavController not initialized")
}

class FlorisAppActivity : ComponentActivity() {
    private val prefs by florisPreferenceModel()
    private var appTheme by mutableStateOf(AppTheme.AUTO)
    private var showAppIcon = true
    private var resourcesContext by mutableStateOf(this as Context)

    private val purchaseHelper = PurchaseHelper(this)
    private val ruStoreHelper = RuStoreHelper(this)
    private val billingRuStoreClient: RuStoreBillingClient = RuStoreModule.provideRuStoreBillingClient()
    private var ruStoreIsConnected = false
    private val productsRuStore: MutableList<RuProduct> = mutableListOf()
    private val productList: ArrayList<String> = arrayListOf(
        BuildConfig.PRODUCT_ID_X1, BuildConfig.PRODUCT_ID_X2, BuildConfig.PRODUCT_ID_X3,
        BuildConfig.SUBSCRIPTION_ID_X1, BuildConfig.SUBSCRIPTION_ID_X2, BuildConfig.SUBSCRIPTION_ID_X3,
        BuildConfig.SUBSCRIPTION_YEAR_ID_X1, BuildConfig.SUBSCRIPTION_YEAR_ID_X2, BuildConfig.SUBSCRIPTION_YEAR_ID_X3 )
    private val iapList: ArrayList<String> = arrayListOf(BuildConfig.PRODUCT_ID_X1, BuildConfig.PRODUCT_ID_X2, BuildConfig.PRODUCT_ID_X3)
    private val subList: ArrayList<String> = arrayListOf(BuildConfig.SUBSCRIPTION_ID_X1, BuildConfig.SUBSCRIPTION_ID_X2, BuildConfig.SUBSCRIPTION_ID_X3,
        BuildConfig.SUBSCRIPTION_YEAR_ID_X1, BuildConfig.SUBSCRIPTION_YEAR_ID_X2, BuildConfig.SUBSCRIPTION_YEAR_ID_X3)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen should be installed before calling super.onCreate()
        installSplashScreen().apply {
            setKeepOnScreenCondition { !prefs.datastoreReadyStatus.get() }
        }
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        prefs.advanced.settingsTheme.observe(this) {
            appTheme = it
        }
        prefs.advanced.settingsLanguage.observe(this) {
            val config = Configuration(resources.configuration)
            config.setLocale(if (it == "auto") FlorisLocale.default() else FlorisLocale.fromTag(it))
            resourcesContext = createConfigurationContext(config)
        }
        if (AndroidVersion.ATMOST_API28_P) {
            prefs.advanced.showAppIcon.observe(this) {
                showAppIcon = it
            }
        }

        //Check if android 13+ is running and the NotificationPermission is not set
        if (AndroidVersion.ATLEAST_API33_T &&
            prefs.internal.notificationPermissionState.get() == NotificationPermissionState.NOT_SET
        ) {
            // update pref value to show the setup screen again again
            prefs.internal.isImeSetUp.set(false)
        }

        // We defer the setContent call until the datastore model is loaded, until then the splash screen stays drawn
        prefs.datastoreReadyStatus.observe(this) { isModelLoaded ->
            if (!isModelLoaded) return@observe
            AppVersionUtils.updateVersionOnInstallAndLastUse(this, prefs)
            setContent {
                ProvideLocalizedResources(resourcesContext) {
                    FlorisAppTheme(theme = appTheme, isMaterialYouAware = prefs.advanced.useMaterialYou.observeAsState().value) {
                        Surface(color = MaterialTheme.colorScheme.background) {
                            //SystemUiApp()
                            AppContent()
                        }
                    }
                }
            }
        }

        if (isRuStoreInstalled()) {
            if (savedInstanceState == null) {
                billingRuStoreClient.onNewIntent(intent)
            }
        }

        prefs.purchase.startPurchase.set("")
        prefs.purchase.startPurchase.observe(this) { id ->
            when {
                id == "restore_purchase_play_store" -> {
                    arrayOf(
                        prefs.purchase.products1, prefs.purchase.products2, prefs.purchase.products3,
                        prefs.purchase.products4, prefs.purchase.products5, prefs.purchase.products6,
                        prefs.purchase.products7, prefs.purchase.products8, prefs.purchase.products9
                    ).forEach {
                        it.reset()
                    }
                    initPlayStore()
                    prefs.purchase.startPurchase.set("")
                }
                id == "restore_purchase_rustore" -> {
                    arrayOf(
                        prefs.purchase.productsRustore1, prefs.purchase.productsRustore2, prefs.purchase.productsRustore3,
                        prefs.purchase.productsRustore4, prefs.purchase.productsRustore5, prefs.purchase.productsRustore6,
                        prefs.purchase.productsRustore7, prefs.purchase.productsRustore8, prefs.purchase.productsRustore9
                    ).forEach {
                        it.reset()
                    }
                    initRuStore()
                    prefs.purchase.startPurchase.set("")
                }
                productList.contains(id) -> {
                    if (!prefs.purchase.useGooglePlay.get() && isRuStoreInstalled()) {
                        val product = productsRuStore.firstOrNull { it.productId == id }
                        if (product != null) ruStoreHelper.purchaseProduct(product)
                    } else {
                        if (iapList.contains(id)) purchaseHelper.getDonation(id)
                        else if (subList.contains(id)) purchaseHelper.getSubscription(id)
                    }
                    prefs.purchase.startPurchase.set("")
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // App icon visibility control was restricted in Android 10.
        // See https://developer.android.com/reference/android/content/pm/LauncherApps#getActivityList(java.lang.String,%20android.os.UserHandle)
        if (AndroidVersion.ATMOST_API28_P) {
            if (showAppIcon) {
                this.showAppIcon()
            } else {
                this.hideAppIcon()
            }
        }
    }

    @Composable
    private fun AppContent() {
        val navController = rememberNavController()
        val previewFieldController = rememberPreviewFieldController()

        val isImeSetUp by prefs.internal.isImeSetUp.observeAsState()

        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalPreviewFieldController provides previewFieldController,
        ) {
            ProvideDefaultDialogPrefStrings(
                confirmLabel = stringRes(R.string.action__ok),
                dismissLabel = stringRes(R.string.action__cancel),
                neutralLabel = stringRes(R.string.action__default),
            ) {
                Column(
                    modifier = Modifier
                        //.statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding(),
                ) {
                    Routes.AppNavHost(
                        modifier = Modifier.weight(1.0f),
                        navController = navController,
                        startDestination = if (isImeSetUp) Routes.Settings.Home else Routes.Setup.Screen,
                    )
                    PreviewKeyboardField(previewFieldController)
                }
            }
        }

        SideEffect {
            navController.setOnBackPressedDispatcher(this.onBackPressedDispatcher)
        }
    }

    //Goodwy
    override fun onResume() {
        super.onResume()

        prefs.purchase.purchaseErrorRustore.set("")
        prefs.purchase.startPurchase.set("")

        val isPlayStoreInstalled = isPlayStoreInstalled()
        val isRuStoreInstalled = isRuStoreInstalled()
        prefs.purchase.isPlayStoreInstalled.set(isPlayStoreInstalled)
        prefs.purchase.isRuStoreInstalled.set(isRuStoreInstalled)
        if (!isPlayStoreInstalled) prefs.purchase.useGooglePlay.set(false)

        //Billing
        if (isPlayStoreInstalled) initPlayStore()
        if (isRuStoreInstalled) initRuStore()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (isRuStoreInstalled()) {
            billingRuStoreClient.onNewIntent(intent)
        }
    }

    private fun initPlayStore() {
        purchaseHelper.initBillingClient()
        purchaseHelper.retrieveDonation(iapList, subList)

        purchaseHelper.iapSkuDetailsInitialized.observe(this) {
            if (it) {
                arrayOf(
                    prefs.purchase.products1, prefs.purchase.products2, prefs.purchase.products3
                ).forEach { pref ->
                    val prefGetId = pref.get().id
                    val price = purchaseHelper.getPriceDonation(prefGetId)
                    val resultPrice =
                        if (price != "???") price.replace(".00", "", true) else price
                    pref.set(Product(prefGetId, resultPrice, valid = true))
                }
            }
        }
        purchaseHelper.subSkuDetailsInitialized.observe(this) {
            if (it) {
                arrayOf(
                    prefs.purchase.products4, prefs.purchase.products5, prefs.purchase.products6,
                    prefs.purchase.products7, prefs.purchase.products8, prefs.purchase.products9
                ).forEach { pref ->
                    val prefGetId = pref.get().id
                    val price = purchaseHelper.getPriceSubscription(prefGetId)
                    val resultPrice =
                        if (price != "???") price.replace(".00", "", true) else price
                    pref.set(Product(prefGetId, resultPrice, valid = true))
                }
            }
        }

        purchaseHelper.isIapPurchased.observe(this) {
            when (it) {
                is Tipping.Succeeded -> {
                    prefs.purchase.isPro.set(true)
                }
                is Tipping.NoTips -> {
                    prefs.purchase.isPro.set(false)
                }
                is Tipping.FailedToLoad -> {
                }
            }
        }

        purchaseHelper.isSupPurchased.observe(this) {
            when (it) {
                is Tipping.Succeeded -> {
                    prefs.purchase.isProSubs.set(true)
                }
                is Tipping.NoTips -> {
                    prefs.purchase.isProSubs.set(false)
                }
                is Tipping.FailedToLoad -> {
                }
            }
        }

        updateCheckedPurchases()
    }

    private fun updateCheckedPurchases() {
        purchaseHelper.isIapPurchasedList.observe(this) { purchasedList ->
            arrayOf(
                prefs.purchase.products1, prefs.purchase.products2, prefs.purchase.products3
            ).forEach { pref ->
                val saveProduct = pref.get()
                val purchased = purchasedList.firstOrNull {  it == saveProduct.id  }
                pref.set(Product(saveProduct.id, saveProduct.price, purchased != null, true))
            }
        }

        purchaseHelper.isSupPurchasedList.observe(this) { purchasedSubsList ->
            arrayOf(
                prefs.purchase.products4, prefs.purchase.products5, prefs.purchase.products6,
                prefs.purchase.products7, prefs.purchase.products8, prefs.purchase.products9
            ).forEach { pref ->
                val saveProduct = pref.get()
                val purchased = purchasedSubsList.firstOrNull {  it == saveProduct.id  }
                pref.set(Product(saveProduct.id, saveProduct.price, purchased != null, true))
            }
        }
    }

    private fun initRuStore() {
        ruStoreHelper.checkPurchasesAvailability(this)

        lifecycleScope.launch {
            ruStoreHelper.eventStart
                .flowWithLifecycle(lifecycle)
                .collect { event ->
                    handleEventStart(event)
                }
        }

        lifecycleScope.launch {
            ruStoreHelper.stateBilling
                .flowWithLifecycle(lifecycle)
                .collect { state ->
                    if (!state.isLoading) {
                        productsRuStore.clear()
                        productsRuStore.addAll(state.products)

                        //price update
                        arrayOf(
                            prefs.purchase.productsRustore1, prefs.purchase.productsRustore2, prefs.purchase.productsRustore3,
                            prefs.purchase.productsRustore4, prefs.purchase.productsRustore5, prefs.purchase.productsRustore6,
                            prefs.purchase.productsRustore7, prefs.purchase.productsRustore8, prefs.purchase.productsRustore9
                        ).forEach { pref ->
                            val prefGetId = pref.get().id
                            val product = state.products.firstOrNull {  it.productId == prefGetId  }
                            val price = product?.priceLabel ?: "???"
                            val resultPrice = price.replace(".00","",true)
                            pref.set(Product(prefGetId, resultPrice))
                        }
                    }
                }
        }
//      lifecycleScope.launch {
//        ruStoreHelper.eventBilling
//          .flowWithLifecycle(lifecycle)
//          .collect { event ->
//            handleEventBilling(event)
//          }
//      }

        updateCheckedPurchasesRuStore()
    }

    private fun updateCheckedPurchasesRuStore() {
        lifecycleScope.launch {
            ruStoreHelper.statePurchased
                .flowWithLifecycle(lifecycle)
                .collect { state ->
                    if (!state.isLoading && ruStoreIsConnected) {
                        //update pro version
                        val isProRu = state.purchases.isNotEmpty()
                        prefs.purchase.isProRustore.set(isProRu)
                        arrayOf(
                            prefs.purchase.productsRustore1, prefs.purchase.productsRustore2, prefs.purchase.productsRustore3,
                            prefs.purchase.productsRustore4, prefs.purchase.productsRustore5, prefs.purchase.productsRustore6,
                            prefs.purchase.productsRustore7, prefs.purchase.productsRustore8, prefs.purchase.productsRustore9
                        ).forEach { pref ->
                            val saveProduct = pref.get()
                            val purchased = state.purchases.firstOrNull {  it.productId == saveProduct.id  }
                            pref.set(Product(saveProduct.id, saveProduct.price, purchased != null, true))
                        }
                    }
                }
        }
    }

    private fun handleEventStart(event: StartPurchasesEvent) {
        when (event) {
            is StartPurchasesEvent.PurchasesAvailability -> {
                when (event.availability) {
                    is FeatureAvailabilityResult.Available -> {
                        //Process purchases available
                        updateProducts()
                        ruStoreIsConnected = true
                    }

                    is FeatureAvailabilityResult.Unavailable -> {
                        val error = event.availability.cause.message ?: "Process purchases unavailable"
                        prefs.purchase.purchaseErrorRustore.set(error)
                    }

                    else -> {}
                }
            }

            is StartPurchasesEvent.Error -> {
                //toast(event.throwable.message ?: "Process unknown error", Toast.LENGTH_LONG)
            }
        }
    }

    private fun updateProducts() {
        ruStoreHelper.getProducts(productList)
    }

    private fun isPlayStoreInstalled(): Boolean {
        return isPackageInstalled("com.android.vending")
            || isPackageInstalled("com.google.market")
    }

    private fun isRuStoreInstalled(): Boolean {
        return isPackageInstalled("ru.vk.store")
    }

    private fun isPackageInstalled(packageName: String?): Boolean {
        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName!!) ?: return false
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.isNotEmpty()
    }
}
