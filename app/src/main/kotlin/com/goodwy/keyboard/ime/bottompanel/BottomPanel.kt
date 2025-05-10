/*
 * Copyright (C) 2025 Goodwy
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

package com.goodwy.keyboard.ime.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.goodwy.keyboard.FlorisImeService
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.florisPreferenceModel
import com.goodwy.keyboard.ime.ImeUiMode
import com.goodwy.keyboard.ime.core.DisplayLanguageNamesIn
import com.goodwy.keyboard.ime.input.LocalInputFeedbackController
import com.goodwy.keyboard.ime.keyboard.FlorisImeSizing
import com.goodwy.keyboard.ime.media.KeyboardLikeButton
import com.goodwy.keyboard.ime.onehanded.OneHandedMode
import com.goodwy.keyboard.ime.text.keyboard.TextKeyData
import com.goodwy.keyboard.ime.theme.FlorisImeTheme
import com.goodwy.keyboard.ime.theme.FlorisImeUi
import com.goodwy.keyboard.keyboardManager
import com.goodwy.keyboard.lib.compose.autoMirrorForRtl
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.keyboard.subtypeManager
import com.goodwy.lib.snygg.ui.snyggBackground
import com.goodwy.lib.snygg.ui.snyggBorder
import com.goodwy.lib.snygg.ui.snyggShadow
import com.goodwy.lib.snygg.ui.solidColor
import dev.patrickgold.jetpref.datastore.model.observeAsState
import kotlinx.coroutines.launch

@Composable
fun BottomPanel(
    modifier: Modifier = Modifier,
) {
    val prefs by florisPreferenceModel()
    val inputFeedbackController = LocalInputFeedbackController.current
    val oneHandedPanelStyle = FlorisImeTheme.style.get(FlorisImeUi.OneHandedPanel)
    val context = LocalContext.current
    val subtypeManager by context.subtypeManager()
    val keyboardManager by context.keyboardManager()
    val state by keyboardManager.activeState.collectAsState()
    var showPopup by remember { mutableStateOf(false) }
    val bottomPanelMic by prefs.keyboard.bottomPanelMic.observeAsState()

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun SwitchLanguagePopup(
        visible: Boolean,
        onLaunchSettings: () -> Unit,
        onEmojiAction: () -> Unit,
        onOneHandedLeft: () -> Unit,
        onOneHandedOff: () -> Unit,
        onOneHandedRight: () -> Unit,
        onDismiss: () -> Unit,
    ) {
        val scope = rememberCoroutineScope()
        val popupStyle = FlorisImeTheme.style.get(element = FlorisImeUi.KeyPopup)
        val popupStyleFocus = FlorisImeTheme.style.get(element = FlorisImeUi.KeyPopup, isFocus = true)
        val heightRow = 42.dp
        val widthRow = 42.dp * 3 + 10.dp

        val subtypeList = subtypeManager.subtypes
        val displayLanguageNamesIn by prefs.localization.displayLanguageNamesIn.observeAsState()
        val oneHandedMode by prefs.keyboard.oneHandedMode.observeAsState()

        @Composable
        fun Action(
            modifier: Modifier = Modifier,
            icon: ImageVector? = null,
            text: String? = null,
            color: Color = popupStyle.foreground.solidColor(context, default = FlorisImeTheme.fallbackContentColor()),
            action: suspend () -> Unit
        ) {
            Box(
                modifier = modifier
                    .pointerInput(Unit) {
                        detectTapGestures {
                            scope.launch {
                                action()
                            }
                        }
                    }
                    .widthIn(min = 42.dp, max = widthRow)
                    .height(heightRow)
                    .padding(all = 4.dp),
            ) {
                if (icon != null) Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                )
                if (text != null) Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .width(widthRow)
                        .align(Alignment.Center),
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = color,
                )
            }
        }

        if (visible) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = with(LocalDensity.current) {
//                val x = 0.dp.toPx().toInt()
                    val y = (heightRow * (3 + subtypeList.size) + 20.dp).toPx().toInt()
                    IntOffset(x = 0, y = -y)
                },
                onDismissRequest = onDismiss,
            ) {
                FlowColumn(
                    modifier = Modifier
                        .padding(4.dp)
                        .widthIn(max = widthRow)
                        .snyggShadow(popupStyle)
                        .snyggBorder(context, popupStyle)
                        .snyggBackground(context, popupStyle, fallbackColor = FlorisImeTheme.fallbackSurfaceColor()),
                ) {
                    FlowColumn {
                        Action(
                            text = stringRes(R.string.settings__title),
                            action = onLaunchSettings,
                        )
                        for (subtype in subtypeList) {
                            HorizontalDivider(thickness = Dp.Hairline)
                            val title = when (displayLanguageNamesIn) {
                                DisplayLanguageNamesIn.SYSTEM_LOCALE -> subtype.primaryLocale.displayName()
                                DisplayLanguageNamesIn.NATIVE_LOCALE -> subtype.primaryLocale.displayName(subtype.primaryLocale)
                            }
                            val subtypeStyle =
                                if (subtypeManager.activeSubtype == subtype && keyboardManager.activeState.imeUiMode != ImeUiMode.MEDIA) {
                                    popupStyleFocus
                                } else {
                                    popupStyle
                                }
                            Action(
                                modifier = Modifier.background(subtypeStyle.background.solidColor(context)),
                                text = title,
                                color = subtypeStyle.foreground.solidColor(context),
                                action = {
                                    keyboardManager.activeState.imeUiMode = ImeUiMode.TEXT
                                    subtypeManager.switchToSubtype(subtype)
                                    showPopup = false
                                },
                            )
                        }
                        HorizontalDivider(thickness = Dp.Hairline)
                        val emojiStyle =
                            if (keyboardManager.activeState.imeUiMode == ImeUiMode.MEDIA) {
                                popupStyleFocus
                            } else {
                                popupStyle
                            }
                        Action(
                            modifier = Modifier.background(emojiStyle.background.solidColor(context)),
                            text = stringRes(R.string.media__tab__emojis),
                            color = emojiStyle.foreground.solidColor(context),
                            action = onEmojiAction,
                        )
                        HorizontalDivider(thickness = Dp.Hairline)
                        FlowRow(
                            modifier = Modifier.padding(horizontal = 4.dp),
                        ) {
                            val oneHandedLeftStyle = if (oneHandedMode == OneHandedMode.START) popupStyleFocus else popupStyle
                            val oneHandedOffStyle = if (oneHandedMode == OneHandedMode.OFF) popupStyleFocus else popupStyle
                            val oneHandedRightStyle = if (oneHandedMode == OneHandedMode.END) popupStyleFocus else popupStyle
                            Action(
                                modifier = Modifier.padding(vertical = 4.dp)
                                    .snyggBackground(context, oneHandedLeftStyle, fallbackColor = FlorisImeTheme.fallbackSurfaceColor()),
                                icon = ImageVector.vectorResource(R.drawable.ic_keyboard_left),
                                color = oneHandedLeftStyle.foreground.solidColor(context),
                                action = onOneHandedLeft,
                            )
                            Action(
                                modifier = Modifier.padding(vertical = 4.dp)
                                    .snyggBackground(context, oneHandedOffStyle, fallbackColor = FlorisImeTheme.fallbackSurfaceColor()),
                                icon = ImageVector.vectorResource(R.drawable.ic_keyboard),
                                color = oneHandedOffStyle.foreground.solidColor(context),
                                action = onOneHandedOff,
                            )
                            Action(
                                modifier = Modifier.padding(vertical = 4.dp)
                                    .snyggBackground(context, oneHandedRightStyle, fallbackColor = FlorisImeTheme.fallbackSurfaceColor()),
                                icon = ImageVector.vectorResource(R.drawable.ic_keyboard_right),
                                color = oneHandedRightStyle.foreground.solidColor(context),
                                action = onOneHandedRight,
                            )
                        }
                    }
                }
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FlorisImeSizing.smartbarHeight)
            .padding(start = 12.dp, end = 6.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            inputFeedbackController.keyPress()
                        },
                        onTap = {
                            if (keyboardManager.activeState.imeUiMode != ImeUiMode.TEXT) {
                                keyboardManager.activeState.imeUiMode = ImeUiMode.TEXT
                            } else {
                                if (prefs.localization.switchEmojiWhenChangingLanguage.get()) {
                                    if (subtypeManager.activeSubtype == subtypeManager.subtypes.lastOrNull()) {
                                        keyboardManager.activeState.imeUiMode = ImeUiMode.MEDIA
                                    }
                                }
                                subtypeManager.switchToNextSubtype()
                            }
                        },
                        onLongPress = {
                            inputFeedbackController.keyPress()
                            showPopup = true
                        },
                    )
                },
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(R.drawable.ic_language),
                contentDescription = stringRes(R.string.enum__utility_key_action__switch_language),
                tint = oneHandedPanelStyle.foreground.solidColor(context),
            )

            SwitchLanguagePopup(
                visible = showPopup,
                onLaunchSettings = {
                    FlorisImeService.launchSettings()
                    showPopup = false
                },
                onEmojiAction = {
                    keyboardManager.activeState.imeUiMode = ImeUiMode.MEDIA
                    showPopup = false
                },
                onOneHandedLeft= {
                    prefs.keyboard.oneHandedMode.set(OneHandedMode.START)
                    showPopup = false
                },
                onOneHandedOff= {
                    prefs.keyboard.oneHandedMode.set(OneHandedMode.OFF)
                    showPopup = false
                },
                onOneHandedRight= {
                    prefs.keyboard.oneHandedMode.set(OneHandedMode.END)
                    showPopup = false
                },
                onDismiss = {
                    showPopup = false
                },
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (bottomPanelMic) KeyboardLikeButton(
            inputEventDispatcher = keyboardManager.inputEventDispatcher,
            keyData = TextKeyData.VOICE_INPUT,
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .autoMirrorForRtl(),
                imageVector = ImageVector.vectorResource(R.drawable.ic_mic),
                contentDescription = null
            )
        }
        if (state.imeUiMode == ImeUiMode.MEDIA) KeyboardLikeButton(
            inputEventDispatcher = keyboardManager.inputEventDispatcher,
            keyData = TextKeyData.DELETE,
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .autoMirrorForRtl(),
                imageVector = ImageVector.vectorResource(R.drawable.ic_backspace),
                contentDescription = null
            )
        }
    }
}
