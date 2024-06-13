/*
 * Copyright (C) 2024 Goodwy
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

package com.goodwy.keyboard.app.settings.purchase

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.florisPreferenceModel
import com.goodwy.keyboard.lib.android.launchUrl
import com.goodwy.keyboard.lib.android.launchUrlNew
import com.goodwy.keyboard.lib.compose.FlorisIconButton
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes
import com.sdkit.paylib.paylibsdk.client.domain.DefaultPaylibClientInfoProvider.packageName
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.LocalDefaultDialogPrefStrings
import dev.patrickgold.jetpref.material.ui.JetPrefAlertDialog
import com.goodwy.keyboard.strings.R as StringsR

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PurchaseScreen() = FlorisScreen {
    title = stringRes(StringsR.string.tipping_jar_title)

    val context = LocalContext.current

    val prefs by florisPreferenceModel()
    val isPro by prefs.purchase.isPro.observeAsState()
    val isProSubs by prefs.purchase.isProSubs.observeAsState()
    val isProRustore by prefs.purchase.isProRustore.observeAsState()
    val isProApp = isPro || isProSubs || isProRustore

    val isPlayStoreInstalled by prefs.purchase.isPlayStoreInstalled.observeAsState()
    val isRuStoreInstalled by prefs.purchase.isRuStoreInstalled.observeAsState()
    val useGooglePlay by prefs.purchase.useGooglePlay.observeAsState()

    val products1 by prefs.purchase.products1.observeAsState()
    val products2 by prefs.purchase.products2.observeAsState()
    val products3 by prefs.purchase.products3.observeAsState()
    val products4 by prefs.purchase.products4.observeAsState()
    val products5 by prefs.purchase.products5.observeAsState()
    val products6 by prefs.purchase.products6.observeAsState()
    val products7 by prefs.purchase.products7.observeAsState()
    val products8 by prefs.purchase.products8.observeAsState()
    val products9 by prefs.purchase.products9.observeAsState()

    val productsRustore1 by prefs.purchase.productsRustore1.observeAsState()
    val productsRustore2 by prefs.purchase.productsRustore2.observeAsState()
    val productsRustore3 by prefs.purchase.productsRustore3.observeAsState()
    val productsRustore4 by prefs.purchase.productsRustore4.observeAsState()
    val productsRustore5 by prefs.purchase.productsRustore5.observeAsState()
    val productsRustore6 by prefs.purchase.productsRustore6.observeAsState()
    val productsRustore7 by prefs.purchase.productsRustore7.observeAsState()
    val productsRustore8 by prefs.purchase.productsRustore8.observeAsState()
    val productsRustore9 by prefs.purchase.productsRustore9.observeAsState()

    val isDialogOpen = remember { mutableStateOf(false) }
    val purchaseErrorRustore by prefs.purchase.purchaseErrorRustore.observeAsState()

    if (isPlayStoreInstalled || isRuStoreInstalled) {
        actions {
            if (isPlayStoreInstalled && isRuStoreInstalled) {
                if (useGooglePlay) {
                    IconButton(onClick = { prefs.purchase.useGooglePlay.set(false) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google_play),
                            contentDescription = "Google Play",
                        )
                    }
                } else {
                    IconButton(onClick = { prefs.purchase.useGooglePlay.set(true) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_rustore),
                            contentDescription = "RuStore",
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
            }
            val isRustore = !useGooglePlay && isRuStoreInstalled
            val textButton = stringRes(StringsR.string.restore_purchase)
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            val store = if (isRustore) "restore_purchase_rustore" else "restore_purchase_play_store"
                            prefs.purchase.startPurchase.set(store) },
                        onLongClick = { Toast.makeText(
                            context,
                            textButton,
                            Toast.LENGTH_SHORT,
                        ).show() },
                        indication = rememberRipple(bounded = false, radius = 20.dp),
                        interactionSource = remember { MutableInteractionSource() },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_restore_purchase),
                    contentDescription = textButton,
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
//            IconButton(
//                onClick = {
//                val store = if (isRustore) "restore_purchase_rustore" else "restore_purchase_play_store"
//                prefs.purchase.startPurchase.set(store) }
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_restore_purchase),
//                    contentDescription = stringRes(StringsR.string.restore_purchase),
//                )
//            }

            var expanded by remember { mutableStateOf(false) }
            FlorisIconButton(
                onClick = { expanded = !expanded },
                icon = Icons.Default.MoreVert,
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    onClick = {
                        val url = if (isRustore) "rustore://profile/subscriptions" else "https://play.google.com/store/account/subscriptions"
                        context.launchUrlNew(url)
                        expanded = false
                    },
                    text = { Text(text = stringRes(StringsR.string.billing_subscriptions)) },
                )
            }
        }
    }

    content {

        val failed = "???"
        val prices1 = if (useGooglePlay) { if (products1.valid) products1.price else failed } else productsRustore1.price
        val prices2 = if (useGooglePlay) { if (products2.valid) products2.price else failed } else productsRustore2.price
        val prices3 = if (useGooglePlay) { if (products3.valid) products3.price else failed } else productsRustore3.price
        val prices4 = if (useGooglePlay) { if (products4.valid) stringResource(id = StringsR.string.per_month, products4.price) else failed }
                                else stringResource(id = StringsR.string.per_month, productsRustore4.price)
        val prices5 = if (useGooglePlay) { if (products5.valid) stringResource(id = StringsR.string.per_month, products5.price) else failed }
                                else stringResource(id = StringsR.string.per_month, productsRustore5.price)
        val prices6 = if (useGooglePlay) { if (products6.valid) stringResource(id = StringsR.string.per_month, products6.price) else failed }
                                else stringResource(id = StringsR.string.per_month, productsRustore6.price)
        val prices7 = if (useGooglePlay) { if (products7.valid) stringResource(id = StringsR.string.per_year, products7.price) else failed }
                                else stringResource(id = StringsR.string.per_year, productsRustore7.price)
        val prices8 = if (useGooglePlay) { if (products8.valid) stringResource(id = StringsR.string.per_year, products8.price) else failed }
                                else stringResource(id = StringsR.string.per_year, productsRustore8.price)
        val prices9 = if (useGooglePlay) { if (products9.valid) stringResource(id = StringsR.string.per_year, products9.price) else failed }
                                else stringResource(id = StringsR.string.per_year, productsRustore9.price)

        val isPurchased1 = if (useGooglePlay) products1.purchased else productsRustore1.purchased
        val isPurchased2 = if (useGooglePlay) products2.purchased else productsRustore2.purchased
        val isPurchased3 = if (useGooglePlay) products3.purchased else productsRustore3.purchased
        val isPurchased4 = if (useGooglePlay) products4.purchased else productsRustore4.purchased
        val isPurchased5 = if (useGooglePlay) products5.purchased else productsRustore5.purchased
        val isPurchased6 = if (useGooglePlay) products6.purchased else productsRustore6.purchased
        val isPurchased7 = if (useGooglePlay) products7.purchased else productsRustore7.purchased
        val isPurchased8 = if (useGooglePlay) products8.purchased else productsRustore8.purchased
        val isPurchased9 = if (useGooglePlay) products9.purchased else productsRustore9.purchased

        val isEnabled1 = if (useGooglePlay) products1.valid else productsRustore1.valid && !isPurchased1
        val isEnabled2 = if (useGooglePlay) products2.valid else productsRustore2.valid && !isPurchased2
        val isEnabled3 = if (useGooglePlay) products3.valid else productsRustore3.valid && !isPurchased3
        val isEnabled4 = if (useGooglePlay) products4.valid else productsRustore4.valid && !isPurchased4
        val isEnabled5 = if (useGooglePlay) products5.valid else productsRustore5.valid && !isPurchased5
        val isEnabled6 = if (useGooglePlay) products6.valid else productsRustore6.valid && !isPurchased6
        val isEnabled7 = if (useGooglePlay) products7.valid else productsRustore7.valid && !isPurchased7
        val isEnabled8 = if (useGooglePlay) products8.valid else productsRustore8.valid && !isPurchased8
        val isEnabled9 = if (useGooglePlay) products9.valid else productsRustore9.valid && !isPurchased9

        val product1 = if (useGooglePlay) products1.id else productsRustore1.id
        val product2 = if (useGooglePlay) products2.id else productsRustore2.id
        val product3 = if (useGooglePlay) products3.id else productsRustore3.id
        val product4 = if (useGooglePlay) products4.id else productsRustore4.id
        val product5 = if (useGooglePlay) products5.id else productsRustore5.id
        val product6 = if (useGooglePlay) products6.id else productsRustore6.id
        val product7 = if (useGooglePlay) products7.id else productsRustore7.id
        val product8 = if (useGooglePlay) products8.id else productsRustore8.id
        val product9 = if (useGooglePlay) products9.id else productsRustore9.id

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(108.dp)
                        .padding(12.dp),
                    painter = painterResource(id = R.drawable.ic_plus_support),
                    contentDescription = stringResource(id = StringsR.string.action_support_project),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
            Spacer(modifier = Modifier.size(16.dp))

            if (isPlayStoreInstalled || isRuStoreInstalled) {
                Text(
                    text = stringResource(StringsR.string.action_support_project),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = stringResource(com.goodwy.keyboard.strings.R.string.any_support_unlock_all),
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    color = LocalContentColor.current.copy(alpha = 0.5F),
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row(
                    modifier = Modifier
                        .wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Spacer(modifier = Modifier.size(16.dp))
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 208.dp)
                                //.aspectRatio(1f, true)
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier
                                    .height(52.dp)
                                    .padding(horizontal = 2.dp)
                                    .wrapContentHeight(),
                                text = stringResource(StringsR.string.tip_kind),
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 3,
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .alpha(0.6f),
                                enabled = isEnabled1,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = { prefs.purchase.startPurchase.set(product1) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isPurchased1) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                    )
                                } else {
                                    Text(
                                        text = prices1,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .alpha(0.7f),
                                enabled = isEnabled4,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = { prefs.purchase.startPurchase.set(product4) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isPurchased4) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                    )
                                } else {
                                    Text(
                                        text = prices4,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .alpha(0.8f),
                                enabled = isEnabled7,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = { prefs.purchase.startPurchase.set(product7) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isPurchased7) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                    )
                                } else {
                                    Text(
                                        text = prices7,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 208.dp)
                                //.aspectRatio(1f, true)
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier
                                    .height(52.dp)
                                    .padding(horizontal = 2.dp)
                                    .wrapContentHeight(),
                                text = stringResource(StringsR.string.tip_excellent),
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 3,
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .alpha(0.7f),
                                enabled = isEnabled2,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = { prefs.purchase.startPurchase.set(product2) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isPurchased2) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                    )
                                } else {
                                    Text(
                                        text = prices2,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .alpha(0.8f),
                                enabled = isEnabled5,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = { prefs.purchase.startPurchase.set(product5) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isPurchased5) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                    )
                                } else {
                                    Text(
                                        text = prices5,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .alpha(0.9f),
                                enabled = isEnabled8,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = { prefs.purchase.startPurchase.set(product8) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isPurchased8) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                    )
                                } else {
                                    Text(
                                        text = prices8,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentSize()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 208.dp)
                                //.aspectRatio(1f, true)
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier
                                    .height(52.dp)
                                    .padding(horizontal = 2.dp)
                                    .wrapContentHeight(),
                                text = stringResource(StringsR.string.tip_incredible),
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 3,
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .alpha(0.8f),
                                enabled = isEnabled3,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = { prefs.purchase.startPurchase.set(product3) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isPurchased3) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                    )
                                } else {
                                    Text(
                                        text = prices3,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .alpha(0.9f),
                                enabled = isEnabled6,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = { prefs.purchase.startPurchase.set(product6) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isPurchased6) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                    )
                                } else {
                                    Text(
                                        text = prices6,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .alpha(1f),
                                enabled = isEnabled9,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                onClick = { prefs.purchase.startPurchase.set(product9) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isPurchased9) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                    )
                                } else {
                                    Text(
                                        text = prices9,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.size(24.dp))
                if (!isProApp) {
                    // Dark Theme
                    ListItem(
//                        modifier = Modifier.clickable {
//                            listener.themeChanged()
//                        },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                        leadingContent = {
                            Icon(
                                modifier = Modifier.size(62.dp),
                                painter = painterResource(id = R.drawable.ic_night_mode),
                                contentDescription = stringResource(id = R.string.pref__theme__night),
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        headlineContent = {
                            Column {
                                Text(
                                    text = stringResource(R.string.pref__theme__night),
                                    fontSize = 18.sp,
                                )
                                Spacer(modifier = Modifier.size(2.dp))
                                Text(
                                    text = stringResource(StringsR.string.night_theme_summary),
                                    lineHeight = 16.sp,
                                    color = LocalContentColor.current.copy(alpha = 0.5F),
                                )
                            }
                        },
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    // Colors
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                        leadingContent = {
                            Icon(
                                modifier = Modifier.size(62.dp),
                                painter = painterResource(id = R.drawable.ic_palette),
                                contentDescription = stringResource(id = R.string.settings__theme_manager__title_manage),
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        headlineContent = {
                            Column {
                                Text(
                                    text = stringResource(R.string.settings__theme_manager__title_manage),
                                    fontSize = 18.sp,
                                )
                                Spacer(modifier = Modifier.size(2.dp))
                                Text(
                                    text = stringResource(StringsR.string.theme_manager_summary),
                                    lineHeight = 16.sp,
                                    color = LocalContentColor.current.copy(alpha = 0.5F),
                                )
                            }
                        },
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                }
                // Support us
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                    leadingContent = {
                        Icon(
                            modifier = Modifier
                                .size(62.dp)
                                .padding(5.dp),
                            painter = painterResource(id = R.drawable.ic_plus_round),
                            contentDescription = stringResource(id = StringsR.string.plus_title),
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    },
                    headlineContent = {
                        Column {
                            Text(
                                text = stringResource(StringsR.string.plus_title),
                                fontSize = 18.sp,
                            )
                            Spacer(modifier = Modifier.size(2.dp))
                            Text(
                                text = stringResource(StringsR.string.plus_summary),
                                lineHeight = 16.sp,
                                color = LocalContentColor.current.copy(alpha = 0.5F),
                            )
                        }
                    },
                )
                // Participants
                Spacer(modifier = Modifier.size(24.dp))
                Image(
                    modifier = Modifier.size(82.dp),
                    painter = painterResource(id = R.drawable.logo_goodwy),
                    contentDescription = null
                )

                when(purchaseErrorRustore) {
                    "Application signature not correct" -> isDialogOpen.value = true
                    "RuStore User Not Authorized" -> isDialogOpen.value = true
                    "" -> {}
                    else -> {
                        Toast.makeText(
                            context,
                            purchaseErrorRustore,
                            Toast.LENGTH_SHORT,
                        ).show()
                        prefs.purchase.purchaseErrorRustore.set("")
                    }
                }
                if (isDialogOpen.value) {
                    val signatureError = purchaseErrorRustore == "Application signature not correct"
                    val subtitle = if (signatureError) stringRes(StringsR.string.billing_error_application_signature_not_correct)
                                            else stringRes(ru.rustore.sdk.billingclient.R.string.ru_store_user_unauthorized_title)
                    JetPrefAlertDialog(
                        title = stringRes(R.string.error__title),
                        confirmLabel = if (signatureError) stringRes(id = ru.rustore.sdk.billingclient.R.string.ru_store_not_installed_button)
                                        else stringRes(id = ru.rustore.sdk.billingclient.R.string.ru_store_user_unauthorized_button),
                        onConfirm = {
                            if (signatureError) context.launchUrlNew("https://apps.rustore.ru/app/$packageName")
                            else context.launchUrlNew("rustore://auth")
                            isDialogOpen.value = false
                            prefs.purchase.purchaseErrorRustore.set("")
                        },
                        dismissLabel = LocalDefaultDialogPrefStrings.current.dismissLabel,
                        onDismiss = {
                            isDialogOpen.value = false
                            prefs.purchase.purchaseErrorRustore.set("")
                        },
//                        neutralLabel = LocalDefaultDialogPrefStrings.current.neutralLabel,
//                        onNeutral = {
//                            isDialogOpen.value = false
//                            prefs.purchase.purchaseErrorRustore.set("")
//                                    },
                        trailingIconTitle = {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = null,
                            )
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                text = subtitle,
                            )
//                            if (!signatureError) {
//                                Spacer(modifier = Modifier.size(12.dp))
//                                Text(
//                                    modifier = Modifier.padding(horizontal = 16.dp),
//                                    text = purchaseErrorRustore,
//                                    fontSize = 14.sp,
//                                    lineHeight = 14.sp,
//                                    color = LocalContentColor.current.copy(alpha = 0.5F),
//                                )
//                            }
                        }
                    }
                }
            } else {
                // no store
                Column(
                    modifier = Modifier
                        .wrapContentWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = stringResource(StringsR.string.donate_text),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        //modifier = Modifier.height(28.dp),
                        onClick = { context.launchUrl("https://sites.google.com/view/goodwy/support-project") },
                        //shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(stringResource(StringsR.string.action_support_project))
                    }
                    Spacer(modifier = Modifier.size(56.dp))
                    Text(stringResource(StringsR.string.unlock))
                    Spacer(modifier = Modifier.size(8.dp))
                    Switch(
                        modifier = Modifier.scale(2f),
                        checked = isPro,
                        onCheckedChange = { prefs.purchase.isPro.set(!isPro) },
                    )
                    Spacer(modifier = Modifier.size(56.dp))
                }
            }
            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}
