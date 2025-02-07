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

package com.goodwy.keyboard.app.settings.dictionary

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.app.settings.DividerRow
import com.goodwy.keyboard.app.settings.PreferenceGroupCard
import com.goodwy.keyboard.app.settings.PreferenceRow
import com.goodwy.keyboard.app.settings.SwitchPreferenceRow
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.Preference
import dev.patrickgold.jetpref.datastore.ui.SwitchPreference

@Composable
fun DictionaryScreen() = FlorisScreen {
    title = stringRes(R.string.settings__dictionary__title)
    previewFieldVisible = true

    val navController = LocalNavController.current

    content {
        PreferenceGroupCard {
            SwitchPreferenceRow(
                prefs.dictionary.enableSystemUserDictionary,
                title = stringRes(R.string.pref__dictionary__enable_system_user_dictionary__label),
                summary = stringRes(R.string.pref__dictionary__enable_system_user_dictionary__summary),
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.pref__dictionary__manage_system_user_dictionary__label),
                summary = stringRes(R.string.pref__dictionary__manage_system_user_dictionary__summary),
                onClick = { navController.navigate(Routes.Settings.UserDictionary(UserDictionaryType.SYSTEM)) },
                enabledIf = { prefs.dictionary.enableSystemUserDictionary isEqualTo true },
            )
            DividerRow(start = 16.dp)
            SwitchPreferenceRow(
                prefs.dictionary.enableFlorisUserDictionary,
                title = stringRes(R.string.pref__dictionary__enable_internal_user_dictionary__label),
                summary = stringRes(R.string.pref__dictionary__enable_internal_user_dictionary__summary),
            )
            DividerRow(start = 16.dp)
            PreferenceRow(
                title = stringRes(R.string.pref__dictionary__manage_floris_user_dictionary__label),
                summary = stringRes(R.string.pref__dictionary__manage_floris_user_dictionary__summary),
                onClick = { navController.navigate(Routes.Settings.UserDictionary(UserDictionaryType.FLORIS)) },
                enabledIf = { prefs.dictionary.enableFlorisUserDictionary isEqualTo true },
            )
        }
        Spacer(modifier = Modifier.size(82.dp))
    }
}
