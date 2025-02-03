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

package com.goodwy.keyboard.app.settings.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.app.enumDisplayEntriesOf
import com.goodwy.keyboard.app.ext.AddonManagementReferenceBox
import com.goodwy.keyboard.app.ext.ExtensionListScreenType
import com.goodwy.keyboard.app.settings.ALPHA_DISABLED
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.ListPreferenceRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.PreferenceRow
import com.goodwy.keyboard.app.settings.SupportRow
import com.goodwy.keyboard.app.settings.shake
import com.goodwy.keyboard.ime.theme.ThemeManager
import com.goodwy.keyboard.ime.theme.ThemeMode
import com.goodwy.keyboard.lib.compose.FlorisInfoCard
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringAddLockedLabelIfNeeded
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.keyboard.lib.compose.stringResAddLockedLabelIfNeeded
import com.goodwy.keyboard.lib.ext.ExtensionComponentName
import com.goodwy.keyboard.themeManager
import com.goodwy.lib.kotlin.curlyFormat
import dev.patrickgold.jetpref.datastore.model.observeAsState

@Composable
fun ThemeScreen() = FlorisScreen {
    title = stringRes(R.string.settings__theme__title)
    previewFieldVisible = true

    val context = LocalContext.current
    val navController = LocalNavController.current
    val themeManager by context.themeManager()

    @Composable
    fun ThemeManager.getThemeLabel(id: ExtensionComponentName): String {
        val configs by indexedThemeConfigs.observeAsState()
        configs?.get(id)?.let { return it.label }
        return id.toString()
    }

    content {
        // Support project
        val isPro by prefs.purchase.isPro.observeAsState()
        val isProSubs by prefs.purchase.isProSubs.observeAsState()
        val isProRustore by prefs.purchase.isProRustore.observeAsState()
        val isProNoGP by prefs.purchase.isProNoGP.observeAsState()
        val isProApp = isPro || isProSubs || isProRustore || isProNoGP
        var enabledShake by remember { mutableStateOf(false) }
        val haptic = LocalHapticFeedback.current
        if (!isProApp) {
            SupportRow(
                modifier = Modifier
                    .shake(enabledShake) { enabledShake = false }
                    .padding(horizontal = 12.dp),
                onClick = { navController.navigate(Routes.Settings.Purchase) },
            )
        }

        val themeMode by prefs.theme.mode.observeAsState()
        val dayThemeId by prefs.theme.dayThemeId.observeAsState()
        val nightThemeId by prefs.theme.nightThemeId.observeAsState()

//        Card(modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)) {
//            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
//                Text("If you want to give feedback on the new stylesheet editor and theme engine, please do so in below linked feedback thread:\n")
//                Button(onClick = {
//                    context.launchUrl("https://github.com/florisboard/florisboard/discussions/1531")
//                }) {
//                    Text("Open Feedback Thread")
//                }
//            }
//        }

        PreferenceGroupCard(paddingTop = 16.dp) {
            ListPreferenceRow(
                prefs.theme.mode,
                icon = Icons.Default.BrightnessAuto,
                title = stringRes(R.string.pref__theme__mode__label),
                entries = enumDisplayEntriesOf(ThemeMode::class),
            )
            if (themeMode == ThemeMode.FOLLOW_TIME) {
                FlorisInfoCard(
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp, bottom = 8.dp),
                    text = """
                The theme mode "Follow time" is not available in this beta release.
            """.trimIndent(),
                    corners = 6.dp
                )
            }
            DividerRow()
            PreferenceRow(
                icon = Icons.Default.LightMode,
                title = stringRes(R.string.pref__theme__day),
                value = themeManager.getThemeLabel(dayThemeId),
                enabledIf = { prefs.theme.mode isNotEqualTo ThemeMode.ALWAYS_NIGHT },
                onClick = {
                    navController.navigate(Routes.Settings.ThemeManager(ThemeManagerScreenAction.SELECT_DAY))
                },
                showEndIcon = false,
            )
            DividerRow()
            PreferenceRow(
                modifier = Modifier.alpha(if (isProApp) 1f else ALPHA_DISABLED),
                icon = Icons.Default.DarkMode,
                title = stringResAddLockedLabelIfNeeded(R.string.pref__theme__night, !isProApp),
                value = themeManager.getThemeLabel(nightThemeId),
                enabledIf = { prefs.theme.mode isNotEqualTo ThemeMode.ALWAYS_DAY },
                onClick = {
                    if (isProApp) navController.navigate(Routes.Settings.ThemeManager(ThemeManagerScreenAction.SELECT_NIGHT))
                    else {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        enabledShake = true
                    }
                },
                showEndIcon = false,
            )
        }

//        AddonManagementReferenceBox(type = ExtensionListScreenType.EXT_THEME)

        PreferenceGroupCard(paddingTop = 16.dp) {
            val type = ExtensionListScreenType.EXT_THEME
            val title = stringRes(id = R.string.ext__addon_management_box__managing_placeholder).curlyFormat(
                "extensions" to type.let { stringRes(id = it.titleResId).lowercase() }
            )
            PreferenceRow(
                modifier = Modifier.alpha(if (isProApp) 1f else ALPHA_DISABLED),
                icon = ImageVector.vectorResource(R.drawable.ic_extension),
                title = stringAddLockedLabelIfNeeded(title, !isProApp),
                summary = stringRes(id = R.string.ext__addon_management_box__addon_manager_info),
                onClick = {
                    if (isProApp) {
                        val route = Routes.Ext.List(type = ExtensionListScreenType.EXT_THEME, showUpdate = true)
                        navController.navigate(
                            route
                        )
                    }
                    else {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        enabledShake = true
                    }
                },
            )
        }
        Spacer(modifier = Modifier.size(82.dp))
    }
}
