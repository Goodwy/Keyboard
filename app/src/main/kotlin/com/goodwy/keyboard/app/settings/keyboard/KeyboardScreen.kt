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

package com.goodwy.keyboard.app.settings.keyboard

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.enumDisplayEntriesOf
import com.goodwy.keyboard.app.settings.DialogSliderPreferenceRow
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.ListPreferenceRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.ime.input.CapitalizationBehavior
import com.goodwy.keyboard.ime.keyboard.SpaceBarMode
import com.goodwy.keyboard.ime.landscapeinput.LandscapeInputUiMode
import com.goodwy.keyboard.ime.onehanded.OneHandedMode
import com.goodwy.keyboard.ime.smartbar.IncognitoDisplayMode
import com.goodwy.keyboard.ime.text.key.KeyHintMode
import com.goodwy.keyboard.ime.text.key.UtilityKeyAction
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun KeyboardScreen() = FlorisScreen {
    title = stringRes(R.string.settings__keyboard__title)
    previewFieldVisible = true

    val navController = LocalNavController.current

    content {
        PreferenceGroupCard(title = stringRes(com.goodwy.keyboard.strings.R.string.settings__keys)) {
            SwitchPreferenceRow(
                prefs.keyboard.numberRow,
                title = stringRes(R.string.pref__keyboard__number_row__label),
                summary = stringRes(R.string.pref__keyboard__number_row__summary),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                listPref = prefs.keyboard.hintedNumberRowMode,
                switchPref = prefs.keyboard.hintedNumberRowEnabled,
                title = stringRes(R.string.pref__keyboard__hinted_number_row_mode__label),
                summarySwitchDisabled = stringRes(R.string.state__disabled),
                entries = enumDisplayEntriesOf(KeyHintMode::class),
                enabledIf = { prefs.keyboard.numberRow.isFalse() }
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                listPref = prefs.keyboard.hintedSymbolsMode,
                switchPref = prefs.keyboard.hintedSymbolsEnabled,
                title = stringRes(R.string.pref__keyboard__hinted_symbols_mode__label),
                summarySwitchDisabled = stringRes(R.string.state__disabled),
                entries = enumDisplayEntriesOf(KeyHintMode::class),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.keyboard.bottomPanelMode,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__language_switch_under_keyboard),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.keyboard.bottomPanelMic,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__mic_under_keyboard),
                enabledIf = { prefs.keyboard.bottomPanelMode isEqualTo true }
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.keyboard.utilityKeyEnabled,
                title = stringRes(R.string.pref__keyboard__utility_key_enabled__label),
                //summary = stringRes(R.string.pref__keyboard__utility_key_enabled__summary),
                summary = stringRes(com.goodwy.keyboard.strings.R.string.settings__by_character_layout),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.keyboard.utilityKeyAction,
                title = stringRes(R.string.pref__keyboard__utility_key_action__label),
                entries = enumDisplayEntriesOf(UtilityKeyAction::class),
                //visibleIf = { prefs.keyboard.utilityKeyEnabled isEqualTo true },
                enabledIf = { prefs.keyboard.utilityKeyEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.keyboard.commaKeyEnabled,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__show_comma_key),
                summary = stringRes(com.goodwy.keyboard.strings.R.string.settings__by_character_layout),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.keyboard.dotKeyEnabled,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__show_dot_key),
                summary = stringRes(com.goodwy.keyboard.strings.R.string.settings__by_character_layout),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.keyboard.bigEnterButton,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__big_enter_key),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.keyboard.spaceBarMode,
                title = stringRes(R.string.pref__keyboard__space_bar_mode__label),
                entries = enumDisplayEntriesOf(SpaceBarMode::class),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.keyboard.capitalizationBehavior,
                title = stringRes(R.string.pref__keyboard__capitalization_behavior__label),
                entries = enumDisplayEntriesOf(CapitalizationBehavior::class),
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                primaryPref = prefs.keyboard.fontSizeMultiplierPortrait,
                secondaryPref = prefs.keyboard.fontSizeMultiplierLandscape,
                title = stringRes(R.string.pref__keyboard__font_size_multiplier__label),
                primaryLabel = stringRes(R.string.screen_orientation__portrait),
                secondaryLabel = stringRes(R.string.screen_orientation__landscape),
                valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it) },
                min = 50,
                max = 150,
                stepIncrement = 5,
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                listPref = prefs.keyboard.incognitoDisplayMode,
                title = stringRes(R.string.pref__keyboard__incognito_indicator__label),
                entries = enumDisplayEntriesOf(IncognitoDisplayMode::class),
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.pref__keyboard__group_layout__label)) {
            ListPreferenceRow(
                prefs.keyboard.oneHandedMode,
                title = stringRes(R.string.pref__keyboard__one_handed_mode__label),
                entries = enumDisplayEntriesOf(OneHandedMode::class),
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.keyboard.oneHandedModeScaleFactor,
                title = stringRes(R.string.pref__keyboard__one_handed_mode_scale_factor__label),
                valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it) },
                min = 70,
                max = 90,
                stepIncrement = 1,
                enabledIf = { prefs.keyboard.oneHandedMode isNotEqualTo OneHandedMode.OFF },
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.keyboard.landscapeInputUiMode,
                title = stringRes(R.string.pref__keyboard__landscape_input_ui_mode__label),
                entries = enumDisplayEntriesOf(LandscapeInputUiMode::class),
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                primaryPref = prefs.keyboard.heightFactorPortrait,
                secondaryPref = prefs.keyboard.heightFactorLandscape,
                title = stringRes(R.string.pref__keyboard__height_factor__label),
                primaryLabel = stringRes(R.string.screen_orientation__portrait),
                secondaryLabel = stringRes(R.string.screen_orientation__landscape),
                valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it) },
                min = 50,
                max = 150,
                stepIncrement = 5,
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                primaryPref = prefs.keyboard.keySpacingVertical,
                secondaryPref = prefs.keyboard.keySpacingHorizontal,
                title = stringRes(R.string.pref__keyboard__key_spacing__label),
                primaryLabel = stringRes(R.string.screen_orientation__vertical),
                secondaryLabel = stringRes(R.string.screen_orientation__horizontal),
                valueLabel = { stringRes(R.string.unit__display_pixel__symbol, "v" to it) },
                min = 0.0f,
                max = 10.0f,
                stepIncrement = 0.5f,
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                primaryPref = prefs.keyboard.bottomOffsetPortrait,
                secondaryPref = prefs.keyboard.bottomOffsetLandscape,
                title = stringRes(R.string.pref__keyboard__bottom_offset__label),
                primaryLabel = stringRes(R.string.screen_orientation__portrait),
                secondaryLabel = stringRes(R.string.screen_orientation__landscape),
                valueLabel = { stringRes(R.string.unit__display_pixel__symbol, "v" to it) },
                min = 0,
                max = 60,
                stepIncrement = 1,
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.pref__keyboard__group_keypress__label)) {
//            PreferenceRow(
//                title = stringRes(R.string.settings__input_feedback__title),
//                onClick = { navController.navigate(Routes.Settings.InputFeedback) },
//            )
//            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.keyboard.popupEnabled,
                title = stringRes(R.string.pref__keyboard__popup_enabled__label),
                summary = stringRes(R.string.pref__keyboard__popup_enabled__summary),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.keyboard.mergeHintPopupsEnabled,
                title = stringRes(R.string.pref__keyboard__merge_hint_popups_enabled__label),
                summary = stringRes(R.string.pref__keyboard__merge_hint_popups_enabled__summary),
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.keyboard.longPressDelay,
                title = stringRes(R.string.pref__keyboard__long_press_delay__label),
                valueLabel = { stringRes(R.string.unit__milliseconds__symbol, "v" to it) },
                min = 100,
                max = 700,
                stepIncrement = 10,
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.keyboard.spaceBarSwitchesToCharacters,
                title = stringRes(R.string.pref__keyboard__space_bar_switches_to_characters__label),
                summary = stringRes(R.string.pref__keyboard__space_bar_switches_to_characters__summary),
            )
        }
        Spacer(modifier = Modifier.size(82.dp))
    }
}
