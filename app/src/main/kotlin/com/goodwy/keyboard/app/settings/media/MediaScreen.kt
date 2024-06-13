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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.settings.DialogSliderPreferenceRow
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.ListPreferenceRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.ime.media.emoji.EmojiCategory
import com.goodwy.keyboard.ime.media.emoji.EmojiSkinTone
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.pluralsRes
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun MediaScreen() = FlorisScreen {
    title = stringRes(R.string.settings__media__title)
    previewFieldVisible = true
    iconSpaceReserved = false

    content {
        PreferenceGroupCard {
            DialogSliderPreferenceRow(
                prefs.media.emojiRecentlyUsedMaxSize,
                title = stringRes(R.string.prefs__media__emoji_recently_used_max_size),
                valueLabel = { maxSize ->
                    if (maxSize == 0) {
                        stringRes(R.string.general__unlimited)
                    } else {
                        pluralsRes(R.plurals.unit__items__written, maxSize, "v" to maxSize)
                    }
                },
                min = 0,
                max = 120,
                stepIncrement = 1,
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                pref = prefs.media.emojiUseLastTab,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__open_last_tab),
            )
            DividerRow(start = 16.dp)
            val emojiDefaultTab = prefs.media.emojiDefaultTab.observeAsState()
            ListPreferenceRow(
                prefs.media.emojiDefaultTab,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__default_tab),
                entries = EmojiCategory.listEntries(),
                enabledIf = { prefs.media.emojiUseLastTab isEqualTo false },
                showEndIcon = true,
                endIcon = emojiDefaultTab.value.icon()
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                pref = prefs.media.emojiUseHorizontalGrid,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__use_horizontal_grid),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.media.emojiPreferredSkinTone,
                title = stringRes(R.string.prefs__media__emoji_preferred_skin_tone),
                entries = EmojiSkinTone.listEntries(),
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
    }
}
