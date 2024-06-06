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

package com.goodwy.keyboard.app.settings.advanced

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.AppTheme
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.ListPreferenceRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.PreferenceRow
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.ime.core.DisplayLanguageNamesIn
import com.goodwy.keyboard.ime.keyboard.IncognitoMode
import com.goodwy.keyboard.lib.FlorisLocale
import com.goodwy.keyboard.lib.android.AndroidVersion
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.listPrefEntries
import dev.patrickgold.jetpref.datastore.ui.vectorResource

@Composable
fun AdvancedScreen() = FlorisScreen {
    title = stringRes(R.string.settings__advanced__title)
    previewFieldVisible = false

    val navController = LocalNavController.current

    content {
        PreferenceGroupCard {
            ListPreferenceRow(
                prefs.advanced.settingsTheme,
                icon = Icons.Default.Palette,
                title = stringRes(R.string.pref__advanced__settings_theme__label),
                entries = listPrefEntries {
                    entry(
                        key = AppTheme.AUTO,
                        label = stringRes(R.string.settings__system_default),
                    )
                    entry(
                        key = AppTheme.AUTO_AMOLED,
                        label = stringRes(R.string.pref__advanced__settings_theme__auto_amoled),
                    )
                    entry(
                        key = AppTheme.LIGHT,
                        label = stringRes(R.string.pref__advanced__settings_theme__light),
                    )
                    entry(
                        key = AppTheme.DARK,
                        label = stringRes(R.string.pref__advanced__settings_theme__dark),
                    )
                    entry(
                        key = AppTheme.AMOLED_DARK,
                        label = stringRes(R.string.pref__advanced__settings_theme__amoled_dark),
                    )
                },
            )
            DividerRow()
            SwitchPreferenceRow(
                pref = prefs.advanced.useMaterialYou,
                icon = Icons.Default.FormatPaint,
                title = stringRes(R.string.pref__advanced__settings_material_you__label),
                visibleIf = {
                    AndroidVersion.ATLEAST_API31_S
                },
            )
            DividerRow()
            ListPreferenceRow(
                prefs.advanced.settingsLanguage,
                icon = Icons.Default.Language,
                title = stringRes(R.string.pref__advanced__settings_language__label),
                entries = listPrefEntries {
                    listOf(
                        "auto",
                        "ar",
                        "bg",
                        "bs",
                        "ca",
                        "ckb",
                        "cs",
                        "da",
                        "de",
                        "el",
                        "en",
                        "eo",
                        "es",
                        "fa",
                        "fi",
                        "fr",
                        "hr",
                        "hu",
                        "in",
                        "it",
                        "iw",
                        "ja",
                        "ko-KR",
                        "ku",
                        "lv-LV",
                        "mk",
                        "nds-DE",
                        "nl",
                        "no",
                        "pl",
                        "pt",
                        "pt-BR",
                        "ru",
                        "sk",
                        "sl",
                        "sr",
                        "sv",
                        "tr",
                        "uk",
                        "zgh",
                        "zh-CN",
                    ).map { languageTag ->
                        if (languageTag == "auto") {
                            entry(
                                key = "auto",
                                label = stringRes(R.string.settings__system_default),
                            )
                        } else {
                            val displayLanguageNamesIn by prefs.localization.displayLanguageNamesIn.observeAsState()
                            val locale = FlorisLocale.fromTag(languageTag)
                            entry(locale.languageTag(), when (displayLanguageNamesIn) {
                                DisplayLanguageNamesIn.SYSTEM_LOCALE -> locale.displayName()
                                DisplayLanguageNamesIn.NATIVE_LOCALE -> locale.displayName(locale)
                            })
                        }
                    }
                }
            )
            DividerRow()
            SwitchPreferenceRow(
                prefs.advanced.showAppIcon,
                icon = Icons.Default.Preview,
                title = stringRes(R.string.pref__advanced__show_app_icon__label),
                summary = when {
                    AndroidVersion.ATLEAST_API29_Q -> stringRes(R.string.pref__advanced__show_app_icon__summary_atleast_q)
                    else -> null
                },
                enabledIf = { AndroidVersion.ATMOST_API28_P },
            )
            DividerRow()
            ListPreferenceRow(
                prefs.advanced.incognitoMode,
                icon = vectorResource(id = R.drawable.ic_incognito),
                title = stringRes(R.string.pref__advanced__incognito_mode__label),
                entries = IncognitoMode.listEntries(),
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.backup_and_restore__title)) {
            PreferenceRow(
                onClick = { navController.navigate(Routes.Settings.Backup) },
                icon = Icons.Default.Archive,
                title = stringRes(R.string.backup_and_restore__back_up__title),
                summary = stringRes(R.string.backup_and_restore__back_up__summary),
            )
            DividerRow()
            PreferenceRow(
                onClick = { navController.navigate(Routes.Settings.Restore) },
                icon = Icons.Default.SettingsBackupRestore,
                title = stringRes(R.string.backup_and_restore__restore__title),
                summary = stringRes(R.string.backup_and_restore__restore__summary),
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
    }
}
