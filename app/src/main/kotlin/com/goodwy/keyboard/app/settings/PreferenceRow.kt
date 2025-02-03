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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.lib.compose.autoMirrorForRtl
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluator
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluatorScope
import dev.patrickgold.jetpref.datastore.ui.LocalIconSpaceReserved
import dev.patrickgold.jetpref.datastore.ui.LocalIsPrefEnabled
import dev.patrickgold.jetpref.datastore.ui.LocalIsPrefVisible

//Modified version https://github.com/patrickgold/jetpref/blob/main/datastore-ui/src/main/kotlin/dev/patrickgold/jetpref/datastore/ui/Preference.kt
/**
 * Material list item which behaves and looks like a preference, but does not provide an
 * automatic display of state for a datastore entry, unlike all other available preference
 * composables. This can be used to manually add a custom preference or be used to allow for
 * click-only actions in a preference screen, such as navigation to a sub screen or in the app's
 * about screen.
 *
 * @param modifier Modifier to be applied to the underlying list item.
 * @param icon The [ImageVector] of the list entry icon.
 * @param iconSpaceReserved If the space at the start of the list item should be reserved (blank
 *  space) if no icon ID is provided.
 * @param title The title of this preference, shown as the list item primary text (max 1 line).
 * @param summary The summary of this preference, shown as the list item secondary text (max 2 lines).
 * @param trailing Optional trailing composable, will be placed at the end of the list item.
 * @param enabledIf Evaluator scope which allows to dynamically decide if this preference should be
 *  enabled (true) or disabled (false).
 * @param visibleIf Evaluator scope which allows to dynamically decide if this preference should be
 *  visible (true) or hidden (false).
 * @param onClick The action to perform if this preference is enabled and the user clicks on this
 *  preference item. Mutually exclusive with [eventModifier].
 * @param eventModifier An optional modifier to apply to the preference item. This can be used to set up toggles or
 *  other interactions. Mutually exclusive with [onClick].
 *
 * @param iconColor [icon] color.
 * @param showEndIcon shows a chevron icon or an [endIcon] at the end of the.
 * @param endIcon icon at the end, the chevron icon is used by default.
 *
 * @since 0.1.0
 */
@Composable
fun PreferenceRow(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSpaceReserved: Boolean = false, //LocalIconSpaceReserved.current
    title: String,
    summary: String? = null,
    value: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    enabledIf: PreferenceDataEvaluator = { true },
    visibleIf: PreferenceDataEvaluator = { true },
    onClick: (() -> Unit)? = null,
    eventModifier: (@Composable () -> Modifier)? = null,
    iconColor: Color = Color.Gray,
    showEndIcon: Boolean = true,
    endIcon: ImageVector = Icons.Rounded.ChevronRight,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 12.dp,
    paddingTop: Dp = 6.dp,
    paddingBottom: Dp = 6.dp
) {
    require(!(onClick != null && eventModifier != null)) {
        "You cannot provide both an onClick lambda and an eventModifier."
    }
    if (LocalIsPrefVisible.current && visibleIf(PreferenceDataEvaluatorScope)) {
        val isEnabled = LocalIsPrefEnabled.current && enabledIf(PreferenceDataEvaluatorScope)
        CompositionLocalProvider(
            LocalIconSpaceReserved provides iconSpaceReserved,
            LocalIsPrefEnabled provides isEnabled,
            LocalIsPrefVisible provides true,
        ) {
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
                        if (value != null) {
                            Text(
                                modifier = Modifier
                                    .weight(0.8f)
                                    .alpha(0.6f)
                                    .padding(4.dp),
                                text = value,
                                maxLines = 2,
                                lineHeight = 15.sp,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.End
                            )
                        }
                        if (showEndIcon) Icon(
                            modifier = Modifier.alpha(0.6f).autoMirrorForRtl(),
                            imageVector = endIcon,
                            contentDescription = title
                        )
                    }
                }
            }
        }
    }
}
