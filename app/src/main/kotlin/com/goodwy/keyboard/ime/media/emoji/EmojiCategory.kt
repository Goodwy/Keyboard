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
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.EmojiFlags
import androidx.compose.material.icons.rounded.EmojiFoodBeverage
import androidx.compose.material.icons.rounded.EmojiNature
import androidx.compose.material.icons.rounded.EmojiObjects
import androidx.compose.material.icons.rounded.EmojiPeople
import androidx.compose.material.icons.rounded.EmojiSymbols
import androidx.compose.material.icons.rounded.EmojiTransportation
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.ui.graphics.vector.ImageVector

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
            RECENTLY_USED -> Icons.Rounded.Schedule
            SMILEYS_EMOTION -> Icons.Rounded.EmojiEmotions
            PEOPLE_BODY -> Icons.Rounded.EmojiPeople
            ANIMALS_NATURE -> Icons.Rounded.EmojiNature
            FOOD_DRINK -> Icons.Rounded.EmojiFoodBeverage
            TRAVEL_PLACES -> Icons.Rounded.EmojiTransportation
            ACTIVITIES -> Icons.Rounded.EmojiEvents
            OBJECTS -> Icons.Rounded.EmojiObjects
            SYMBOLS -> Icons.Rounded.EmojiSymbols
            FLAGS -> Icons.Rounded.EmojiFlags
        }
    }
}

