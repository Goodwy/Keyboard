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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.patrickgold.jetpref.datastore.model.PreferenceData
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluator
import dev.patrickgold.jetpref.datastore.model.PreferenceDataEvaluatorScope
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.DialogPrefStrings
import dev.patrickgold.jetpref.datastore.ui.ExperimentalJetPrefDatastoreUi
import dev.patrickgold.jetpref.datastore.ui.LocalDefaultDialogPrefStrings
import dev.patrickgold.jetpref.datastore.ui.LocalIconSpaceReserved
import dev.patrickgold.jetpref.datastore.ui.LocalIsPrefEnabled
import dev.patrickgold.jetpref.datastore.ui.LocalIsPrefVisible
import dev.patrickgold.jetpref.material.ui.JetPrefAlertDialog
import kotlin.math.round
import kotlin.math.roundToInt

//Modified version https://github.com/patrickgold/jetpref/blob/main/datastore-ui/src/main/kotlin/dev/patrickgold/jetpref/datastore/ui/DialogSliderPreference.kt
@ExperimentalJetPrefDatastoreUi
@Composable
fun DialogSliderPreferenceRow(
    pref: PreferenceData<Int>,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSpaceReserved: Boolean = false,
    title: String,
    valueLabel: @Composable (Int) -> String = { it.toString() },
    summary: @Composable (Int) -> String = valueLabel,
    min: Int,
    max: Int,
    stepIncrement: Int,
    onPreviewSelectedValue: (Int) -> Unit = { },
    dialogStrings: DialogPrefStrings = LocalDefaultDialogPrefStrings.current,
    enabledIf: PreferenceDataEvaluator = { true },
    visibleIf: PreferenceDataEvaluator = { true },
    iconColor: Color = Color.Gray,
    oldView: Boolean = false,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 12.dp,
    paddingTop: Dp = 6.dp,
    paddingBottom: Dp = 6.dp,
) {
    DialogSliderPreferenceRow(
        pref, modifier, icon, iconSpaceReserved, title, valueLabel, summary, min, max,
        stepIncrement, onPreviewSelectedValue, dialogStrings, enabledIf, visibleIf,
        iconColor, oldView, paddingStart, paddingEnd, paddingTop, paddingBottom
    ) {
        try {
            it.roundToInt()
        } catch (e: IllegalArgumentException) {
            it.toInt()
        }
    }
}

