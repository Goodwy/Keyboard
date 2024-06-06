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

package com.goodwy.keyboard.app.settings.localization

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.ListPreferenceRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.PreferenceRow
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.cacheManager
import com.goodwy.keyboard.ime.core.DisplayLanguageNamesIn
import com.goodwy.keyboard.ime.keyboard.LayoutType
import com.goodwy.keyboard.keyboardManager
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.FlorisWarningCard
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.keyboard.lib.observeAsNonNullState
import com.goodwy.keyboard.subtypeManager
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.ListPreference
import dev.patrickgold.jetpref.datastore.ui.Preference
import dev.patrickgold.jetpref.datastore.ui.PreferenceGroup

@Composable
fun LocalizationScreen() = FlorisScreen {
    title = stringRes(R.string.settings__localization__title)
    previewFieldVisible = true
    iconSpaceReserved = false

    val navController = LocalNavController.current
    val context = LocalContext.current
    val keyboardManager by context.keyboardManager()
    val subtypeManager by context.subtypeManager()
    val cacheManager by context.cacheManager()

    floatingActionButton {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringRes(R.string.settings__localization__subtype_add_title),
                )
            },
            text = {
                Text(
                    text = stringRes(R.string.settings__localization__subtype_add_title),
                )
            },
            shape = FloatingActionButtonDefaults.extendedFabShape,
            onClick = { navController.navigate(Routes.Settings.SubtypeAdd) },
        )
    }


    content {
        val subtypes by subtypeManager.subtypesFlow.collectAsState()

        PreferenceGroupCard {
            ListPreferenceRow(
                prefs.localization.displayLanguageNamesIn,
                title = stringRes(R.string.settings__localization__display_language_names_in__label),
                entries = DisplayLanguageNamesIn.listEntries(),
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
//            icon = R.drawable.ic_edit,
                title = stringRes(R.string.settings__localization__language_pack_title),
                summary = stringRes(R.string.settings__localization__language_pack_summary),
                onClick = {
                    navController.navigate(Routes.Settings.LanguagePackManager(LanguagePackManagerScreenAction.MANAGE))
                },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                pref = prefs.localization.switchEmojiWhenChangingLanguage,
                title = stringRes(R.string.settings__emoji_as_subtype),
                summary = stringRes(R.string.settings__switch_to_emoji_when_changing_language),
                enabledIf = { subtypes.isNotEmpty() },
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.settings__localization__group_subtypes__label)) {
            if (subtypes.isEmpty()) {
                FlorisWarningCard(
                    modifier = Modifier.padding(all = 8.dp),
                    text = stringRes(R.string.settings__localization__subtype_no_subtypes_configured_warning),
                    corners = 6.dp,
                )
            } else {
                val currencySets by keyboardManager.resources.currencySets.observeAsNonNullState()
                val layouts by keyboardManager.resources.layouts.observeAsNonNullState()
                val displayLanguageNamesIn by prefs.localization.displayLanguageNamesIn.observeAsState()
                for (subtype in subtypes) {
                    val cMeta = layouts[LayoutType.CHARACTERS]?.get(subtype.layoutMap.characters)
                    val sMeta = layouts[LayoutType.SYMBOLS]?.get(subtype.layoutMap.symbols)
                    val currMeta = currencySets[subtype.currencySet]
                    val summary = stringRes(
                        id = R.string.settings__localization__subtype_summary,
                        "characters_name" to (cMeta?.label ?: "null"),
                        "symbols_name" to (sMeta?.label ?: "null"),
                        "currency_set_name" to (currMeta?.label ?: "null"),
                    )
                    PreferenceRow(
                        title = when (displayLanguageNamesIn) {
                            DisplayLanguageNamesIn.SYSTEM_LOCALE -> subtype.primaryLocale.displayName()
                            DisplayLanguageNamesIn.NATIVE_LOCALE -> subtype.primaryLocale.displayName(subtype.primaryLocale)
                        },
                        summary = summary,
                        onClick = {
                            navController.navigate(
                                Routes.Settings.SubtypeEdit(subtype.id)
                            )
                        },
                    )
                    if (subtypes.last() != subtype) DividerRow(start = 16.dp)
                }
            }
        }
        Spacer(modifier = Modifier.size(32.dp))

        //PreferenceGroup(title = stringRes(R.string.settings__localization__group_layouts__label)) {
        //}
    }
}
