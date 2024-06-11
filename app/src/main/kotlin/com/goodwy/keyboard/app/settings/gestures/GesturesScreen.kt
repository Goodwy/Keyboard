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

package com.goodwy.keyboard.app.settings.gestures

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
import com.goodwy.keyboard.ime.text.gestures.SwipeAction
import com.goodwy.keyboard.lib.compose.FlorisInfoCard
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun GesturesScreen() = FlorisScreen {
    title = stringRes(R.string.settings__gestures__title)
    previewFieldVisible = true

    content {
        FlorisInfoCard(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
            text = """
                Glide typing is currently not available and will be re-implemented from the ground up with word suggestions & the new keyboard layout engine. DO NOT file an issue for this missing functionality.
            """.trimIndent()
        )

        /*PreferenceGroupCard(title = stringRes(R.string.pref__glide__title)) {
            SwitchPreferenceRow(
                prefs.glide.enabled,
                title = stringRes(R.string.pref__glide__enabled__label),
                summary = stringRes(R.string.pref__glide__enabled__summary),
            )
            SwitchPreferenceRow(
                prefs.glide.showTrail,
                title = stringRes(R.string.pref__glide__show_trail__label),
                summary = stringRes(R.string.pref__glide__show_trail__summary),
                enabledIf = { prefs.glide.enabled isEqualTo true },
            )
            DialogSliderPreferenceRow(
                prefs.glide.trailDuration,
                title = stringRes(R.string.pref__glide_trail_fade_duration),
                valueLabel = { stringRes(R.string.unit__milliseconds__symbol, "v" to it) },
                min = 0,
                max = 500,
                stepIncrement = 10,
                enabledIf = { prefs.glide.enabled isEqualTo true && prefs.glide.showTrail isEqualTo true },
            )
            SwitchPreferenceRow(
                prefs.glide.showPreview,
                title = stringRes(R.string.pref__glide__show_preview),
                summary = "Word suggestions must be enabled for this to take effect!",
                enabledIf = { prefs.glide.enabled isEqualTo true },
            )
            DialogSliderPreferenceRow(
                prefs.glide.previewRefreshDelay,
                title = stringRes(R.string.pref__glide_preview_refresh_delay),
                valueLabel = { stringRes(R.string.unit__milliseconds__symbol, "v" to it) },
                min = 50,
                max = 500,
                stepIncrement = 25,
                enabledIf = { prefs.glide.enabled isEqualTo true && prefs.glide.showPreview isEqualTo true },
            )
            SwitchPreferenceRow(
                prefs.glide.immediateBackspaceDeletesWord,
                title = stringRes(R.string.pref__glide__immediate_backspace_deletes_word__label),
                summary = stringRes(R.string.pref__glide__immediate_backspace_deletes_word__summary),
                enabledIf = { prefs.glide.enabled isEqualTo true },
            )
        }*/

        PreferenceGroupCard(title = stringRes(R.string.pref__gestures__general_title)) {
            ListPreferenceRow(
                prefs.gestures.swipeUp,
                title = stringRes(R.string.pref__gestures__swipe_up__label),
                entries = SwipeAction.generalListEntries(),
                enabledIf = { prefs.glide.enabled isEqualTo false },
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.gestures.swipeDown,
                title = stringRes(R.string.pref__gestures__swipe_down__label),
                entries = SwipeAction.generalListEntries(),
                enabledIf = { prefs.glide.enabled isEqualTo false },
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.gestures.swipeLeft,
                title = stringRes(R.string.pref__gestures__swipe_left__label),
                entries = SwipeAction.generalListEntries(),
                enabledIf = { prefs.glide.enabled isEqualTo false },
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.gestures.swipeRight,
                title = stringRes(R.string.pref__gestures__swipe_right__label),
                entries = SwipeAction.generalListEntries(),
                enabledIf = { prefs.glide.enabled isEqualTo false },
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.pref__gestures__space_bar_title)) {
            ListPreferenceRow(
                prefs.gestures.spaceBarLongPress,
                title = stringRes(R.string.pref__gestures__space_bar_long_press__label),
                entries = SwipeAction.generalListEntries(),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.gestures.spaceBarSwipeUp,
                title = stringRes(R.string.pref__gestures__space_bar_swipe_up__label),
                entries = SwipeAction.generalListEntries(),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.gestures.spaceBarSwipeLeft,
                title = stringRes(R.string.pref__gestures__space_bar_swipe_left__label),
                entries = SwipeAction.generalListEntries(),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.gestures.spaceBarSwipeRight,
                title = stringRes(R.string.pref__gestures__space_bar_swipe_right__label),
                entries = SwipeAction.generalListEntries(),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.gestures.spaceBarSwipeDown,
                title = stringRes(com.goodwy.keyboard.strings.R.string.pref__gestures__space_bar_swipe_down__label_g),
                entries = SwipeAction.generalListEntries(),
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.gestures.useHideLabelWhenMoveCursor,
                title = stringRes(com.goodwy.keyboard.strings.R.string.pref__gestures__use_hide_label_when_move_cursor__label),
                enabledIf = { prefs.gestures.spaceBarSwipeUp isEqualTo SwipeAction.MOVE_CURSOR_UP && prefs.gestures.spaceBarSwipeLeft isEqualTo SwipeAction.MOVE_CURSOR_LEFT
                    && prefs.gestures.spaceBarSwipeRight isEqualTo SwipeAction.MOVE_CURSOR_RIGHT && prefs.gestures.spaceBarSwipeDown isEqualTo SwipeAction.MOVE_CURSOR_DOWN},
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.pref__gestures__other_title)) {
            ListPreferenceRow(
                prefs.gestures.deleteKeySwipeLeft,
                title = stringRes(R.string.pref__gestures__delete_key_swipe_left__label),
                entries = SwipeAction.deleteSwipeListEntries(),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.gestures.deleteKeyLongPress,
                title = stringRes(R.string.pref__gestures__delete_key_long_press__label),
                entries = SwipeAction.deleteLongPressListEntries(),
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.gestures.swipeVelocityThreshold,
                title = stringRes(R.string.pref__gestures__swipe_velocity_threshold__label),
                valueLabel = { stringRes(R.string.unit__display_pixel_per_seconds__symbol, "v" to it) },
                min = 400,
                max = 4000,
                stepIncrement = 100,
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.gestures.swipeDistanceThreshold,
                title = stringRes(R.string.pref__gestures__swipe_distance_threshold__label),
                valueLabel = { stringRes(R.string.unit__display_pixel__symbol, "v" to it) },
                min = 12,
                max = 72,
                stepIncrement = 1,
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
    }
}
