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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluator
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluatorScope
import dev.patrickgold.jetpref.datastore.model.PreferenceModel
import dev.patrickgold.jetpref.datastore.ui.PreferenceUiContent
import dev.patrickgold.jetpref.datastore.ui.PreferenceUiScope

@Composable
fun <T : PreferenceModel> PreferenceUiScope<T>.PreferenceGroupCard(
    modifier: Modifier = Modifier,
    iconSpaceReserved: Boolean = false,
    title: String? = null,
    paddingTop: Dp = 24.dp,
    enabledIf: PreferenceDataEvaluator = { true },
    visibleIf: PreferenceDataEvaluator = { true },
    content: PreferenceUiContent<T>,
) {
    val evalScope = PreferenceDataEvaluatorScope.instance()
    if (visibleIf(evalScope)) {
        Column(modifier = modifier) {
            val preferenceScope = PreferenceUiScope(
                prefs = this@PreferenceGroupCard.prefs,
                iconSpaceReserved = iconSpaceReserved,
                enabledIf = { enabledIf(evalScope) },
                visibleIf = { visibleIf(evalScope) },
                columnScope = this@Column,
            )

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
