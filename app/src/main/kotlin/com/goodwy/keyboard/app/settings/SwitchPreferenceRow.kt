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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodwy.keyboard.R
import dev.patrickgold.jetpref.datastore.model.PreferenceData
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluator
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluatorScope
import dev.patrickgold.jetpref.datastore.model.observeAsState

//Modified version https://github.com/patrickgold/jetpref/blob/main/datastore-ui/src/main/kotlin/dev/patrickgold/jetpref/datastore/ui/SwitchPreference.kt
/**
 * Material switch preference which provides a list item with a trailing switch.
 *
 * @param pref The boolean preference data entry from the datastore.
 * @param modifier Modifier to be applied to the underlying list item.
 * @param icon The [ImageVector] of the list entry.
 * @param iconSpaceReserved Whether the icon space should be reserved even if no icon is provided.
 * @param title The title of this preference, shown as the list item primary text (max 1 line).
 * @param summary The summary of this preference, shown as the list item secondary text (max 2 lines).
 * @param summaryOn The summary of this preference if the state is `true`. If this is specified it will override
 *  provided [summary]. Shown as the list item secondary text (max 2 lines).
 * @param summaryOff The summary of this preference if the state is `false`. If this is specified it will override
 *  provided [summary]. Shown as the list item secondary text (max 2 lines).
 * @param enabledIf Evaluator scope which allows to dynamically decide if this preference should be enabled (true) or
 *  disabled (false).
 * @param visibleIf Evaluator scope which allows to dynamically decide if this preference should be visible (true) or
 *  hidden (false).
 *
 * @param iconColor [icon] color.
 *
 * @since 0.1.0
 */
@Composable
fun SwitchPreferenceRow(
    pref: PreferenceData<Boolean>,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSpaceReserved: Boolean = false, //LocalIconSpaceReserved.current
    title: String,
    summary: String? = null,
    summaryOn: String? = null,
    summaryOff: String? = null,
    enabledIf: PreferenceDataEvaluator = { true },
    visibleIf: PreferenceDataEvaluator = { true },
    iconColor: Color = Color.Gray,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 12.dp,
    paddingTop: Dp = 6.dp,
    paddingBottom: Dp = 6.dp,
) {
    val prefValue by pref.observeAsState()

    if (dev.patrickgold.jetpref.datastore.ui.LocalIsPrefVisible.current && visibleIf(PreferenceDataEvaluatorScope)) {
        val isEnabled = LocalIsPrefEnabled.current && enabledIf(PreferenceDataEvaluatorScope)
        CompositionLocalProvider(
            LocalIconSpaceReserved provides iconSpaceReserved,
            LocalIsPrefEnabled provides isEnabled,
            LocalIsPrefVisible provides true,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .heightIn(min = MIN_HEIGHT_ROW.dp)
                    .fillMaxWidth()
                    .alpha(if (isEnabled) 1f else ALPHA_DISABLED)
                    .toggleable(
                        value = prefValue,
                        enabled = isEnabled,
                        role = Role.Switch,
                        onValueChange = { pref.set(it) }
                    )
                    .background(color = MaterialTheme.colorScheme.inverseOnSurface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = paddingStart, end = paddingEnd, top = paddingTop, bottom = paddingBottom),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Icon(
                            modifier = Modifier
                                .height(32.dp)
                                .width(32.dp)
                                .paint(
                                    painter = painterResource(R.drawable.ic_squircle),
                                    contentScale = ContentScale.FillWidth,
                                    colorFilter = ColorFilter.tint(color = iconColor)
                                )
                                .padding(4.dp),
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = title, lineHeight = 15.sp)
                        val subtitle = when {
                            prefValue && summaryOn != null -> summaryOn
                            !prefValue && summaryOff != null -> summaryOff
                            summary != null -> summary
                            else -> null
                        }
                        if (subtitle != null) Text(
                            modifier = Modifier.alpha(0.6f),
                            text = subtitle,
                            fontSize = 12.sp,
                            lineHeight = 12.sp)
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Switch(
                        //modifier = Modifier.size(LocalViewConfiguration.current.minimumTouchTargetSize),
                        checked = prefValue,
                        onCheckedChange = null,
                        enabled = isEnabled
                    )
                }
            }
        }
    }
}
