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

package com.goodwy.keyboard.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import com.goodwy.keyboard.BuildConfig
import com.goodwy.keyboard.app.settings.purchase.Product
import com.goodwy.keyboard.app.settings.theme.DisplayColorsAs
import com.goodwy.keyboard.app.settings.theme.DisplayKbdAfterDialogs
import com.goodwy.keyboard.app.setup.NotificationPermissionState
import com.goodwy.keyboard.ime.core.DisplayLanguageNamesIn
import com.goodwy.keyboard.ime.core.Subtype
import com.goodwy.keyboard.ime.input.CapitalizationBehavior
import com.goodwy.keyboard.ime.input.HapticVibrationMode
import com.goodwy.keyboard.ime.input.InputFeedbackActivationMode
import com.goodwy.keyboard.ime.input.InputFeedbackSoundEffect
import com.goodwy.keyboard.ime.keyboard.IncognitoMode
import com.goodwy.keyboard.ime.keyboard.SpaceBarMode
import com.goodwy.keyboard.ime.landscapeinput.LandscapeInputUiMode
import com.goodwy.keyboard.ime.media.emoji.EmojiCategory
import com.goodwy.keyboard.ime.media.emoji.EmojiHairStyle
import com.goodwy.keyboard.ime.media.emoji.EmojiHistory
import com.goodwy.keyboard.ime.media.emoji.EmojiSkinTone
import com.goodwy.keyboard.ime.media.emoji.EmojiSuggestionType
import com.goodwy.keyboard.ime.nlp.SpellingLanguageMode
import com.goodwy.keyboard.ime.onehanded.OneHandedMode
import com.goodwy.keyboard.ime.smartbar.IncognitoDisplayMode
import com.goodwy.keyboard.ime.smartbar.CandidatesDisplayMode
import com.goodwy.keyboard.ime.smartbar.ExtendedActionsPlacement
import com.goodwy.keyboard.ime.smartbar.SmartbarLayout
import com.goodwy.keyboard.ime.smartbar.quickaction.QuickActionArrangement
import com.goodwy.keyboard.ime.text.gestures.SwipeAction
import com.goodwy.keyboard.ime.text.key.KeyHintConfiguration
import com.goodwy.keyboard.ime.text.key.KeyHintMode
import com.goodwy.keyboard.ime.text.key.UtilityKeyAction
import com.goodwy.keyboard.ime.theme.ThemeMode
import com.goodwy.keyboard.ime.theme.extCoreTheme
import com.goodwy.keyboard.lib.ext.ExtensionComponentName
import com.goodwy.keyboard.lib.observeAsTransformingState
import com.goodwy.keyboard.lib.util.VersionName
import com.goodwy.lib.android.isOrientationPortrait
import com.goodwy.lib.snygg.SnyggLevel
import dev.patrickgold.jetpref.datastore.JetPref
import dev.patrickgold.jetpref.datastore.model.PreferenceMigrationEntry
import dev.patrickgold.jetpref.datastore.model.PreferenceModel
import dev.patrickgold.jetpref.datastore.model.PreferenceType
import dev.patrickgold.jetpref.datastore.model.observeAsState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun florisPreferenceModel() = JetPref.getOrCreatePreferenceModel(AppPrefs::class, ::AppPrefs)

class AppPrefs : PreferenceModel("florisboard-app-prefs") {
    val advanced = Advanced()
    inner class Advanced {
        val settingsTheme = enum(
            key = "advanced__settings_theme",
            default = AppTheme.AUTO,
        )
        val useMaterialYou = boolean(
            key = "advanced__use_material_you",
            default = true,
        )
        val settingsLanguage = string(
            key = "advanced__settings_language",
            default = "auto",
        )
        val showAppIcon = boolean(
            key = "advanced__show_app_icon",
            default = true,
        )
        val incognitoMode = enum(
            key = "advanced__incognito_mode",
            default = IncognitoMode.DYNAMIC_ON_OFF,
        )
        // Internal pref
        val forceIncognitoModeFromDynamic = boolean(
            key = "advanced__force_incognito_mode_from_dynamic",
            default = false,
        )
    }

