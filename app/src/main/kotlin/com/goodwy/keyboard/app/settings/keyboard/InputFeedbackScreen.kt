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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.enumDisplayEntriesOf
import com.goodwy.keyboard.app.settings.DialogSliderPreferenceRow
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.ListPreferenceRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.ime.input.InputFeedbackActivationMode
import com.goodwy.keyboard.ime.input.HapticVibrationMode
import com.goodwy.keyboard.ime.input.InputFeedbackSoundEffect
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.lib.android.AndroidVersion
import com.goodwy.lib.android.systemVibratorOrNull
import com.goodwy.lib.android.vibrate
import dev.patrickgold.jetpref.datastore.ui.DialogSliderPreference
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.jetpref.datastore.ui.ListPreference
import dev.patrickgold.jetpref.datastore.ui.PreferenceGroup
import dev.patrickgold.jetpref.datastore.ui.SwitchPreference

@OptIn(ExperimentalJetPrefDatastoreUi::class)
@Composable
fun InputFeedbackScreen() = FlorisScreen {
    title = stringRes(R.string.settings__input_feedback__title)
    previewFieldVisible = true
    iconSpaceReserved = false

    val context = LocalContext.current
    val vibrator = context.systemVibratorOrNull()

    content {
        PreferenceGroupCard(title = stringRes(R.string.pref__input_feedback__group_audio__label)) {
            ListPreferenceRow(
                listPref = prefs.inputFeedback.audioActivationMode,
                switchPref = prefs.inputFeedback.audioEnabled,
                title = stringRes(R.string.pref__input_feedback__audio_enabled__label),
                summarySwitchDisabled = stringRes(R.string.pref__input_feedback__audio_enabled__summary_disabled),
                entries = enumDisplayEntriesOf(InputFeedbackActivationMode::class, "audio"),
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.inputFeedback.soundEffect,
                title = stringRes(com.goodwy.keyboard.strings.R.string.settings__sound_effect),
                summary = stringRes(com.goodwy.keyboard.strings.R.string.settings__sound_effect_summary),
                enabledIf = { prefs.inputFeedback.audioEnabled isEqualTo true},
                entries = enumDisplayEntriesOf(InputFeedbackSoundEffect::class),
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.inputFeedback.audioVolume,
                title = stringRes(R.string.pref__input_feedback__audio_volume__label),
                valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it) },
                min = 1,
                max = 100,
                stepIncrement = 1,
                enabledIf = { prefs.inputFeedback.audioEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.audioFeatKeyPress,
                title = stringRes(R.string.pref__input_feedback__audio_feat_key_press__label),
                summary = stringRes(R.string.pref__input_feedback__any_feat_key_press__summary),
                enabledIf = { prefs.inputFeedback.audioEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.audioFeatKeyLongPress,
                title = stringRes(R.string.pref__input_feedback__audio_feat_key_long_press__label),
                summary = stringRes(R.string.pref__input_feedback__any_feat_key_long_press__summary),
                enabledIf = { prefs.inputFeedback.audioEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.audioFeatKeyRepeatedAction,
                title = stringRes(R.string.pref__input_feedback__audio_feat_key_repeated_action__label),
                summary = stringRes(R.string.pref__input_feedback__any_feat_key_repeated_action__summary),
                enabledIf = { prefs.inputFeedback.audioEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.audioFeatGestureSwipe,
                title = stringRes(R.string.pref__input_feedback__audio_feat_gesture_swipe__label),
                summary = stringRes(R.string.pref__input_feedback__any_feat_gesture_swipe__summary),
                enabledIf = { prefs.inputFeedback.audioEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.audioFeatGestureMovingSwipe,
                title = stringRes(R.string.pref__input_feedback__audio_feat_gesture_moving_swipe__label),
                summary = stringRes(R.string.pref__input_feedback__audio_feat_gesture_moving_swipe__label),
                enabledIf = { prefs.inputFeedback.audioEnabled isEqualTo true },
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.pref__input_feedback__group_haptic__label)) {
            ListPreferenceRow(
                listPref = prefs.inputFeedback.hapticActivationMode,
                switchPref = prefs.inputFeedback.hapticEnabled,
                title = stringRes(R.string.pref__input_feedback__haptic_enabled__label),
                summarySwitchDisabled = stringRes(R.string.pref__input_feedback__haptic_enabled__summary_disabled),
                entries = enumDisplayEntriesOf(InputFeedbackActivationMode::class, "haptic")
            )
            DividerRow(start = 16.dp)
            ListPreferenceRow(
                prefs.inputFeedback.hapticVibrationMode,
                title = stringRes(R.string.pref__input_feedback__haptic_vibration_mode__label),
                enabledIf = { prefs.inputFeedback.hapticEnabled isEqualTo true },
                entries = enumDisplayEntriesOf(HapticVibrationMode::class),
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.inputFeedback.hapticVibrationDuration,
                title = stringRes(R.string.pref__input_feedback__haptic_vibration_duration__label),
                valueLabel = { stringRes(R.string.unit__milliseconds__symbol, "v" to it) },
                summary = {
                    if (vibrator == null || !vibrator.hasVibrator()) {
                        stringRes(R.string.pref__input_feedback__haptic_vibration_strength__summary_no_vibrator)
                    } else {
                        stringRes(R.string.unit__milliseconds__symbol, "v" to it)
                    }
                },
                oldView = vibrator == null || !vibrator.hasVibrator(),
                min = 1,
                max = 100,
                stepIncrement = 1,
                onPreviewSelectedValue = { duration ->
                    val strength = prefs.inputFeedback.hapticVibrationStrength.get()
                    vibrator?.vibrate(duration, strength)
                },
                enabledIf = {
                    prefs.inputFeedback.hapticEnabled isEqualTo true &&
                        prefs.inputFeedback.hapticVibrationMode isEqualTo HapticVibrationMode.USE_VIBRATOR_DIRECTLY &&
                        vibrator != null && vibrator.hasVibrator()
                },
            )
            DividerRow(start = 16.dp)
            DialogSliderPreferenceRow(
                prefs.inputFeedback.hapticVibrationStrength,
                title = stringRes(R.string.pref__input_feedback__haptic_vibration_strength__label),
                valueLabel = { stringRes(R.string.unit__percent__symbol, "v" to it) },
                summary = { strength ->
                    if (vibrator == null || !vibrator.hasVibrator()) {
                        stringRes(R.string.pref__input_feedback__haptic_vibration_strength__summary_no_vibrator)
                    } else if (AndroidVersion.ATMOST_API25_N_MR1) {
                        stringRes(R.string.pref__input_feedback__haptic_vibration_strength__summary_unsupported_android_version)
                    } else if (!vibrator.hasAmplitudeControl()) {
                        stringRes(R.string.pref__input_feedback__haptic_vibration_strength__summary_no_amplitude_ctrl)
                    } else {
                        stringRes(R.string.unit__percent__symbol, "v" to strength)
                    }
                },
                oldView = (vibrator == null || !vibrator.hasVibrator()) || (AndroidVersion.ATMOST_API25_N_MR1) || (!vibrator.hasAmplitudeControl()),
                min = 1,
                max = 100,
                stepIncrement = 1,
                onPreviewSelectedValue = { strength ->
                    val duration = prefs.inputFeedback.hapticVibrationDuration.get()
                    vibrator?.vibrate(duration, strength)
                },
                enabledIf = {
                    prefs.inputFeedback.hapticEnabled isEqualTo true &&
                        prefs.inputFeedback.hapticVibrationMode isEqualTo HapticVibrationMode.USE_VIBRATOR_DIRECTLY &&
                        vibrator != null && vibrator.hasVibrator() &&
                        AndroidVersion.ATLEAST_API26_O && vibrator.hasAmplitudeControl()
                },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.hapticFeatKeyPress,
                title = stringRes(R.string.pref__input_feedback__haptic_feat_key_press__label),
                summary = stringRes(R.string.pref__input_feedback__any_feat_key_press__summary),
                enabledIf = { prefs.inputFeedback.hapticEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.hapticFeatKeyLongPress,
                title = stringRes(R.string.pref__input_feedback__haptic_feat_key_long_press__label),
                summary = stringRes(R.string.pref__input_feedback__any_feat_key_long_press__summary),
                enabledIf = { prefs.inputFeedback.hapticEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.hapticFeatKeyRepeatedAction,
                title = stringRes(R.string.pref__input_feedback__haptic_feat_key_repeated_action__label),
                summary = stringRes(R.string.pref__input_feedback__any_feat_key_repeated_action__summary),
                enabledIf = { prefs.inputFeedback.hapticEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.hapticFeatGestureSwipe,
                title = stringRes(R.string.pref__input_feedback__haptic_feat_gesture_swipe__label),
                summary = stringRes(R.string.pref__input_feedback__any_feat_gesture_swipe__summary),
                enabledIf = { prefs.inputFeedback.hapticEnabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.inputFeedback.hapticFeatGestureMovingSwipe,
                title = stringRes(R.string.pref__input_feedback__haptic_feat_gesture_moving_swipe__label),
                summary = stringRes(R.string.pref__input_feedback__audio_feat_gesture_moving_swipe__label),
                enabledIf = { prefs.inputFeedback.hapticEnabled isEqualTo true },
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
    }
}
