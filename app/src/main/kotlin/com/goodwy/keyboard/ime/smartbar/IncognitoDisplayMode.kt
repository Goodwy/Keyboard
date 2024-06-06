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

package com.goodwy.keyboard.ime.smartbar

import androidx.compose.runtime.Composable
import com.goodwy.keyboard.R
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.listPrefEntries

enum class IncognitoDisplayMode {
    REPLACE_SHARED_ACTIONS_TOGGLE,
    DISPLAY_BEHIND_KEYBOARD;

    companion object {
        @Composable
        fun listEntries() = listPrefEntries {
            entry(
                key = REPLACE_SHARED_ACTIONS_TOGGLE,
                label = stringRes(id = R.string.enum__incognito_display_mode__replace_shared_actions_toggle),
            )
            entry(
                key = DISPLAY_BEHIND_KEYBOARD,
                label = stringRes(id = R.string.enum__incognito_display_mode__display_behind_keyboard),
            )
        }
    }
}