    val clipboard = Clipboard()
    inner class Clipboard {
        val useInternalClipboard = boolean(
            key = "clipboard__use_internal_clipboard",
            default = false,
        )
        val syncToFloris = boolean(
            key = "clipboard__sync_to_floris",
            default = true,
        )
        val syncToSystem = boolean(
            key = "clipboard__sync_to_system",
            default = false,
        )
        val historyEnabled = boolean(
            key = "clipboard__history_enabled",
            default = false,
        )
        val cleanUpOld = boolean(
            key = "clipboard__clean_up_old",
            default = false,
        )
        val cleanUpAfter = int(
            key = "clipboard__clean_up_after",
            default = 20,
        )
        val autoCleanSensitive = boolean(
            key = "clipboard__auto_clean_sensitive",
            default = false,
        )
        val autoCleanSensitiveAfter = int(
            key = "clipboard__auto_clean_sensitive_after",
            default = 20,
        )
        val limitHistorySize = boolean(
            key = "clipboard__limit_history_size",
            default = true,
        )
        val maxHistorySize = int(
            key = "clipboard__max_history_size",
            default = 20,
        )
        val clearPrimaryClipDeletesLastItem = boolean(
            key = "clipboard__clear_primary_clip_deletes_last_item",
            default = true,
        )
    }

    val correction = Correction()
    inner class Correction {
        val autoCapitalization = boolean(
            key = "correction__auto_capitalization",
            default = true,
        )
        val autoSpacePunctuation = boolean(
            key = "correction__auto_space_punctuation",
            default = false,
        )
        val doubleSpacePeriod = boolean(
            key = "correction__double_space_period",
            default = true,
        )
        val rememberCapsLockState = boolean(
            key = "correction__remember_caps_lock_state",
            default = false,
        )
    }

    val devtools = Devtools()
    inner class Devtools {
        val enabled = boolean(
            key = "devtools__enabled",
            default = false,
        )
        val showPrimaryClip = boolean(
            key = "devtools__show_primary_clip",
            default = false,
        )
        val showInputStateOverlay = boolean(
            key = "devtools__show_input_state_overlay",
            default = false,
        )
        val showSpellingOverlay = boolean(
            key = "devtools__show_spelling_overlay",
            default = false,
        )
        val showInlineAutofillOverlay = boolean(
            key = "devtools__show_inline_autofill_overlay",
            default = false,
        )
        val showKeyTouchBoundaries = boolean(
            key = "devtools__show_touch_boundaries",
            default = false,
        )
        val showDragAndDropHelpers = boolean(
            key = "devtools__show_drag_and_drop_helpers",
            default = false,
        )
        //Goodwy
        val showDevtools = boolean(
            key = "devtools__show_devtools",
            default = false,
        )
    }

    val dictionary = Dictionary()
    inner class Dictionary {
        val enableSystemUserDictionary = boolean(
            key = "suggestion__enable_system_user_dictionary",
            default = true,
        )
        val enableFlorisUserDictionary = boolean(
            key = "suggestion__enable_floris_user_dictionary",
            default = true,
        )
    }

