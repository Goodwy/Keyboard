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

package com.goodwy.keyboard.app.settings.smartbar

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.enumDisplayEntriesOf
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.ListPreferenceRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.ime.smartbar.CandidatesDisplayMode
import com.goodwy.keyboard.ime.smartbar.ExtendedActionsPlacement
import com.goodwy.keyboard.ime.smartbar.SmartbarLayout
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes

@Composable
fun SmartbarScreen() = FlorisScreen {
    title = stringRes(R.string.settings__smartbar__title)
    previewFieldVisible = true

    content {
        PreferenceGroupCard {
            SwitchPreferenceRow(
                prefs.smartbar.enabled,
                title = stringRes(R.string.pref__smartbar__enabled__label),
                summary = stringRes(R.string.pref__smartbar__enabled__summary),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                listPref = prefs.smartbar.layout,
                title = stringRes(R.string.pref__smartbar__layout__label),
                entries = enumDisplayEntriesOf(SmartbarLayout::class),
                enabledIf = { prefs.smartbar.enabled isEqualTo true },
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.pref__smartbar__group_layout_specific__label)) {
            ListPreferenceRow(
                prefs.suggestion.displayMode,
                title = stringRes(R.string.pref__suggestion__display_mode__label),
                entries = enumDisplayEntriesOf(CandidatesDisplayMode::class),
                enabledIf = { prefs.smartbar.enabled isEqualTo true },
                visibleIf = { prefs.smartbar.layout isNotEqualTo SmartbarLayout.ACTIONS_ONLY },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.smartbar.flipToggles,
                title = stringRes(R.string.pref__smartbar__flip_toggles__label),
                summary = stringRes(R.string.pref__smartbar__flip_toggles__summary),
                enabledIf = { prefs.smartbar.enabled isEqualTo true },
                visibleIf = {
                    prefs.smartbar.layout isEqualTo SmartbarLayout.SUGGESTIONS_ACTIONS_SHARED ||
                        prefs.smartbar.layout isEqualTo SmartbarLayout.SUGGESTIONS_ACTIONS_EXTENDED
                },
            )
            DividerRow(start = 16.dp)
            // TODO: schedule to remove this preference in the future, but keep it for now so users
            //  know why the setting is not available anymore. Also force enable it for UI display.
            SideEffect {
                prefs.smartbar.sharedActionsAutoExpandCollapse.set(true)
            }
            SwitchPreferenceRow(
                prefs.smartbar.sharedActionsAutoExpandCollapse,
                title = stringRes(R.string.pref__smartbar__shared_actions_auto_expand_collapse__label),
                summary = "[Since v0.2.9] Always enabled due to UX issues",
                enabledIf = { false },
                visibleIf = { prefs.smartbar.layout isEqualTo SmartbarLayout.SUGGESTIONS_ACTIONS_SHARED },
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                listPref = prefs.smartbar.extendedActionsPlacement,
                title = stringRes(R.string.pref__smartbar__extended_actions_placement__label),
                entries = enumDisplayEntriesOf(ExtendedActionsPlacement::class),
                enabledIf = { prefs.smartbar.enabled isEqualTo true },
                visibleIf = { prefs.smartbar.layout isEqualTo SmartbarLayout.SUGGESTIONS_ACTIONS_EXTENDED },
            )
        }
        Spacer(modifier = Modifier.size(82.dp))
    }
}
