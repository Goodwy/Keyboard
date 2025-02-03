/*
 * Copyright (C) 2024 Goodwy
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

package com.goodwy.keyboard.app.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluator
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluatorScope
import dev.patrickgold.jetpref.datastore.model.PreferenceModel
import dev.patrickgold.jetpref.datastore.ui.PreferenceUiContent
import dev.patrickgold.jetpref.datastore.ui.PreferenceUiScope

/**
 * Composition local for the global setting if all sub-preference composables should reserve an icon space.
 * This can be overridden for each individual preference composable.
 *
 * @since 0.2.0
 */
val LocalIconSpaceReserved = staticCompositionLocalOf { false }
/**
 * Composition local of the current isEnabled state which applies.
 *
 * @since 0.2.0
 */
val LocalIsPrefEnabled = staticCompositionLocalOf { true }
/**
 * Composition local of the current isVisible state which applies.
 *
 * @since 0.2.0
 */
val LocalIsPrefVisible = staticCompositionLocalOf { true }

//Modified version https://github.com/patrickgold/jetpref/blob/main/datastore-ui/src/main/kotlin/dev/patrickgold/jetpref/datastore/ui/PreferenceUi.kt
/**
 * Material preference group which automatically provides a title UI.
 *
 * @param modifier Modifier to be applied to this group.
 * @param icon The [ImageVector] of the group title.
 * @param iconSpaceReserved If the space at the start of the list item should be reserved (blank
 *  space) if no `icon` is provided. Also acts as a local setting if all sub-preference composables
 *  should reserve an additional space if no icon is specified. It Can be overridden for each
 *  preference composable.
 * @param title The title of this preference group.
 * @param enabledIf Evaluator scope which allows to dynamically decide if this preference layout
 *  should be enabled (true) or disabled (false).
 * @param visibleIf Evaluator scope which allows to dynamically decide if this preference layout
 *  should be visible (true) or hidden (false).
 * @param content The content of this preference group.
 *
 * @since 0.1.0
 */
@Composable
fun <T : PreferenceModel> PreferenceUiScope<T>.PreferenceGroupCard(
    modifier: Modifier = Modifier,
    iconSpaceReserved: Boolean = false, //LocalIconSpaceReserved.current
    title: String? = null,
    paddingTop: Dp = 24.dp,
    enabledIf: PreferenceDataEvaluator = { true },
    visibleIf: PreferenceDataEvaluator = { true },
    content: PreferenceUiContent<T>,
) {
    if (LocalIsPrefVisible.current && visibleIf(PreferenceDataEvaluatorScope)) {
        Column(modifier = modifier) {
            val preferenceScope = PreferenceUiScope(
                prefs = this@PreferenceGroupCard.prefs,
                columnScope = this@Column,
            )

            CompositionLocalProvider(
                LocalIconSpaceReserved provides iconSpaceReserved,
                LocalIsPrefEnabled provides enabledIf(PreferenceDataEvaluatorScope),
                LocalIsPrefVisible provides visibleIf(PreferenceDataEvaluatorScope),
            ) {
                if (title != null) {
                    Spacer(modifier = Modifier.size(16.dp))
                    HeaderRow(title)
                } else Spacer(modifier = Modifier.size(paddingTop))

                Card(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    content(preferenceScope)
                }
            }
        }
    }
}

@Composable
fun HeaderRow(text: String) {
    Text(
        text = text.uppercase(),
        modifier = Modifier.padding(start = 28.dp, top = 12.dp, bottom = 4.dp, end = 28.dp),
        color = MaterialTheme.colorScheme.secondary,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = MaterialTheme.typography.bodyMedium,
    )
}