    val emoji = Emoji()
    inner class Emoji {
        val preferredSkinTone = enum(
            key = "emoji__preferred_skin_tone",
            default = EmojiSkinTone.DEFAULT,
        )
        val preferredHairStyle = enum(
            key = "emoji__preferred_hair_style",
            default = EmojiHairStyle.DEFAULT,
        )
        val historyEnabled = boolean(
            key = "emoji__history_enabled",
            default = true,
        )
        val historyData = custom(
            key = "emoji__history_data",
            default = EmojiHistory.Empty,
            serializer = EmojiHistory.Serializer,
        )
        val historyPinnedUpdateStrategy = enum(
            key = "emoji__history_pinned_update_strategy",
            default = EmojiHistory.UpdateStrategy.MANUAL_SORT_PREPEND,
        )
        val historyPinnedMaxSize = int(
            key = "emoji__history_pinned_max_size",
            default = EmojiHistory.MaxSizeUnlimited,
        )
        val historyRecentUpdateStrategy = enum(
            key = "emoji__history_recent_update_strategy",
            default = EmojiHistory.UpdateStrategy.AUTO_SORT_PREPEND,
        )
        val historyRecentMaxSize = int(
            key = "emoji__history_recent_max_size",
            default = 90,
        )
        val suggestionEnabled = boolean(
            key = "emoji__suggestion_enabled",
            default = true,
        )
        val suggestionType = enum(
            key = "emoji__suggestion_type",
            default = EmojiSuggestionType.INLINE_TEXT,
        )
        val suggestionUpdateHistory = boolean(
            key = "emoji__suggestion_update_history",
            default = true,
        )
        val suggestionCandidateShowName = boolean(
            key = "emoji__suggestion_candidate_show_name",
            default = false,
        )
        val suggestionQueryMinLength = int(
            key = "emoji__suggestion_query_min_length",
            default = 3,
        )
        val suggestionCandidateMaxCount = int(
            key = "emoji__suggestion_candidate_max_count",
            default = 5,
        )
        //Goodwy
        val emojiUseLastTab = boolean(
            key = "media__emoji_last_tab",
            default = true
        )
        val emojiLastTab = enum(
            key = "media__emoji_last_tab",
            default = EmojiCategory.SMILEYS_EMOTION
        )
        val emojiDefaultTab = enum(
            key = "media__emoji_default_tab",
            default = EmojiCategory.RECENTLY_USED,
        )
        val emojiUseHorizontalGrid = boolean(
            key = "media__emoji_use_horizontal_grid",
            default = false
        )
    }

    val gestures = Gestures()
    inner class Gestures {
        val swipeUp = enum(
            key = "gestures__swipe_up",
            default = SwipeAction.SHIFT,
        )
        val swipeDown = enum(
            key = "gestures__swipe_down",
            default = SwipeAction.HIDE_KEYBOARD,
        )
        val swipeLeft = enum(
            key = "gestures__swipe_left",
            default = SwipeAction.SWITCH_TO_NEXT_SUBTYPE,
        )
        val swipeRight = enum(
            key = "gestures__swipe_right",
            default = SwipeAction.SWITCH_TO_PREV_SUBTYPE,
        )
        val spaceBarSwipeUp = enum(
            key = "gestures__space_bar_swipe_up",
            default = SwipeAction.MOVE_CURSOR_UP,
        )
        val spaceBarSwipeLeft = enum(
            key = "gestures__space_bar_swipe_left",
            default = SwipeAction.MOVE_CURSOR_LEFT,
        )
        val spaceBarSwipeRight = enum(
            key = "gestures__space_bar_swipe_right",
            default = SwipeAction.MOVE_CURSOR_RIGHT,
        )
        val spaceBarLongPress = enum(
            key = "gestures__space_bar_long_press",
            default = SwipeAction.SHOW_INPUT_METHOD_PICKER,
        )
        val deleteKeySwipeLeft = enum(
            key = "gestures__delete_key_swipe_left",
            default = SwipeAction.DELETE_CHARACTERS_PRECISELY,
        )
        val deleteKeyLongPress = enum(
            key = "gestures__delete_key_long_press",
            default = SwipeAction.DELETE_CHARACTER,
        )
        val swipeDistanceThreshold = int(
            key = "gestures__swipe_distance_threshold",
            default = 32,
        )
        val swipeVelocityThreshold = int(
            key = "gestures__swipe_velocity_threshold",
            default = 1900,
        )
        //Goodwy
        val spaceBarSwipeDown = enum(
            key = "gestures__space_bar_swipe_down",
            default = SwipeAction.MOVE_CURSOR_DOWN,
        )
        // Moving the cursor with the spacebar hides all labels
        val useHideLabelWhenMoveCursor = boolean(
            key = "keyboard__use_hide_label_when_move_cursor",
            default = true,
        )
        val hideAllLabel = boolean(
            key = "keyboard__hide_all_label",
            default = false,
        )
    }

