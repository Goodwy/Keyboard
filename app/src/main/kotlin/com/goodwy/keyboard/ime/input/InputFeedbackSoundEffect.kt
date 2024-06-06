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

package com.goodwy.keyboard.ime.input

import androidx.compose.runtime.Composable
import com.goodwy.keyboard.R
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.listPrefEntries

enum class InputFeedbackSoundEffect  {
    KEYPRESS_STANDARD,
    KEY_CLICK;

    companion object {
        @Composable
        fun audioListEntries() = listPrefEntries {
            entry(
                key = KEYPRESS_STANDARD,
                label = stringRes(R.string.settings__sound_effect__standard),
            )
            entry(
                key = KEY_CLICK,
                label = stringRes(R.string.settings__sound_effect__clicking),
            )
        }
    }
}
