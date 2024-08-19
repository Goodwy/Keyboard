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

package com.goodwy.keyboard.app.settings.about


import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodwy.keyboard.BuildConfig
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.clipboardManager
import com.goodwy.keyboard.lib.util.launchUrlNew
import com.goodwy.keyboard.lib.compose.FlorisCanvasIcon
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.lib.android.stringRes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AboutScreen() = FlorisScreen {
    title = stringRes(R.string.about__title)

    val navController = LocalNavController.current
    val context = LocalContext.current
    val clipboardManager by context.clipboardManager()

    val appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

    content {
        var count: Int = 0
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    role = Role.Button,
                    onClick = {
                        ++count
                        if (count == 9) {
                            Toast.makeText(context, "9", Toast.LENGTH_SHORT).show()
                        }
                        if (count == 12) {
                            count = 0
                            val showDevtools = prefs.devtools.showDevtools.get()
                            prefs.devtools.showDevtools.set(!showDevtools)
                            val text = if (showDevtools) "Developer mode Off" else "Developer mode On"
                            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
                        }
                    },
                    onLongClick = {
                        try {
                            clipboardManager.addNewPlaintext(appVersion)
                            Toast.makeText(context, R.string.about__version_copied__title, Toast.LENGTH_SHORT).show()
                        } catch (e: Throwable) {
                            Toast.makeText(
                                context,
                                context.stringRes(R.string.about__version_copied__error, "error_message" to e.message),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                )
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            FlorisCanvasIcon(
                modifier = Modifier.requiredSize(64.dp),
                iconId = R.mipmap.floris_app_icon,
                contentDescription = "FlorisBoard app icon",
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringRes(R.string.floris_app_name),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = appVersion,
                fontSize = 15.sp,
            )
            Spacer(modifier = Modifier.size(20.dp))
        }
        Text(
            modifier = Modifier.padding(horizontal = 28.dp),
            text = stringResource(com.goodwy.keyboard.strings.R.string.about_message),
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.size(12.dp))

        val useGooglePlay = prefs.purchase.useGooglePlay.get() && prefs.purchase.isPlayStoreInstalled.get()
        val useRuStore = prefs.purchase.isRuStoreInstalled.get() && !useGooglePlay
        val rateUrl = if (useRuStore) stringRes(R.string.inkwell__rustore_url) else stringRes(R.string.inkwell__google_play_url)
        AboutRow(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            icon = Icons.Rounded.Star,
            title = stringRes(com.goodwy.keyboard.strings.R.string.about_rate_title),
            onClick = { context.launchUrlNew(rateUrl) },
        )
        val otherAppIcon = if (useRuStore) ImageVector.vectorResource(R.drawable.ic_rustore) else ImageVector.vectorResource(R.drawable.ic_google_play)
        val otherAppUrl = if (useRuStore) stringRes(R.string.inkwell__rustore_apps_url) else stringRes(R.string.inkwell__google_play_apps_url)
        AboutRow(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            icon = otherAppIcon,
            title = stringRes(com.goodwy.keyboard.strings.R.string.about_other_app_title),
            onClick = { context.launchUrlNew(otherAppUrl) },
        )
        val privacyPolicyUrl = stringRes(R.string.florisboard__privacy_policy_url)
        AboutRow(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            icon = Icons.Rounded.Policy,
            title = stringRes(R.string.about__privacy_policy__title),
            summary = stringRes(R.string.about__privacy_policy__summary),
            onClick = { context.launchUrlNew(privacyPolicyUrl) },
            )
        AboutRow(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            icon = Icons.Rounded.Savings,
            title = stringRes(id = com.goodwy.keyboard.strings.R.string.tipping_jar_title),
            color = MaterialTheme.colorScheme.primaryContainer,
            onClick = { navController.navigate(Routes.Settings.Purchase) },
        )
        AboutRow(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            icon = Icons.Rounded.Description,
            title = stringRes(R.string.about__project_license__title),
            summary = stringRes(com.goodwy.keyboard.strings.R.string.about__project_license__summary_g, "license_name" to "Apache 2.0"),
            onClick = { navController.navigate(Routes.Settings.ProjectLicense) },
        )
        AboutRow(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            icon = Icons.Rounded.Description,
            title = stringRes(id = R.string.about__third_party_licenses__title),
            summary = stringRes(id = R.string.about__third_party_licenses__summary),
            onClick = { navController.navigate(Routes.Settings.ThirdPartyLicenses) },
        )
        AboutRow(
            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 32.dp),
            icon = Icons.Rounded.Code,
            title = stringRes(R.string.about__repository__title),
            summary = stringRes(R.string.about__repository__summary, "license_name" to "Apache 2.0"),
            onClick = { context.launchUrlNew(R.string.florisboard__repo_url) },
        )
    }
}

@Composable
fun AboutRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    summary: String? = null,
    color: Color = MaterialTheme.colorScheme.inverseOnSurface,
    onClick: (() -> Unit),
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(modifier = Modifier.padding(start = 16.dp, end  = 8.dp).weight(1f),
                text = title.toUpperCase(LocaleList.current),
                fontSize = 16.sp,
                lineHeight = 18.sp,)
            Box (modifier = Modifier.padding(end = 8.dp, top = 8.dp, bottom = 8.dp).width(42.dp)) {
                val description = summary ?: title
                Icon(modifier = Modifier.alpha(0.2f).size(42.dp), imageVector = Icons.Rounded.Circle, contentDescription = description)
                Icon(modifier = Modifier.size(42.dp).padding(8.dp), imageVector = icon, contentDescription = description)
            }
        }
    }
}