    val glide = Glide()
    inner class Glide {
        val enabled = boolean(
            key = "glide__enabled",
            default = false,
        )
        val showTrail = boolean(
            key = "glide__show_trail",
            default = true,
        )
        val trailDuration = int(
            key = "glide__trail_fade_duration",
            default = 200,
        )
        val showPreview = boolean(
            key = "glide__show_preview",
            default = true,
        )
        val previewRefreshDelay = int(
            key = "glide__preview_refresh_delay",
            default = 150,
        )
        val immediateBackspaceDeletesWord = boolean(
            key = "glide__immediate_backspace_deletes_word",
            default = true,
        )
    }

    val inputFeedback = InputFeedback()
    inner class InputFeedback {
        val audioEnabled = boolean(
            key = "input_feedback__audio_enabled",
            default = true,
        )
        val audioActivationMode = enum(
            key = "input_feedback__audio_activation_mode",
            default = InputFeedbackActivationMode.IGNORE_SYSTEM_SETTINGS,
        )
        val audioVolume = int(
            key = "input_feedback__audio_volume",
            default = 50,
        )
        val audioFeatKeyPress = boolean(
            key = "input_feedback__audio_feat_key_press",
            default = true,
        )
        val audioFeatKeyLongPress = boolean(
            key = "input_feedback__audio_feat_key_long_press",
            default = false,
        )
        val audioFeatKeyRepeatedAction = boolean(
            key = "input_feedback__audio_feat_key_repeated_action",
            default = false,
        )
        val audioFeatGestureSwipe = boolean(
            key = "input_feedback__audio_feat_gesture_swipe",
            default = false,
        )
        val audioFeatGestureMovingSwipe = boolean(
            key = "input_feedback__audio_feat_gesture_moving_swipe",
            default = false,
        )

        val hapticEnabled = boolean(
            key = "input_feedback__haptic_enabled",
            default = true,
        )
        val hapticActivationMode = enum(
            key = "input_feedback__haptic_activation_mode",
            default = InputFeedbackActivationMode.IGNORE_SYSTEM_SETTINGS,
        )
        val hapticVibrationMode = enum(
            key = "input_feedback__haptic_vibration_mode",
            default = HapticVibrationMode.USE_VIBRATOR_DIRECTLY,
        )
        val hapticVibrationDuration = int(
            key = "input_feedback__haptic_vibration_duration",
            default = 50,
        )
        val hapticVibrationStrength = int(
            key = "input_feedback__haptic_vibration_strength",
            default = 50,
        )
        val hapticFeatKeyPress = boolean(
            key = "input_feedback__haptic_feat_key_press",
            default = true,
        )
        val hapticFeatKeyLongPress = boolean(
            key = "input_feedback__haptic_feat_key_long_press",
            default = false,
        )
        val hapticFeatKeyRepeatedAction = boolean(
            key = "input_feedback__haptic_feat_key_repeated_action",
            default = true,
        )
        val hapticFeatGestureSwipe = boolean(
            key = "input_feedback__haptic_feat_gesture_swipe",
            default = false,
        )
        val hapticFeatGestureMovingSwipe = boolean(
            key = "input_feedback__haptic_feat_gesture_moving_swipe",
            default = true,
        )
        //Goodwy
        val soundEffect = enum(
            key = "input_feedback__sound_effect",
            default = InputFeedbackSoundEffect.KEY_CLICK,
        )
    }

    val internal = Internal()
    inner class Internal {
        val homeIsBetaToolboxCollapsed = boolean(
            key = "internal__home_is_beta_toolbox_collapsed_040a01",
            default = false,
        )
        val isImeSetUp = boolean(
            key = "internal__is_ime_set_up",
            default = false,
        )
        val versionOnInstall = string(
            key = "internal__version_on_install",
            default = VersionName.DEFAULT_RAW,
        )
        val versionLastUse = string(
            key = "internal__version_last_use",
            default = VersionName.DEFAULT_RAW,
        )
        val versionLastChangelog = string(
            key = "internal__version_last_changelog",
            default = VersionName.DEFAULT_RAW,
        )
        val notificationPermissionState = enum(
            key = "internal__notification_permission_state",
            default = NotificationPermissionState.NOT_SET,
        )
        val previewKeyboardType = int(
            key = "internal__preview_keyboard_type",
            default = 1
        )
    }

