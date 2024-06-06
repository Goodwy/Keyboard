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

package com.goodwy.keyboard.ime.media.emoji

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.EmojiFlags
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.EmojiSymbols
import androidx.compose.material.icons.filled.EmojiTransportation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.goodwy.keyboard.R
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.listPrefEntries

enum class EmojiCategory(val id: String) {
    RECENTLY_USED("recently_used"),
    SMILEYS_EMOTION("smileys_emotion"),
    PEOPLE_BODY("people_body"),
    ANIMALS_NATURE("animals_nature"),
    FOOD_DRINK("food_drink"),
    TRAVEL_PLACES("travel_places"),
    ACTIVITIES("activities"),
    OBJECTS("objects"),
    SYMBOLS("symbols"),
    FLAGS("flags");

    fun icon(): ImageVector {
        return when (this) {
            RECENTLY_USED -> Icons.Default.Schedule
            SMILEYS_EMOTION -> Icons.Default.EmojiEmotions
            PEOPLE_BODY -> Icons.Default.EmojiPeople
            ANIMALS_NATURE -> Icons.Default.EmojiNature
            FOOD_DRINK -> Icons.Default.EmojiFoodBeverage
            TRAVEL_PLACES -> Icons.Default.EmojiTransportation
            ACTIVITIES -> Icons.Default.EmojiEvents
            OBJECTS -> Icons.Default.EmojiObjects
            SYMBOLS -> Icons.Default.EmojiSymbols
            FLAGS -> Icons.Default.EmojiFlags
        }
    }

    companion object {
        @Composable
        fun listEntries() = listPrefEntries {
            entry(
                key = RECENTLY_USED,
                label = stringRes(R.string.clipboard__group_recent),
            )
            entry(
                key = SMILEYS_EMOTION,
                label = stringRes(R.string.emoji__category__smileys_emotion),
            )
            entry(
                key = PEOPLE_BODY,
                label = stringRes(R.string.emoji__category__people_body),
            )
            entry(
                key = ANIMALS_NATURE,
                label = stringRes(R.string.emoji__category__animals_nature),
            )
            entry(
                key = FOOD_DRINK,
                label = stringRes(R.string.emoji__category__food_drink),
            )
            entry(
                key = TRAVEL_PLACES,
                label = stringRes(R.string.emoji__category__travel_places),
            )
            entry(
                key = ACTIVITIES,
                label = stringRes(R.string.emoji__category__activities),
            )
            entry(
                key = OBJECTS,
                label = stringRes(R.string.emoji__category__objects),
            )
            entry(
                key = SYMBOLS,
                label = stringRes(R.string.emoji__category__symbols),
            )
            entry(
                key = FLAGS,
                label = stringRes(R.string.emoji__category__flags),
            )
        }
    }
}

