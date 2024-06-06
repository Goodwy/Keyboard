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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodwy.keyboard.R

@Composable
fun SupportRow(
    modifier: Modifier,
    onClick: () -> Unit,
    ) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(86.dp)
                        .padding(2.dp),
                    painter = painterResource(id = R.drawable.ic_plus_support),
                    contentDescription = stringResource(id = R.string.action_support_project),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
            Column(
                modifier = Modifier
                    .heightIn(86.dp)
                    .padding(bottom = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(R.string.action_support_project),
                    fontSize = 18.sp,
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(R.string.action_support_project_summary),
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    color = LocalContentColor.current.copy(alpha = 0.5F),
                )
                Button(
                    modifier = Modifier.padding(start = 16.dp, top = 3.dp).wrapContentWidth().height(24.dp)
                        .alpha(0.6f),
                    onClick = { onClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.learn_more).toUpperCase(LocaleList.current),
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.shake(enabled: Boolean, onAnimationFinish: () -> Unit): Modifier = then(
    composed(
        factory = {
            val distance by animateFloatAsState(
                targetValue = if (enabled) 12f else 0f,
                animationSpec = repeatable(
                    iterations = 3,
                    animation = tween(durationMillis = 70, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                finishedListener = { onAnimationFinish.invoke() }, label = ""
            )

            Modifier.graphicsLayer {
                translationX = if (enabled) distance else 0f
            }
        },
        inspectorInfo = debugInspectorInfo {
            name = "shake"
            properties["enabled"] = enabled
        }
    )
)
