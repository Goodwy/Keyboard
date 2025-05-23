/*
 * Copyright (C) 2021 Patrick Goldinger
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

package com.goodwy.keyboard.ime.text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.florisPreferenceModel
import com.goodwy.keyboard.ime.keyboard.KeyboardMode
import com.goodwy.keyboard.ime.smartbar.IncognitoDisplayMode
import com.goodwy.keyboard.ime.smartbar.Smartbar
import com.goodwy.keyboard.ime.smartbar.quickaction.QuickActionsOverflowPanel
import com.goodwy.keyboard.ime.text.keyboard.TextKeyboardLayout
import com.goodwy.keyboard.ime.theme.FlorisImeTheme
import com.goodwy.keyboard.ime.theme.FlorisImeUi
import com.goodwy.keyboard.keyboardManager
import com.goodwy.lib.snygg.ui.solidColor
import dev.patrickgold.jetpref.datastore.model.observeAsState

@Composable
fun TextInputLayout(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val keyboardManager by context.keyboardManager()

    val prefs by florisPreferenceModel()

    val state by keyboardManager.activeState.collectAsState()
    val evaluator by keyboardManager.activeEvaluator.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 4.dp), //TODO Goodwy. Keyboard bottom padding
        ) {
            Smartbar()
            if (state.isActionsOverflowVisible) {
                QuickActionsOverflowPanel()
            } else {
                Box {
                    val showIncognitoIcon = evaluator.state.isIncognitoMode && prefs.keyboard.incognitoDisplayMode.observeAsState().value == IncognitoDisplayMode.DISPLAY_BEHIND_KEYBOARD
                    if (showIncognitoIcon) {
                        val indicatorStyle = FlorisImeTheme.style.get(FlorisImeUi.IncognitoModeIndicator)
                        Icon(
                            modifier = Modifier
                                .matchParentSize()
                                .align(Alignment.Center),
                            painter = painterResource(R.drawable.ic_incognito),
                            contentDescription = null,
                            tint = indicatorStyle.foreground.solidColor(
                                context, default = FlorisImeTheme.fallbackContentColor().copy(alpha = 0.067f),
                            ),
                        )
                    }
                    val debugLayoutResult by keyboardManager.layoutManager
                        .debugLayoutComputationResultFlow.collectAsState()
                    if (state.keyboardMode != KeyboardMode.EDITING) {
                        if (debugLayoutResult?.allLayoutsSuccess() == true) {
                            TextKeyboardLayout(evaluator = evaluator)
                        } else {
                            HowDidWeGetHere()
                        }
                    } else {
                        HowDidWeGetHere()
                    }
                }
            }
        }
    }
}
