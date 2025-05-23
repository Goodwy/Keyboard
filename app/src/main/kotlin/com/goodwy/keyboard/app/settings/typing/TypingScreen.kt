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

package com.goodwy.keyboard.app.settings.typing

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.app.enumDisplayEntriesOf
import com.goodwy.keyboard.app.settings.DialogSliderPreferenceRow
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.ListPreferenceRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.PreferenceRow
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.ime.nlp.SpellingLanguageMode
import com.goodwy.keyboard.lib.compose.FlorisErrorCard
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.lib.android.AndroidVersion
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun TypingScreen() = FlorisScreen {
    title = stringRes(R.string.settings__typing__title)
    previewFieldVisible = true

    val navController = LocalNavController.current

    content {
        // This card is temporary and is therefore not using a string resource (not so temporary as we thought...)
        FlorisErrorCard(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
            text = """
                Suggestions (except system autofill) and spell checking are not available in this release. All
                preferences in the "Corrections" group are properly implemented though.
            """.trimIndent().replace('\n', ' '),
        )

        PreferenceGroupCard(title = stringRes(R.string.pref__suggestion__title)) {
            SwitchPreferenceRow(
                prefs.suggestion.enabled,
                title = stringRes(R.string.pref__suggestion__enabled__label),
                summary = stringRes(R.string.pref__suggestion__enabled__summary),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.suggestion.blockPossiblyOffensive,
                title = stringRes(R.string.pref__suggestion__block_possibly_offensive__label),
                summary = stringRes(R.string.pref__suggestion__block_possibly_offensive__summary),
                enabledIf = { prefs.suggestion.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.suggestion.clipboardContentEnabled,
                title = stringRes(R.string.pref__suggestion__clipboard_content_enabled__label),
                summary = stringRes(R.string.pref__suggestion__clipboard_content_enabled__summary),
                enabledIf = { prefs.suggestion.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.suggestion.clipboardContentTimeout,
                title = stringRes(R.string.pref__suggestion__clipboard_content_timeout__label),
                valueLabel = { stringRes(R.string.pref__suggestion__clipboard_content_timeout__summary, "v" to it) },
                min = 30,
                max = 300,
                stepIncrement = 5,
                enabledIf = { prefs.suggestion.enabled isEqualTo true },
                visibleIf = { prefs.suggestion.clipboardContentEnabled isEqualTo true },
                oldView = true
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.suggestion.api30InlineSuggestionsEnabled,
                title = stringRes(R.string.pref__suggestion__api30_inline_suggestions_enabled__label),
                summary = stringRes(R.string.pref__suggestion__api30_inline_suggestions_enabled__summary),
                visibleIf = { AndroidVersion.ATLEAST_API30_R },
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.pref__correction__title)) {
            SwitchPreferenceRow(
                prefs.correction.autoCapitalization,
                title = stringRes(R.string.pref__correction__auto_capitalization__label),
                summary = stringRes(R.string.pref__correction__auto_capitalization__summary),
            )
            DividerRow(start = 16.dp)
            val isAutoSpacePunctuationEnabled by prefs.correction.autoSpacePunctuation.observeAsState()
            SwitchPreferenceRow(
                prefs.correction.autoSpacePunctuation,
                //icon = Icons.Default.SpaceBar,
                title = stringRes(R.string.pref__correction__auto_space_punctuation__label),
                summary = stringRes(R.string.pref__correction__auto_space_punctuation__summary),
            )
            if (isAutoSpacePunctuationEnabled) {
//                Card(
//                    modifier = Modifier.padding(8.dp),
//                    shape = RoundedCornerShape(6.dp),
//                ) {
//                    Column(modifier = Modifier.padding(8.dp)) {
//                        Text(
//                            text = """
//                                Auto-space after punctuation is an experimental feature which may break or behave
//                                unexpectedly. If you want, please give feedback about it in below linked feedback
//                                thread. This helps a lot in improving this feature. Thanks!
//                            """.trimIndent().replace('\n', ' '),
//                        )
//                        FlorisHyperlinkText(
//                            text = "Feedback thread (GitHub)",
//                            url = "https://github.com/florisboard/florisboard/discussions/1935",
//                        )
//                    }
//                }
                Card(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(6.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = """
                                Auto-space after punctuation is an experimental feature which may break or behave
                                unexpectedly.
                            """.trimIndent().replace('\n', ' '),
                    )
                }
            }
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.correction.rememberCapsLockState,
                title = stringRes(R.string.pref__correction__remember_caps_lock_state__label),
                summary = stringRes(R.string.pref__correction__remember_caps_lock_state__summary),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.correction.doubleSpacePeriod,
                title = stringRes(R.string.pref__correction__double_space_period__label),
                summary = stringRes(R.string.pref__correction__double_space_period__summary),
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.pref__spelling__title)) {
            val florisSpellCheckerEnabled = remember { mutableStateOf(false) }
            SpellCheckerServiceSelector(florisSpellCheckerEnabled)
            ListPreferenceRow(
                prefs.spelling.languageMode,
                icon = Icons.Default.Language,
                title = stringRes(R.string.pref__spelling__language_mode__label),
                entries = enumDisplayEntriesOf(SpellingLanguageMode::class),
                enabledIf = { florisSpellCheckerEnabled.value },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.spelling.useContacts,
                icon = Icons.Default.Contacts,
                title = stringRes(R.string.pref__spelling__use_contacts__label),
                summary = stringRes(R.string.pref__spelling__use_contacts__summary),
                enabledIf = { florisSpellCheckerEnabled.value },
                visibleIf = { false }, // For now
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.spelling.useUdmEntries,
                icon = Icons.AutoMirrored.Rounded.LibraryBooks,
                title = stringRes(R.string.pref__spelling__use_udm_entries__label),
                summary = stringRes(R.string.pref__spelling__use_udm_entries__summary),
                enabledIf = { florisSpellCheckerEnabled.value },
                visibleIf = { false }, // For now
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.settings__dictionary__title)) {
            PreferenceRow(
                icon = Icons.AutoMirrored.Rounded.LibraryBooks,
                title = stringRes(R.string.settings__dictionary__title),
                onClick = { navController.navigate(Routes.Settings.Dictionary) },
            )
        }
        Spacer(modifier = Modifier.size(82.dp))
    }
}
