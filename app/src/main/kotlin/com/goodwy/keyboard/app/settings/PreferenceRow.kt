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
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.goodwy.keyboard.lib.compose.autoMirrorForRtl
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluator
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluatorScope
import dev.patrickgold.jetpref.datastore.model.PreferenceModel
import dev.patrickgold.jetpref.datastore.ui.PreferenceUiScope

@Composable
fun <T : PreferenceModel> PreferenceUiScope<T>.PreferenceRow(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSpaceReserved: Boolean = false,
    title: String,
    summary: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    enabledIf: PreferenceDataEvaluator = { true },
    visibleIf: PreferenceDataEvaluator = { true },
    onClick: (() -> Unit)? = null,
    iconColor: Color = Color.Gray,
    showEndIcon: Boolean = true,
    endIcon: ImageVector? = null,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 12.dp,
    paddingTop: Dp = 6.dp,
    paddingBottom: Dp = 6.dp
) {
    val evalScope = PreferenceDataEvaluatorScope.instance()
    if (visibleIf(evalScope)) {
        val isEnabled = enabledIf(evalScope)
        Box(
            contentAlignment = Alignment.Center,
            modifier = if (onClick != null) {
                Modifier
                    .heightIn(min = MIN_HEIGHT_ROW.dp)
                    .fillMaxWidth()
                    .alpha(if (isEnabled) 1f else ALPHA_DISABLED)
                    .clickable(
                        enabled = isEnabled,
                        role = Role.Button,
                        onClick = onClick,
                    )
                    .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            } else {
                Modifier
                    .heightIn(min = MIN_HEIGHT_ROW.dp)
                    .fillMaxWidth()
                    .alpha(if (isEnabled) 1f else ALPHA_DISABLED)
                    .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            },
        ) {
            Box(modifier = modifier) {
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
                        Text(text = title, lineHeight = 15.sp,)
                        if (summary != null) Text(
                            modifier = Modifier.alpha(0.6f),
                            text = summary,
                            fontSize = 12.sp,
                            lineHeight = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    if (showEndIcon) Icon(
                        modifier = Modifier.alpha(0.6f).autoMirrorForRtl(),
                        imageVector = endIcon ?: Icons.Rounded.ChevronRight,
                        contentDescription = title
                    )
                }
            }
        }
    }
}
