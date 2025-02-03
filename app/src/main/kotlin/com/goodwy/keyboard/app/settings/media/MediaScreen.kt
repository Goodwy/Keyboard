/*
 * Copyright (C) 2022 Patrick Goldinger
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

package com.goodwy.keyboard.app.settings.media

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiSymbols
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.enumDisplayEntriesOf
import com.goodwy.keyboard.app.florisPreferenceModel
import com.goodwy.keyboard.app.settings.DialogSliderPreferenceRow
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.ListPreferenceRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.PreferenceRow
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.ime.media.emoji.EmojiCategory
import com.goodwy.keyboard.ime.media.emoji.EmojiHistory
import com.goodwy.keyboard.ime.media.emoji.EmojiHistoryHelper
import com.goodwy.keyboard.ime.media.emoji.EmojiSkinTone
import com.goodwy.keyboard.ime.media.emoji.EmojiSuggestionType
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.pluralsRes
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.jetpref.datastore.ui.Preference
import dev.patrickgold.jetpref.datastore.ui.PreferenceGroup
import dev.patrickgold.jetpref.datastore.ui.SwitchPreference
import dev.patrickgold.jetpref.material.ui.JetPrefAlertDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun MediaScreen() = FlorisScreen {
    title = stringRes(R.string.settings__media__title)
    previewFieldVisible = true
    iconSpaceReserved = true

    val prefs by florisPreferenceModel()

    var shouldDelete by remember { mutableStateOf<ShouldDelete?>(null) }
    val scope = rememberCoroutineScope()

    content {
        PreferenceGroupCard {
            SwitchPreferenceRow(
                pref = prefs.emoji.emojiUseLastTab,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__open_last_tab),
            )
            DividerRow(start = 16.dp)
            val emojiDefaultTab = prefs.emoji.emojiDefaultTab.observeAsState()
            ListPreferenceRow(
                prefs.emoji.emojiDefaultTab,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__default_tab),
                entries = enumDisplayEntriesOf(EmojiCategory::class),
                enabledIf = { prefs.emoji.emojiUseLastTab isEqualTo false },
                showEndIcon = true,
                endIcon = emojiDefaultTab.value.icon()
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                pref = prefs.emoji.emojiUseHorizontalGrid,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__use_horizontal_grid),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.emoji.preferredSkinTone,
                title = stringRes(R.string.prefs__media__emoji_preferred_skin_tone),
                entries = enumDisplayEntriesOf(EmojiSkinTone::class),
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.prefs__media__emoji_history__title)) {
            SwitchPreferenceRow(
                prefs.emoji.historyEnabled,
                icon = Icons.Outlined.Schedule,
                title = stringRes(R.string.prefs__media__emoji_history_enabled),
                summary = stringRes(R.string.prefs__media__emoji_history_enabled__summary),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.emoji.historyPinnedUpdateStrategy,
                title = stringRes(R.string.prefs__media__emoji_history_pinned_update_strategy),
                entries = enumDisplayEntriesOf(EmojiHistory.UpdateStrategy::class),
                enabledIf = { prefs.emoji.historyEnabled.isTrue() },
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.emoji.historyRecentUpdateStrategy,
                title = stringRes(R.string.prefs__media__emoji_history_recent_update_strategy),
                entries = enumDisplayEntriesOf(EmojiHistory.UpdateStrategy::class),
                enabledIf = { prefs.emoji.historyEnabled.isTrue() },
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                primaryPref = prefs.emoji.historyPinnedMaxSize,
                secondaryPref = prefs.emoji.historyRecentMaxSize,
                title = stringRes(R.string.prefs__media__emoji_history_max_size),
                primaryLabel = stringRes(R.string.emoji__history__pinned),
                secondaryLabel = stringRes(R.string.emoji__history__recent),
                valueLabel = { maxSize ->
                    if (maxSize == EmojiHistory.MaxSizeUnlimited) {
                        stringRes(R.string.general__unlimited)
                    } else {
                        pluralsRes(R.plurals.unit__items__written, maxSize, "v" to maxSize)
                    }
                },
                min = 0,
                max = 120,
                stepIncrement = 1,
                enabledIf = { prefs.emoji.historyEnabled.isTrue() },
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.prefs__media__emoji_history_pinned_reset),
                onClick = {
                    shouldDelete = ShouldDelete(true)
                },
                enabledIf = { prefs.emoji.historyEnabled.isTrue() },
                endIcon = Icons.Rounded.RestartAlt
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.prefs__media__emoji_history_reset),
                onClick = {
                    shouldDelete = ShouldDelete(false)
                },
                enabledIf = { prefs.emoji.historyEnabled.isTrue() },
                endIcon = Icons.Rounded.RestartAlt
            )

        }

        PreferenceGroupCard(title = stringRes(R.string.prefs__media__emoji_suggestion__title)) {
            SwitchPreferenceRow(
                prefs.emoji.suggestionEnabled,
                icon = Icons.Outlined.EmojiSymbols,
                title = stringRes(R.string.prefs__media__emoji_suggestion_enabled),
                summary = stringRes(R.string.prefs__media__emoji_suggestion_enabled__summary),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.emoji.suggestionType,
                title = stringRes(R.string.prefs__media__emoji_suggestion_type),
                entries = enumDisplayEntriesOf(EmojiSuggestionType::class),
                enabledIf = { prefs.emoji.suggestionEnabled.isTrue() },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.emoji.suggestionUpdateHistory,
                title = stringRes(R.string.prefs__media__emoji_suggestion_update_history),
                summary = stringRes(R.string.prefs__media__emoji_suggestion_update_history__summary),
                enabledIf = {
                    prefs.emoji.suggestionEnabled.isTrue() && prefs.emoji.historyEnabled.isTrue()
                },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.emoji.suggestionCandidateShowName,
                title = stringRes(R.string.prefs__media__emoji_suggestion_candidate_show_name),
                summary = stringRes(R.string.prefs__media__emoji_suggestion_candidate_show_name__summary),
                enabledIf = { prefs.emoji.suggestionEnabled.isTrue() },
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.emoji.suggestionQueryMinLength,
                title = stringRes(R.string.prefs__media__emoji_suggestion_query_min_length),
                valueLabel = { length ->
                    pluralsRes(R.plurals.unit__characters__written, length, "v" to length)
                },
                min = 1,
                max = 5,
                stepIncrement = 1,
                enabledIf = { prefs.emoji.suggestionEnabled.isTrue() },
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.emoji.suggestionCandidateMaxCount,
                title = stringRes(R.string.prefs__media__emoji_suggestion_candidate_max_count),
                valueLabel = { count ->
                    pluralsRes(R.plurals.unit__candidates__written, count, "v" to count)
                },
                min = 1,
                max = 10,
                stepIncrement = 1,
                enabledIf = { prefs.emoji.suggestionEnabled.isTrue() },
            )
        }
        Spacer(modifier = Modifier.size(82.dp))
    }

    DeleteEmojiHistoryConfirmDialog(
        shouldDelete = shouldDelete,
        onDismiss = {
            shouldDelete = null
        },
        onConfirm = {
            shouldDelete?.let {
                scope.launch {
                    if (it.pinned) {
                        EmojiHistoryHelper.deletePinned(prefs = prefs)
                    } else {
                        EmojiHistoryHelper.deleteHistory(prefs = prefs)
                    }
                }
                shouldDelete = null
            }
        },
    )

    Spacer(modifier = Modifier.size(82.dp))
}

@Composable
fun DeleteEmojiHistoryConfirmDialog(
    shouldDelete: ShouldDelete?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    shouldDelete?.let {
        JetPrefAlertDialog(
            title = stringRes(R.string.action__reset_confirm_title),
            confirmLabel = stringRes(R.string.action__yes),
            dismissLabel = stringRes(R.string.action__no),
            onDismiss = onDismiss,
            onConfirm = onConfirm,
        ) {
            if (it.pinned) {
                Text(stringRes(R.string.action__reset_confirm_message, "name" to "pinned emojis"))
            } else {
                Text(stringRes(R.string.action__reset_confirm_message, "name" to "emoji history"))
            }

        }
    }
}

data class ShouldDelete(val pinned: Boolean)