    val keyboard = Keyboard()
    inner class Keyboard {
        val numberRow = boolean(
            key = "keyboard__number_row",
            default = false,
        )
        val hintedNumberRowEnabled = boolean(
            key = "keyboard__hinted_number_row_enabled",
            default = true,
        )
        val hintedNumberRowMode = enum(
            key = "keyboard__hinted_number_row_mode",
            default = KeyHintMode.SMART_PRIORITY,
        )
        val hintedSymbolsEnabled = boolean(
            key = "keyboard__hinted_symbols_enabled",
            default = true,
        )
        val hintedSymbolsMode = enum(
            key = "keyboard__hinted_symbols_mode",
            default = KeyHintMode.SMART_PRIORITY,
        )
        val utilityKeyEnabled = boolean(
            key = "keyboard__utility_key_enabled",
            default = true,
        )
        val utilityKeyAction = enum(
            key = "keyboard__utility_key_action",
            default = UtilityKeyAction.DYNAMIC_SWITCH_LANGUAGE_EMOJIS,
        )
        val spaceBarMode = enum(
            key = "keyboard__space_bar_display_mode",
            default = SpaceBarMode.CURRENT_LANGUAGE,
        )
        val capitalizationBehavior = enum(
            key = "keyboard__capitalization_behavior",
            default = CapitalizationBehavior.CAPSLOCK_BY_DOUBLE_TAP,
        )
        val fontSizeMultiplierPortrait = int(
            key = "keyboard__font_size_multiplier_portrait",
            default = 100,
        )
        val fontSizeMultiplierLandscape = int(
            key = "keyboard__font_size_multiplier_landscape",
            default = 100,
        )
        val oneHandedMode = enum(
            key = "keyboard__one_handed_mode",
            default = OneHandedMode.OFF,
        )
        val oneHandedModeScaleFactor = int(
            key = "keyboard__one_handed_mode_scale_factor",
            default = 87,
        )
        val landscapeInputUiMode = enum(
            key = "keyboard__landscape_input_ui_mode",
            default = LandscapeInputUiMode.DYNAMICALLY_SHOW,
        )
        val heightFactorPortrait = int(
            key = "keyboard__height_factor_portrait",
            default = 100,
        )
        val heightFactorLandscape = int(
            key = "keyboard__height_factor_landscape",
            default = 100,
        )
        val keySpacingVertical = float(
            key = "keyboard__key_spacing_vertical",
            default = 5.0f,
        )
        val keySpacingHorizontal = float(
            key = "keyboard__key_spacing_horizontal",
            default = 2.0f,
        )
        val bottomOffsetPortrait = int(
            key = "keyboard__bottom_offset_portrait",
            default = 0,
        )
        val bottomOffsetLandscape = int(
            key = "keyboard__bottom_offset_landscape",
            default = 0,
        )
        val popupEnabled = boolean(
            key = "keyboard__popup_enabled",
            default = true,
        )
        val mergeHintPopupsEnabled = boolean(
            key = "keyboard__merge_hint_popups_enabled",
            default = false,
        )
        val longPressDelay = int(
            key = "keyboard__long_press_delay",
            default = 300,
        )
        val spaceBarSwitchesToCharacters = boolean(
            key = "keyboard__space_bar_switches_to_characters",
            default = true,
        )
        val incognitoDisplayMode = enum(
            key = "keyboard__incognito_indicator",
            default = IncognitoDisplayMode.REPLACE_SHARED_ACTIONS_TOGGLE,
        )
        //Goodwy
        val bigEnterButton = boolean(
            key = "keyboard__big_enter_button",
            default = true,
        )
        val commaKeyEnabled = boolean(
            key = "keyboard__comma_key_enabled",
            default = true,
        )
        val dotKeyEnabled = boolean(
            key = "keyboard__dot_key_enabled",
            default = true,
        )
        val bottomPanelMode = boolean(
            key = "keyboard__bottom_panel_enabled",
            default = false,
        )
        val bottomPanelMic = boolean(
            key = "keyboard__bottom_panel_mic_enabled",
            default = true,
        )

        fun keyHintConfiguration(): KeyHintConfiguration {
            return KeyHintConfiguration(
                numberHintMode = when {
                    hintedNumberRowEnabled.get() -> hintedNumberRowMode.get()
                    else -> KeyHintMode.DISABLED
                },
                symbolHintMode = when {
                    hintedSymbolsEnabled.get() -> hintedSymbolsMode.get()
                    else -> KeyHintMode.DISABLED
                },
                mergeHintPopups = mergeHintPopupsEnabled.get(),
            )
        }

        @Composable
        fun fontSizeMultiplier(): Float {
            val configuration = LocalConfiguration.current
            val oneHandedMode by oneHandedMode.observeAsState()
            val oneHandedModeFactor by oneHandedModeScaleFactor.observeAsTransformingState { it / 100.0f }
            val fontSizeMultiplierBase by if (configuration.isOrientationPortrait()) {
                fontSizeMultiplierPortrait
            } else {
                fontSizeMultiplierLandscape
            }.observeAsTransformingState { it / 100.0f }
            val fontSizeMultiplier = fontSizeMultiplierBase * if (oneHandedMode != OneHandedMode.OFF && configuration.isOrientationPortrait()) {
                oneHandedModeFactor
            } else {
                1.0f
            }
            return fontSizeMultiplier
        }
    }

