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

package com.goodwy.keyboard.app.settings.clipboard

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.settings.DialogSliderPreferenceRow
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.pluralsRes
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.lib.android.AndroidVersion
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun ClipboardScreen() = FlorisScreen {
    title = stringRes(R.string.settings__clipboard__title)
    previewFieldVisible = true

    content {
        PreferenceGroupCard {
            SwitchPreferenceRow(
                prefs.clipboard.useInternalClipboard,
                title = stringRes(R.string.pref__clipboard__use_internal_clipboard__label),
                summary = stringRes(R.string.pref__clipboard__use_internal_clipboard__summary),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.clipboard.syncToFloris,
                title = stringRes(R.string.pref__clipboard__sync_from_system_clipboard__label),
                summary = stringRes(com.goodwy.keyboard.strings.R.string.pref__clipboard__sync_from_system_clipboard__summary_g),
                enabledIf = { prefs.clipboard.useInternalClipboard isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.clipboard.syncToSystem,
                title = stringRes(R.string.pref__clipboard__sync_to_system_clipboard__label),
                summary = stringRes(com.goodwy.keyboard.strings.R.string.pref__clipboard__sync_to_system_clipboard__summary_g),
                enabledIf = { prefs.clipboard.useInternalClipboard isEqualTo true },
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.pref__clipboard__group_clipboard_history__label)) {
            SwitchPreferenceRow(
                prefs.clipboard.historyEnabled,
                title = stringRes(R.string.pref__clipboard__enable_clipboard_history__label),
                summary = stringRes(R.string.pref__clipboard__enable_clipboard_history__summary),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.clipboard.cleanUpOld,
                title = stringRes(R.string.pref__clipboard__clean_up_old__label),
                enabledIf = { prefs.clipboard.historyEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.clipboard.cleanUpAfter,
                title = stringRes(R.string.pref__clipboard__clean_up_after__label),
                valueLabel = { pluralsRes(R.plurals.unit__minutes__written, it, "v" to it) },
                min = 0,
                max = 120,
                stepIncrement = 5,
                enabledIf = { prefs.clipboard.historyEnabled isEqualTo true && prefs.clipboard.cleanUpOld isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.clipboard.autoCleanSensitive,
                title = stringRes(R.string.pref__clipboard__auto_clean_sensitive__label),
                enabledIf = { prefs.clipboard.historyEnabled isEqualTo true },
                visibleIf = { AndroidVersion.ATLEAST_API33_T },
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.clipboard.autoCleanSensitiveAfter,
                title = stringRes(R.string.pref__clipboard__auto_clean_sensitive_after__label),
                valueLabel = { pluralsRes(R.plurals.unit__seconds__written, it, "v" to it) },
                min = 0,
                max = 300,
                stepIncrement = 10,
                enabledIf = { prefs.clipboard.historyEnabled isEqualTo true && prefs.clipboard.autoCleanSensitive isEqualTo true },
                visibleIf = { AndroidVersion.ATLEAST_API33_T },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.clipboard.limitHistorySize,
                title = stringRes(R.string.pref__clipboard__limit_history_size__label),
                enabledIf = { prefs.clipboard.historyEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.clipboard.maxHistorySize,
                title = stringRes(R.string.pref__clipboard__max_history_size__label),
                valueLabel = { pluralsRes(R.plurals.unit__items__written, it, "v" to it) },
                min = 5,
                max = 100,
                stepIncrement = 5,
                enabledIf = { prefs.clipboard.historyEnabled isEqualTo true && prefs.clipboard.limitHistorySize isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.clipboard.clearPrimaryClipDeletesLastItem,
                title = stringRes(R.string.pref__clipboard__clear_primary_clip_deletes_last_item__label),
                summary = stringRes(R.string.pref__clipboard__clear_primary_clip_deletes_last_item__summary),
                enabledIf = { prefs.clipboard.historyEnabled isEqualTo true },
            )
        }
        Spacer(modifier = Modifier.size(82.dp))
    }
}
