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

package com.goodwy.keyboard.app.devtools

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.PreferenceRow
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.extensionManager
import com.goodwy.keyboard.ime.dictionary.DictionaryManager
import com.goodwy.keyboard.ime.dictionary.FlorisUserDictionaryDatabase
import com.goodwy.keyboard.lib.compose.FlorisConfirmDeleteDialog
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.lib.android.AndroidSettings
import com.goodwy.lib.android.showLongToast
import com.goodwy.lib.android.AndroidVersion
import dev.patrickgold.jetpref.datastore.model.observeAsState

class DebugOnPurposeCrashException : Exception(
    "Success! The app crashed purposely to display this beautiful screen we all love :)"
)

@Composable
fun DevtoolsScreen() = FlorisScreen {
    title = stringRes(R.string.devtools__title)
    previewFieldVisible = true

    val context = LocalContext.current
    val navController = LocalNavController.current
    val extensionManager by context.extensionManager()

    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    content {
        PreferenceGroupCard {
            SwitchPreferenceRow(
                prefs.devtools.enabled,
                title = stringRes(R.string.devtools__enabled__label),
                summary = stringRes(R.string.devtools__enabled__summary),
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.devtools__title)) {
            SwitchPreferenceRow(
                prefs.devtools.showPrimaryClip,
                title = stringRes(R.string.devtools__show_primary_clip__label),
                summary = stringRes(R.string.devtools__show_primary_clip__summary),
                enabledIf = { prefs.devtools.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.devtools.showInputStateOverlay,
                title = stringRes(R.string.devtools__show_input_state_overlay__label),
                summary = stringRes(R.string.devtools__show_input_state_overlay__summary),
                enabledIf = { prefs.devtools.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.devtools.showSpellingOverlay,
                title = stringRes(R.string.devtools__show_spelling_overlay__label),
                summary = stringRes(R.string.devtools__show_spelling_overlay__summary),
                enabledIf = { prefs.devtools.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.devtools.showInlineAutofillOverlay,
                title = stringRes(R.string.devtools__show_inline_autofill_overlay__label),
                summary = stringRes(R.string.devtools__show_inline_autofill_overlay__summary),
                enabledIf = { prefs.devtools.enabled isEqualTo true },
                visibleIf = { AndroidVersion.ATLEAST_API30_R },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.devtools.showKeyTouchBoundaries,
                title = stringRes(R.string.devtools__show_key_touch_boundaries__label),
                summary = stringRes(R.string.devtools__show_key_touch_boundaries__summary),
                enabledIf = { prefs.devtools.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.devtools.showDragAndDropHelpers,
                title = stringRes(R.string.devtools__show_drag_and_drop_helpers__label),
                summary = stringRes(R.string.devtools__show_drag_and_drop_helpers__summary),
                enabledIf = { prefs.devtools.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.devtools__clear_udm_internal_database__label),
                summary = stringRes(R.string.devtools__clear_udm_internal_database__summary),
                onClick = { setShowDialog(true) },
                enabledIf = { prefs.devtools.enabled isEqualTo true },
                endIcon = Icons.Rounded.CleaningServices,
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.devtools__reset_flag__label, "flag_name" to "isImeSetUp"),
                summary = stringRes(R.string.devtools__reset_flag_is_ime_set_up__summary),
                onClick = { prefs.internal.isImeSetUp.set(false) },
                enabledIf = { prefs.devtools.enabled isEqualTo true },
                endIcon = Icons.Rounded.RestartAlt,
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.devtools__test_crash_report__label),
                summary = stringRes(R.string.devtools__test_crash_report__summary),
                onClick = { throw DebugOnPurposeCrashException() },
                enabledIf = { prefs.devtools.enabled isEqualTo true },
                endIcon = Icons.Rounded.BugReport,
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = "Debug log",
                summary = "View and export the debug log",
                onClick = { navController.navigate(Routes.Devtools.ExportDebugLog) },
                enabledIf = { prefs.devtools.enabled isEqualTo true },
                endIcon = Icons.Rounded.Code,
            )
        }

        PreferenceGroupCard(title = stringRes(R.string.devtools__group_android__title)) {
            PreferenceRow(
                title = stringRes(R.string.devtools__android_settings_global__title),
                onClick = {
                    navController.navigate(
                        Routes.Devtools.AndroidSettings(AndroidSettings.Global.groupId)
                    )
                },
                enabledIf = { prefs.devtools.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.devtools__android_settings_secure__title),
                onClick = {
                    navController.navigate(
                        Routes.Devtools.AndroidSettings(AndroidSettings.Secure.groupId)
                    )
                },
                enabledIf = { prefs.devtools.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.devtools__android_settings_system__title),
                onClick = {
                    navController.navigate(
                        Routes.Devtools.AndroidSettings(AndroidSettings.System.groupId)
                    )
                },
                enabledIf = { prefs.devtools.enabled isEqualTo true },
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.devtools__android_locales__title),
                onClick = { navController.navigate(Routes.Devtools.AndroidLocales) },
                enabledIf = { prefs.devtools.enabled isEqualTo true },
            )
        }

        PreferenceGroupCard(title = "prefs.internal.version*") {
            val versionOnInstall by prefs.internal.versionOnInstall.observeAsState()
            PreferenceRow(
                title = "prefs.internal.versionOnInstall",
                summary = versionOnInstall,
                showEndIcon = false,
            )
            DividerRow(start = 16.dp)
            val versionLastUse by prefs.internal.versionLastUse.observeAsState()
            PreferenceRow(
                title = "prefs.internal.versionLastUse",
                summary = versionLastUse,
                showEndIcon = false,
            )
            DividerRow(start = 16.dp)
            val versionLastChangelog by prefs.internal.versionLastChangelog.observeAsState()
            PreferenceRow(
                title = "prefs.internal.versionLastChangelog",
                summary = versionLastChangelog,
                showEndIcon = false,
            )
        }

        PreferenceGroupCard(title = "ExtensionManager index paths") {
            PreferenceRow(
                title = "keyboardExtensions",
                summary = extensionManager.keyboardExtensions.internalModuleDir.absolutePath,
                onClick = {
                    context.showLongToast(extensionManager.keyboardExtensions.internalModuleDir.absolutePath)
                },
                endIcon = Icons.Rounded.ContentCopy,
            )
            PreferenceRow(
                title = "themes",
                summary = extensionManager.themes.internalModuleDir.absolutePath,
                onClick = {
                    context.showLongToast(extensionManager.themes.internalModuleDir.absolutePath)
                },
                endIcon = Icons.Rounded.ContentCopy,
            )
        }
        Spacer(modifier = Modifier.size(32.dp))

        if (showDialog) {
            FlorisConfirmDeleteDialog(
                onConfirm = {
                    DictionaryManager.default().let {
                        it.loadUserDictionariesIfNecessary()
                        it.florisUserDictionaryDao()?.deleteAll()
                    }
                    setShowDialog(false)
                },
                onDismiss = { setShowDialog(false) },
                what = FlorisUserDictionaryDatabase.DB_FILE_NAME,
            )
        }
    }
}