    val localization = Localization()
    inner class Localization {
        val displayLanguageNamesIn = enum(
            key = "localization__display_language_names_in",
            default = DisplayLanguageNamesIn.SYSTEM_LOCALE,
        )
        val activeSubtypeId = long(
            key = "localization__active_subtype_id",
            default = Subtype.DEFAULT.id,
        )
        val subtypes = string(
            key = "localization__subtypes",
            default = "[]",
        )
        //Goodwy
        val switchEmojiWhenChangingLanguage = boolean(
            key = "media__switch_emoji_when_changing_language",
            default = false
        )
    }

    val smartbar = Smartbar()
    inner class Smartbar {
        val enabled = boolean(
            key = "smartbar__enabled",
            default = true,
        )
        val layout = enum(
            key = "smartbar__layout",
            default = SmartbarLayout.SUGGESTIONS_ACTIONS_SHARED,
        )
        val actionArrangement = custom(
            key = "smartbar__action_arrangement",
            default = QuickActionArrangement.Default,
            serializer = QuickActionArrangement.Serializer,
        )
        val flipToggles = boolean(
            key = "smartbar__flip_toggles",
            default = false,
        )
        val sharedActionsExpanded = boolean(
            key = "smartbar__shared_actions_expanded",
            default = false,
        )
        @Deprecated("Always enabled due to UX issues")
        val sharedActionsAutoExpandCollapse = boolean(
            key = "smartbar__shared_actions_auto_expand_collapse",
            default = false,
        )
        val sharedActionsExpandWithAnimation = boolean(
            key = "smartbar__shared_actions_expand_with_animation",
            default = true,
        )
        val extendedActionsExpanded = boolean(
            key = "smartbar__extended_actions_expanded",
            default = false,
        )
        val extendedActionsPlacement = enum(
            key = "smartbar__extended_actions_placement",
            default = ExtendedActionsPlacement.ABOVE_CANDIDATES,
        )
    }

    val spelling = Spelling()
    inner class Spelling {
        val languageMode = enum(
            key = "spelling__language_mode",
            default = SpellingLanguageMode.USE_KEYBOARD_SUBTYPES,
        )
        val useContacts = boolean(
            key = "spelling__use_contacts",
            default = true,
        )
        val useUdmEntries = boolean(
            key = "spelling__use_udm_entries",
            default = true,
        )
    }

