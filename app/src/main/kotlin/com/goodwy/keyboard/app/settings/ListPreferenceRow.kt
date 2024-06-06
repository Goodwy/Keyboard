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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.lib.compose.autoMirrorForRtl
import dev.patrickgold.jetpref.datastore.model.PreferenceData
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluator
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluatorScope
import dev.patrickgold.jetpref.datastore.model.PreferenceModel
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.DialogPrefStrings
import dev.patrickgold.jetpref.datastore.ui.ListPreferenceEntry
import dev.patrickgold.jetpref.datastore.ui.LocalDefaultDialogPrefStrings
import dev.patrickgold.jetpref.datastore.ui.PreferenceUiScope
import dev.patrickgold.jetpref.material.ui.JetPrefAlertDialog

@Composable
internal fun <T : PreferenceModel, V : Any> PreferenceUiScope<T>.ListPreferenceRow(
    listPref: PreferenceData<V>,
    switchPref: PreferenceData<Boolean>? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSpaceReserved: Boolean = false,
    title: String,
    summary: String? = null,
    summarySwitchDisabled: String? = null,
    dialogStrings: DialogPrefStrings = LocalDefaultDialogPrefStrings.current,
    enabledIf: PreferenceDataEvaluator = { true },
    visibleIf: PreferenceDataEvaluator = { true },
    entries: List<ListPreferenceEntry<V>>,
    iconColor: Color = Color.Gray,
    showEndIcon: Boolean = false,
    endIcon: ImageVector? = null,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 12.dp,
    paddingTop: Dp = 6.dp,
    paddingBottom: Dp = 6.dp,
) {
    val listPrefValue by listPref.observeAsState()
    val switchPrefValue = switchPref?.observeAsState() // can't use delegate because nullable
    val (tmpListPrefValue, setTmpListPrefValue) = remember { mutableStateOf(listPref.get()) }
    val (tmpSwitchPrefValue, setTmpSwitchPrefValue) = remember { mutableStateOf(false) }
    val isDialogOpen = remember { mutableStateOf(false) }

    val evalScope = PreferenceDataEvaluatorScope.instance()
    if (visibleIf(evalScope)) {
        val isEnabled = enabledIf(evalScope)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .heightIn(min = MIN_HEIGHT_ROW.dp)
                .fillMaxWidth()
                .alpha(if (isEnabled) 1f else ALPHA_DISABLED)
                .clickable(
                    enabled = isEnabled,
                    role = Role.Button,
                    onClick = {
                        setTmpListPrefValue(listPrefValue)
                        if (switchPrefValue != null) {
                            setTmpSwitchPrefValue(switchPrefValue.value)
                        }
                        isDialogOpen.value = true
                    }
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
                    val value = if (switchPrefValue?.value == true || switchPrefValue == null) {
                        entries.find {
                            it.key == listPrefValue
                        }?.label ?: "!! invalid !!"
                    } else { summarySwitchDisabled }
                    if (value != null && switchPrefValue != null) Text(
                        modifier = Modifier.alpha(0.6f).fillMaxWidth(),
                        text = value,
                        maxLines = 2,
                        fontSize = 15.sp,
                        lineHeight = 15.sp,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End)
                    if (summary != null) Text(
                        modifier = Modifier.alpha(0.6f),
                        text = summary,
                        fontSize = 12.sp,
                        lineHeight = 12.sp)
                }
                Spacer(modifier = Modifier.size(16.dp))
                Row(
                    modifier = Modifier.widthIn(min = 24.dp, max = 200.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val value = if (switchPrefValue?.value == true || switchPrefValue == null) {
                        entries.find {
                            it.key == listPrefValue
                        }?.label ?: "!! invalid !!"
                    } else { summarySwitchDisabled }
                    if (value != null && switchPrefValue == null) {
                        Text(
                            modifier = Modifier
                                .alpha(0.6f)
                                .padding(4.dp),
                            text = value,
                            maxLines = 3,
                            lineHeight = 15.sp,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End
                        )
                    }
                    if (showEndIcon) Icon(
                        modifier = if (endIcon == null) Modifier.alpha(0.6f).autoMirrorForRtl() else Modifier.alpha(0.6f),
                        imageVector = endIcon ?: Icons.Rounded.ChevronRight,
                        contentDescription = title)
                    if (switchPrefValue != null) {
                        val dividerColor = MaterialTheme.colorScheme.outlineVariant
                        Box(
                            modifier = Modifier
                                .size(LocalViewConfiguration.current.minimumTouchTargetSize + DpSize(10.dp, 0.dp))
                                .toggleable(
                                    value = switchPrefValue.value,
                                    enabled = isEnabled,
                                    role = Role.Switch,
                                    onValueChange = { switchPref.set(it) },
                                )
                                .drawBehind {
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(0f, size.height * 0.1f),
                                        end = Offset(0f, size.height * 0.9f),
                                        strokeWidth = 2f
                                    )
                                }
                                .padding(end = 2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Switch(
                                modifier = Modifier.padding(start = 8.dp),
                                checked = switchPrefValue.value,
                                onCheckedChange = null,
                                enabled = isEnabled,
                            )
                        }
                    }
                }
            }
        }
        if (isDialogOpen.value) {
            JetPrefAlertDialog(
                title = title,
                confirmLabel = dialogStrings.confirmLabel,
                onConfirm = {
                    listPref.set(tmpListPrefValue)
                    switchPref?.set(tmpSwitchPrefValue)
                    isDialogOpen.value = false
                },
                dismissLabel = dialogStrings.dismissLabel,
                onDismiss = { isDialogOpen.value = false },
                neutralLabel = dialogStrings.neutralLabel,
                onNeutral = {
                    listPref.reset()
                    switchPref?.reset()
                    isDialogOpen.value = false
                },
                trailingIconTitle = {
                    if (switchPrefValue != null) {
                        Switch(
                            modifier = Modifier.padding(start = 16.dp),
                            checked = tmpSwitchPrefValue,
                            onCheckedChange = { setTmpSwitchPrefValue(it) },
                            enabled = true,
                        )
                    }
                },
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                Column {
                    val alpha = when {
                        switchPrefValue == null -> 1f
                        tmpSwitchPrefValue -> 1f
                        else -> 0.38f
                    }
                    for (entry in entries) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = entry.key == tmpListPrefValue,
                                    enabled = switchPrefValue == null || tmpSwitchPrefValue,
                                    onClick = {
                                        setTmpListPrefValue(entry.key)
                                    }
                                )
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp,
                                )
                                .alpha(alpha)
                        ) {
                            RadioButton(
                                selected = entry.key == tmpListPrefValue,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                ),
                                modifier = Modifier.padding(end = 12.dp),
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                entry.labelComposer(entry.label)
                                if (entry.showDescriptionOnlyIfSelected) {
                                    if (entry.key == tmpListPrefValue) {
                                        entry.descriptionComposer(entry.description)
                                    }
                                } else {
                                    entry.descriptionComposer(entry.description)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
