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

package com.goodwy.keyboard.app.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.SettingsSuggest
import androidx.compose.material.icons.rounded.Spellcheck
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.app.apptheme.*
import com.goodwy.keyboard.lib.compose.FlorisErrorCard
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.FlorisWarningCard
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.keyboard.lib.util.InputMethodUtils
import dev.patrickgold.jetpref.datastore.model.observeAsState

@Composable
fun HomeScreen() = FlorisScreen {
    title = stringRes(R.string.settings__title)
    navigationIconVisible = false
    previewFieldVisible = true

    val navController = LocalNavController.current
    val context = LocalContext.current

    content {
        // Support project
        val isPro by prefs.purchase.isPro.observeAsState()
        val isProSubs by prefs.purchase.isProSubs.observeAsState()
        val isProRustore by prefs.purchase.isProRustore.observeAsState()
        val isProApp = isPro || isProSubs || isProRustore
        if (!isProApp) {
            SupportRow(
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = { navController.navigate(Routes.Settings.Purchase) })
        }

        val isCollapsed by prefs.internal.homeIsBetaToolboxCollapsed.observeAsState()

        val isFlorisBoardEnabled by InputMethodUtils.observeIsFlorisboardEnabled(foregroundOnly = true)
        val isFlorisBoardSelected by InputMethodUtils.observeIsFlorisboardSelected(foregroundOnly = true)
        if (!isFlorisBoardEnabled) {
            FlorisErrorCard(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 16.dp),
                showIcon = false,
                text = stringRes(R.string.settings__home__ime_not_enabled_g),
                onClick = { InputMethodUtils.showImeEnablerActivity(context) },
            )
        } else if (!isFlorisBoardSelected) {
            FlorisWarningCard(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 16.dp),
                showIcon = false,
                text = stringRes(R.string.settings__home__ime_not_selected_g),
                onClick = { InputMethodUtils.showImePicker(context) },
            )
        }

        /*Card(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Welcome to the 0.4 alpha series!",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                    IconButton(onClick = { this@content.prefs.internal.homeIsBetaToolboxCollapsed.set(!isCollapsed) }) {
                        Icon(
                            painter = painterResource(if (isCollapsed) {
                                R.drawable.ic_keyboard_arrow_down
                            } else {
                                R.drawable.ic_keyboard_arrow_up
                            }),
                            contentDescription = null,
                        )
                    }
                }
                if (!isCollapsed) {
                    Text("0.4 will be quite a big release and finally work on adding support for word suggestion and inline autocorrect within the keyboard UI, at first for Latin-based languages. Additionally general improvements and bug fixes will also be made.\n")
                    Text("Currently the alpha releases are preparations for the suggestions implementation and general improvements and bug fixes.\n")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Note that this release does not contain support for word suggestions (will show the current word plus numbers as a placeholder).", color = Color.Red)
                    Text("Please DO NOT file an issue for this. It is already more than known and a major goal for implementation in 0.4.0. Thank you!\n")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }*/

        PreferenceGroupCard(paddingTop = 16.dp) {
            PreferenceRow(
                iconColor = preferenceOrange,
                icon = Icons.Default.Language,
                title = stringRes(R.string.settings__localization__title),
                onClick = { navController.navigate(Routes.Settings.Localization) },
            )
            DividerRow()
            PreferenceRow(
                iconColor = preferenceBlue,
                icon = Icons.Outlined.Palette,
                title = stringRes(R.string.settings__theme__title),
                onClick = { navController.navigate(Routes.Settings.Theme) },
            )
            DividerRow()
            PreferenceRow(
                iconColor = preferenceGreen,
                icon = Icons.Outlined.Keyboard,
                title = stringRes(R.string.settings__keyboard__title),
                onClick = { navController.navigate(Routes.Settings.Keyboard) },
            )
            DividerRow()
            PreferenceRow(
                iconColor = preferenceCyan,
                icon = Icons.Default.SmartButton,
                title = stringRes(R.string.settings__smartbar__title),
                onClick = { navController.navigate(Routes.Settings.Smartbar) },
            )
        }

        PreferenceGroupCard {
            PreferenceRow(
                iconColor = preferenceRed,
                icon = Icons.Rounded.Spellcheck,
                title = stringRes(R.string.settings__typing__title),
                onClick = { navController.navigate(Routes.Settings.Typing) },
            )
            DividerRow()
            PreferenceRow(
                iconColor = preferenceMagenta,
                icon = ImageVector.vectorResource(R.drawable.ic_dictionary),
                title = stringRes(R.string.settings__dictionary__title),
                onClick = { navController.navigate(Routes.Settings.Dictionary) },
            )
            DividerRow()
            PreferenceRow(
                iconColor = preferencePurple,
                icon = Icons.Default.Gesture,
                title = stringRes(R.string.settings__gestures__title),
                onClick = { navController.navigate(Routes.Settings.Gestures) },
            )
            DividerRow()
            PreferenceRow(
                iconColor = preferenceGreen,
                icon = Icons.Outlined.Assignment,
                title = stringRes(R.string.settings__clipboard__title),
                onClick = { navController.navigate(Routes.Settings.Clipboard) },
            )
            DividerRow()
            PreferenceRow(
                iconColor = preferenceYellow,
                icon = Icons.Default.SentimentSatisfiedAlt,
                title = stringRes(R.string.settings__media__title),
                onClick = { navController.navigate(Routes.Settings.Media) },
            )
        }

        PreferenceGroupCard {
            PreferenceRow(
                iconColor = preferenceBlue,
                icon = Icons.Rounded.SettingsSuggest,
                title = stringRes(R.string.settings__advanced__title),
                onClick = { navController.navigate(Routes.Settings.Advanced) },
            )
            if (prefs.devtools.showDevtools.get()) {
                DividerRow()
                PreferenceRow(
                    iconColor = preferencePurple,
                    icon = Icons.Default.Adb,
                    title = stringRes(R.string.devtools__title),
                    onClick = { navController.navigate(Routes.Devtools.Home) },
                )
            }
            DividerRow()
            PreferenceRow(
                iconColor = preferenceOrange,
                icon = Icons.Rounded.Savings,
                title = stringRes(R.string.tipping_jar_title),
                onClick = { navController.navigate(Routes.Settings.Purchase) },
            )
            DividerRow()
            PreferenceRow(
                iconColor = preferenceGrey,
                icon = Icons.Outlined.Info,
                title = stringRes(R.string.about__title),
                onClick = { navController.navigate(Routes.Settings.About) },
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
    }
}