    val suggestion = Suggestion()
    inner class Suggestion {
        val api30InlineSuggestionsEnabled = boolean(
            key = "suggestion__api30_inline_suggestions_enabled",
            default = true,
        )
        val enabled = boolean(
            key = "suggestion__enabled",
            default = false,
        )
        val displayMode = enum(
            key = "suggestion__display_mode",
            default = CandidatesDisplayMode.DYNAMIC_SCROLLABLE,
        )
        val blockPossiblyOffensive = boolean(
            key = "suggestion__block_possibly_offensive",
            default = true,
        )
        val clipboardContentEnabled = boolean(
            key = "suggestion__clipboard_content_enabled",
            default = true,
        )
        val clipboardContentTimeout = int(
            key = "suggestion__clipboard_content_timeout",
            default = 60,
        )
    }

    val theme = Theme()
    inner class Theme {
        val mode = enum(
            key = "theme__mode",
            default = ThemeMode.FOLLOW_SYSTEM,
        )
        val dayThemeId = custom(
            key = "theme__day_theme_id",
            default = extCoreTheme("ios_day"),
            serializer = ExtensionComponentName.Serializer,
        )
        val nightThemeId = custom(
            key = "theme__night_theme_id",
            default = extCoreTheme("ios_night"),
            serializer = ExtensionComponentName.Serializer,
        )
        //val sunriseTime = localTime(
        //    key = "theme__sunrise_time",
        //    default = LocalTime.of(6, 0),
        //)
        //val sunsetTime = localTime(
        //    key = "theme__sunset_time",
        //    default = LocalTime.of(18, 0),
        //)
        val editorDisplayColorsAs = enum(
            key = "theme__editor_display_colors_as",
            default = DisplayColorsAs.HEX8,
        )
        val editorDisplayKbdAfterDialogs = enum(
            key = "theme__editor_display_kbd_after_dialogs",
            default = DisplayKbdAfterDialogs.REMEMBER,
        )
        val editorLevel = enum(
            key = "theme__editor_level",
            default = SnyggLevel.ADVANCED,
        )
    }

    override fun migrate(entry: PreferenceMigrationEntry): PreferenceMigrationEntry {
        return when (entry.key) {
            // Migrate enums from their lowercase to uppercase representation
            // Keep migration rule until: 0.5 dev cycle
            "advanced__settings_theme", "gestures__swipe_up", "gestures__swipe_down", "gestures__swipe_left",
            "gestures__swipe_right", "gestures__space_bar_swipe_up", "gestures__space_bar_swipe_left",
            "gestures__space_bar_swipe_right", "gestures__space_bar_long_press", "gestures__delete_key_swipe_left",
            "gestures__delete_key_long_press", "keyboard__hinted_number_row_mode", "keyboard__hinted_symbols_mode",
            "keyboard__utility_key_action", "keyboard__one_handed_mode", "keyboard__landscape_input_ui_mode",
            "localization__display_language_names_in", "smartbar__primary_actions_row_type",
            "smartbar__secondary_actions_placement", "smartbar__secondary_actions_row_type", "spelling__language_mode",
            "suggestion__display_mode", "theme__mode", "theme__editor_display_colors_as",
            "theme__editor_display_kbd_after_dialogs", "theme__editor_level",
            -> {
                entry.transform(rawValue = entry.rawValue.uppercase())
            }

            // Migrate old private mode force flag as this is a sensitive preference
            // Keep migration rule until: 0.5 dev cycle
            "advanced__force_private_mode" -> {
                if (entry.rawValue.toBoolean()) {
                    entry.transform(
                        type = PreferenceType.string(),
                        key = "advanced__incognito_mode",
                        rawValue = IncognitoMode.FORCE_ON.toString(),
                    )
                } else {
                    entry.reset()
                }
            }

            // Migrate media prefs to emoji prefs
            // Keep migration rule until: 0.6 dev cycle
            "media__emoji_recently_used" -> {
                val emojiValues = entry.rawValue.split(";")
                val recent = emojiValues.map {
                    com.goodwy.keyboard.ime.media.emoji.Emoji(it, "", emptyList())
                }
                val data = EmojiHistory(emptyList(), recent)
                entry.transform(key = "emoji__history_data", rawValue = Json.encodeToString(data))
            }
            "media__emoji_recently_used_max_size" -> {
                entry.transform(key = "emoji__history_recent_max_size")
            }
            "media__emoji_preferred_skin_tone" -> {
                entry.transform(
                    key = "emoji__preferred_skin_tone",
                    rawValue = entry.rawValue.uppercase(), // keep until: 0.5 dev cycle
                )
            }
            "media__emoji_preferred_hair_style" -> {
                entry.transform(
                    key = "emoji__preferred_hair_style",
                    rawValue = entry.rawValue.uppercase(), // keep until: 0.5 dev cycle
                )
            }

            // Default: keep entry
            else -> entry.keepAsIs()
        }
    }