@ExperimentalJetPrefDatastoreUi
@Composable
internal fun <V> DialogSliderPreferenceRow(
    pref: PreferenceData<V>,
    modifier: Modifier,
    icon: ImageVector? = null,
    iconSpaceReserved: Boolean,
    title: String,
    valueLabel: @Composable (V) -> String,
    summary: @Composable (V) -> String,
    min: V,
    max: V,
    stepIncrement: V,
    onPreviewSelectedValue: (V) -> Unit,
    dialogStrings: DialogPrefStrings,
    enabledIf: PreferenceDataEvaluator,
    visibleIf: PreferenceDataEvaluator,
    iconColor: Color = Color.Gray,
    oldView: Boolean = true,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 12.dp,
    paddingTop: Dp = 6.dp,
    paddingBottom: Dp = 6.dp,
    convertToV: (Float) -> V,
) where V : Number, V : Comparable<V> {
    require(stepIncrement > convertToV(0f)) { "Step increment must be greater than 0!" }
    require(max > min) { "Maximum value ($max) must be greater than minimum value ($min)!" }

    val prefValue by pref.observeAsState()
    var sliderValue by remember { mutableFloatStateOf(0.0f) }
    val isDialogOpen = remember { mutableStateOf(false) }

    if (LocalIsPrefVisible.current && visibleIf(PreferenceDataEvaluatorScope)) {
        val isEnabled = LocalIsPrefEnabled.current && enabledIf(PreferenceDataEvaluatorScope)
        CompositionLocalProvider(
            LocalIconSpaceReserved provides iconSpaceReserved,
            LocalIsPrefEnabled provides isEnabled,
            LocalIsPrefVisible provides true,
        ) {
            if (oldView) Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .heightIn(min = MIN_HEIGHT_ROW.dp)
                    .fillMaxWidth()
                    .alpha(if (isEnabled) 1f else ALPHA_DISABLED)
                    .clickable(
                        enabled = isEnabled,
                        role = Role.Button,
                        onClick = {
                            sliderValue = prefValue.toFloat()
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
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            modifier = Modifier.padding(end = 34.dp),
                            text = title,
                            lineHeight = 15.sp,)
                        Text(
                            modifier = Modifier.alpha(0.6f).fillMaxWidth(),
                            text = summary(prefValue),
                            fontSize = 15.sp,
                            lineHeight = 15.sp,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End)
                    }
                }
            } else Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .heightIn(min = MIN_HEIGHT_ROW.dp)
                    .fillMaxWidth()
                    .alpha(if (isEnabled) 1f else ALPHA_DISABLED)
                    .clickable(
                        enabled = isEnabled,
                        role = Role.Button,
                        onClick = {
                            sliderValue = prefValue.toFloat()
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
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = title,
                        lineHeight = 15.sp)
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(
                        modifier = Modifier
                            .alpha(0.6f)
                            .padding(4.dp),
                        text = summary(prefValue),
                        maxLines = 3,
                        lineHeight = 15.sp,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                }
            }
            if (isDialogOpen.value) {
                JetPrefAlertDialog(
                    title = title,
                    confirmLabel = dialogStrings.confirmLabel,
                    onConfirm = {
                        pref.set(convertToV(sliderValue))
                        isDialogOpen.value = false
                    },
                    dismissLabel = dialogStrings.dismissLabel,
                    onDismiss = { isDialogOpen.value = false },
                    neutralLabel = dialogStrings.neutralLabel,
                    onNeutral = {
                        pref.reset()
                        isDialogOpen.value = false
                    }
                ) {
                    Column {
                        Text(
                            text = valueLabel(convertToV(sliderValue)),
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                        Slider(
                            value = sliderValue,
                            valueRange = min.toFloat()..max.toFloat(),
                            steps = ((max.toFloat() - min.toFloat()) / stepIncrement.toFloat()).roundToInt() - 1,
                            onValueChange = { sliderValue = round(it) },
                            onValueChangeFinished = { onPreviewSelectedValue(convertToV(sliderValue)) },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                activeTickColor = Color.Transparent,
                                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = SliderDefaults.colors().inactiveTrackColor.alpha,
                                ),
                                inactiveTickColor = Color.Transparent,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalJetPrefDatastoreUi
@Composable
fun DialogSliderPreferenceRow(
    primaryPref: PreferenceData<Int>,
    secondaryPref: PreferenceData<Int>,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSpaceReserved: Boolean = false,
    title: String,
    primaryLabel: String,
    secondaryLabel: String,
    valueLabel: @Composable (Int) -> String = { it.toString() },
    summary: @Composable (Int, Int) -> String = { p, s -> "${valueLabel(p)} / ${valueLabel(s)}" },
    min: Int,
    max: Int,
    stepIncrement: Int,
    onPreviewSelectedPrimaryValue: (Int) -> Unit = { },
    onPreviewSelectedSecondaryValue: (Int) -> Unit = { },
    dialogStrings: DialogPrefStrings = LocalDefaultDialogPrefStrings.current,
    enabledIf: PreferenceDataEvaluator = { true },
    visibleIf: PreferenceDataEvaluator = { true },
    iconColor: Color = Color.Gray,
    oldView: Boolean = false,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 12.dp,
    paddingTop: Dp = 6.dp,
    paddingBottom: Dp = 6.dp,
) {
    DialogSliderPreferenceRow(
        primaryPref, secondaryPref, modifier, icon, iconSpaceReserved, title, primaryLabel,
        secondaryLabel, valueLabel, summary, min, max, stepIncrement, onPreviewSelectedPrimaryValue,
        onPreviewSelectedSecondaryValue, dialogStrings, enabledIf, visibleIf,
        iconColor, oldView, paddingStart, paddingEnd, paddingTop, paddingBottom
    ) {
        try {
            it.roundToInt()
        } catch (e: IllegalArgumentException) {
            it.toInt()
        }
    }
}

@ExperimentalJetPrefDatastoreUi
@Composable
internal fun <V> DialogSliderPreferenceRow(
    primaryPref: PreferenceData<V>,
    secondaryPref: PreferenceData<V>,
    modifier: Modifier,
    icon: ImageVector? = null,
    iconSpaceReserved: Boolean,
    title: String,
    primaryLabel: String,
    secondaryLabel: String,
    valueLabel: @Composable (V) -> String,
    summary: @Composable (V, V) -> String,
    min: V,
    max: V,
    stepIncrement: V,
    onPreviewSelectedPrimaryValue: (V) -> Unit,
    onPreviewSelectedSecondaryValue: (V) -> Unit,
    dialogStrings: DialogPrefStrings,
    enabledIf: PreferenceDataEvaluator,
    visibleIf: PreferenceDataEvaluator,
    iconColor: Color = Color.Gray,
    oldView: Boolean = false,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 12.dp,
    paddingTop: Dp = 6.dp,
    paddingBottom: Dp = 6.dp,
    convertToV: (Float) -> V,
) where V : Number, V : Comparable<V> {
    require(stepIncrement > convertToV(0f)) { "Step increment must be greater than 0!" }
    require(max > min) { "Maximum value ($max) must be greater than minimum value ($min)!" }

    val primaryPrefValue by primaryPref.observeAsState()
    val secondaryPrefValue by secondaryPref.observeAsState()
    var primarySliderValue by remember { mutableStateOf(convertToV(0.0f)) }
    var secondarySliderValue by remember { mutableStateOf(convertToV(0.0f)) }
    val isDialogOpen = remember { mutableStateOf(false) }

    if (LocalIsPrefVisible.current && visibleIf(PreferenceDataEvaluatorScope)) {
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
                    .clickable(
                        enabled = isEnabled,
                        role = Role.Button,
                        onClick = {
                            primarySliderValue = primaryPrefValue
                            secondarySliderValue = secondaryPrefValue
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
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = title,
                        lineHeight = 15.sp)
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(
                        modifier = Modifier
                            .alpha(0.6f)
                            .padding(4.dp),
                        text = summary(primaryPrefValue, secondaryPrefValue),
                        maxLines = 3,
                        lineHeight = 15.sp,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                }
            }
            if (isDialogOpen.value) {
                JetPrefAlertDialog(
                    title = title,
                    confirmLabel = dialogStrings.confirmLabel,
                    onConfirm = {
                        primaryPref.set(primarySliderValue)
                        secondaryPref.set(secondarySliderValue)
                        isDialogOpen.value = false
                    },
                    dismissLabel = dialogStrings.dismissLabel,
                    onDismiss = { isDialogOpen.value = false },
                    neutralLabel = dialogStrings.neutralLabel,
                    onNeutral = {
                        primaryPref.reset()
                        secondaryPref.reset()
                        isDialogOpen.value = false
                    }
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(primaryLabel)
                            Text(valueLabel(primarySliderValue))
                        }
                        Slider(
                            value = primarySliderValue.toFloat(),
                            valueRange = min.toFloat()..max.toFloat(),
                            steps = ((max.toFloat() - min.toFloat()) / stepIncrement.toFloat()).toInt() - 1,
                            onValueChange = { primarySliderValue = convertToV(it) },
                            onValueChangeFinished = { onPreviewSelectedPrimaryValue(primarySliderValue) },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                activeTickColor = Color.Transparent,
                                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = SliderDefaults.colors().inactiveTrackColor.alpha,
                                ),
                                inactiveTickColor = Color.Transparent,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(secondaryLabel)
                            Text(valueLabel(secondarySliderValue))
                        }
                        Slider(
                            value = secondarySliderValue.toFloat(),
                            valueRange = min.toFloat()..max.toFloat(),
                            steps = ((max.toFloat() - min.toFloat()) / stepIncrement.toFloat()).toInt() - 1,
                            onValueChange = { secondarySliderValue = convertToV(it) },
                            onValueChangeFinished = { onPreviewSelectedSecondaryValue(secondarySliderValue) },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                activeTickColor = Color.Transparent,
                                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = SliderDefaults.colors().inactiveTrackColor.alpha,
                                ),
                                inactiveTickColor = Color.Transparent,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalJetPrefDatastoreUi
@Composable
fun DialogSliderPreferenceRow(
    primaryPref: PreferenceData<Float>,
    secondaryPref: PreferenceData<Float>,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconSpaceReserved: Boolean = false,
    title: String,
    primaryLabel: String,
    secondaryLabel: String,
    valueLabel: @Composable (Float) -> String = { it.toString() },
    summary: @Composable (Float, Float) -> String = { p, s -> "${valueLabel(p)} / ${valueLabel(s)}" },
    min: Float,
    max: Float,
    stepIncrement: Float,
    onPreviewSelectedPrimaryValue: (Float) -> Unit = { },
    onPreviewSelectedSecondaryValue: (Float) -> Unit = { },
    dialogStrings: DialogPrefStrings = LocalDefaultDialogPrefStrings.current,
    enabledIf: PreferenceDataEvaluator = { true },
    visibleIf: PreferenceDataEvaluator = { true },
    iconColor: Color = Color.Gray,
    oldView: Boolean = false,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 12.dp,
    paddingTop: Dp = 6.dp,
    paddingBottom: Dp = 6.dp,
) {
    DialogSliderPreferenceRow(
        primaryPref, secondaryPref, modifier, icon, iconSpaceReserved, title, primaryLabel,
        secondaryLabel, valueLabel, summary, min, max, stepIncrement, onPreviewSelectedPrimaryValue,
        onPreviewSelectedSecondaryValue, dialogStrings, enabledIf, visibleIf,
        iconColor, oldView, paddingStart, paddingEnd, paddingTop, paddingBottom
    ) { it }
}
