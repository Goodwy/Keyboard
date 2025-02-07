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

package com.goodwy.keyboard.ime.smartbar.quickaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.goodwy.keyboard.app.florisPreferenceModel
import com.goodwy.keyboard.ime.smartbar.SmartbarLayout
import com.goodwy.keyboard.ime.text.keyboard.TextKeyData
import com.goodwy.keyboard.ime.theme.FlorisImeTheme
import com.goodwy.keyboard.ime.theme.FlorisImeUi
import com.goodwy.keyboard.keyboardManager
import com.goodwy.lib.snygg.ui.snyggBackground
import com.goodwy.lib.snygg.ui.solidColor
import dev.patrickgold.jetpref.datastore.model.observeAsState

internal val ToggleOverflowPanelAction = QuickAction.InsertKey(TextKeyData.TOGGLE_ACTIONS_OVERFLOW)

@Composable
fun QuickActionsRow(
    elementName: String,
    modifier: Modifier = Modifier,
) = with(LocalDensity.current) {
    val prefs by florisPreferenceModel()
    val context = LocalContext.current
    val keyboardManager by context.keyboardManager()

    val flipToggles by prefs.smartbar.flipToggles.observeAsState()
    val evaluator by keyboardManager.activeSmartbarEvaluator.collectAsState()
    val smartbarLayout by prefs.smartbar.layout.observeAsState()
    val actionArrangement by prefs.smartbar.actionArrangement.observeAsState()
    val sharedActionsExpanded by prefs.smartbar.sharedActionsExpanded.observeAsState()

    val dynamicActions = remember(smartbarLayout, actionArrangement) {
        if (smartbarLayout == SmartbarLayout.ACTIONS_ONLY && actionArrangement.stickyAction != null) {
            buildList {
                add(actionArrangement.stickyAction!!)
                addAll(actionArrangement.dynamicActions)
            }
        } else {
            actionArrangement.dynamicActions
        }
    }
    val showOverflowAction = actionArrangement.stickyAction != null ||
        smartbarLayout != SmartbarLayout.SUGGESTIONS_ACTIONS_SHARED || !sharedActionsExpanded

    val rowStyle = FlorisImeTheme.style.get(elementName)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toDp()
        val height = constraints.maxHeight.toDp()
        val numActionsToShow = ((width / height).toInt() - (if (showOverflowAction) 1 else 0) - 2).coerceAtLeast(0)
        val visibleActions = dynamicActions
            .subList(0, numActionsToShow.coerceAtMost(dynamicActions.size))

        SideEffect {
            keyboardManager.smartbarVisibleDynamicActionsCount =
                if (smartbarLayout == SmartbarLayout.ACTIONS_ONLY && actionArrangement.stickyAction != null) {
                    numActionsToShow - 1
                } else {
                    numActionsToShow
                }.coerceAtLeast(0)
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .snyggBackground(context, rowStyle),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            val spacerStyle = FlorisImeTheme.style.get(FlorisImeUi.SmartbarCandidateSpacer)
            if (showOverflowAction && flipToggles) {
                QuickActionButton(ToggleOverflowPanelAction, evaluator)
                //TODO Goodwy. Candidates Divider
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .align(Alignment.CenterVertically),
                    thickness = Dp.Hairline,
                    color = spacerStyle.foreground.solidColor(context)
                )
            }
            for (action in visibleActions) {
                QuickActionButton(action, evaluator)
            }
            if (showOverflowAction && !flipToggles) {
                //TODO Goodwy. More actions Divider
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .align(Alignment.CenterVertically),
                    thickness = Dp.Hairline,
                    color = spacerStyle.foreground.solidColor(context)
                )
                QuickActionButton(ToggleOverflowPanelAction, evaluator)
            }
        }
    }
}