    val purchase = Purchase()
    inner class Purchase {
        val isPlayStoreInstalled = boolean(
            key = "purchase__is_play_store_installed",
            default = true,
        )
        val isRuStoreInstalled = boolean(
            key = "purchase__is_ru_store_installed",
            default = false,
        )
        val useGooglePlay = boolean(
            key = "purchase__use_google_play",
            default = true,
        )
        val isPro = boolean(
            key = "purchase__is_pro",
            default = false,
        )
        val isProSubs = boolean(
            key = "purchase__is_pro_subs",
            default = false,
        )
        val isProRustore = boolean(
            key = "purchase__is_pro_rustore",
            default = false,
        )
        val isProNoGP = boolean(
            key = "purchase__is_pro_no_gp",
            default = false,
        )
        val startPurchase = string(
            key = "start_purchase",
            default = "",
        )
        val purchaseErrorRustore = string(
            key = "purchase_error_rustore",
            default = "",
        )
        val products1 = custom(
            key = "purchase__products_1",
            default = Product(BuildConfig.PRODUCT_ID_X1, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val products2 = custom(
            key = "purchase__products_2",
            default = Product(BuildConfig.PRODUCT_ID_X2, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val products3 = custom(
            key = "purchase__products_3",
            default = Product(BuildConfig.PRODUCT_ID_X3, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val products4 = custom(
            key = "purchase__products_4",
            default = Product(BuildConfig.SUBSCRIPTION_ID_X1, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val products5 = custom(
            key = "purchase__products_5",
            default = Product(BuildConfig.SUBSCRIPTION_ID_X2, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val products6 = custom(
            key = "purchase__products_6",
            default = Product(BuildConfig.SUBSCRIPTION_ID_X3, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val products7 = custom(
            key = "purchase__products_7",
            default = Product(BuildConfig.SUBSCRIPTION_YEAR_ID_X1, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val products8 = custom(
            key = "purchase__products_8",
            default = Product(BuildConfig.SUBSCRIPTION_YEAR_ID_X2, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val products9 = custom(
            key = "purchase__products_9",
            default = Product(BuildConfig.SUBSCRIPTION_YEAR_ID_X3, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val productsRustore1 = custom(
            key = "purchase__products_rustore1",
            default = Product(BuildConfig.PRODUCT_ID_X1, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val productsRustore2 = custom(
            key = "purchase__products_rustore2",
            default = Product(BuildConfig.PRODUCT_ID_X2, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val productsRustore3 = custom(
            key = "purchase__products_rustore3",
            default = Product(BuildConfig.PRODUCT_ID_X3, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val productsRustore4 = custom(
            key = "purchase__products_rustore4",
            default = Product(BuildConfig.SUBSCRIPTION_ID_X1, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val productsRustore5 = custom(
            key = "purchase__products_rustore5",
            default = Product(BuildConfig.SUBSCRIPTION_ID_X2, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val productsRustore6 = custom(
            key = "purchase__products_rustore6",
            default = Product(BuildConfig.SUBSCRIPTION_ID_X3, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val productsRustore7 = custom(
            key = "purchase__products_rustore7",
            default = Product(BuildConfig.SUBSCRIPTION_YEAR_ID_X1, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val productsRustore8 = custom(
            key = "purchase__products_rustore8",
            default = Product(BuildConfig.SUBSCRIPTION_YEAR_ID_X2, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
        val productsRustore9 = custom(
            key = "purchase__products_rustore9",
            default = Product(BuildConfig.SUBSCRIPTION_YEAR_ID_X3, "...", purchased = false, valid = false),
            serializer = Product.Serializer,
        )
    }
}
