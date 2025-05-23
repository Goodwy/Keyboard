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

package com.goodwy.keyboard.ime.media

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.ime.input.InputEventDispatcher
import com.goodwy.keyboard.ime.input.LocalInputFeedbackController
import com.goodwy.keyboard.ime.keyboard.FlorisImeSizing
import com.goodwy.keyboard.ime.keyboard.KeyData
import com.goodwy.keyboard.ime.media.emoji.EmojiData
import com.goodwy.keyboard.ime.media.emoji.EmojiPaletteView
import com.goodwy.keyboard.ime.text.keyboard.TextKeyData
import com.goodwy.keyboard.ime.theme.FlorisImeTheme
import com.goodwy.keyboard.ime.theme.FlorisImeUi
import com.goodwy.keyboard.keyboardManager
import com.goodwy.keyboard.lib.compose.autoMirrorForRtl
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.lib.snygg.ui.SnyggSurface

@SuppressLint("MutableCollectionMutableState")
@Composable
fun MediaInputLayout(
    modifier: Modifier = Modifier,
    bottomPanelMode: Boolean,
) {
    val context = LocalContext.current
    val keyboardManager by context.keyboardManager()

    var emojiLayoutDataMap by remember { mutableStateOf(EmojiData.Fallback) }
    LaunchedEffect(Unit) {
        emojiLayoutDataMap = EmojiData.get(context, "ime/media/emoji/root.txt")
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(FlorisImeSizing.imeUiHeight() + 4.dp)
                .padding(bottom = 4.dp), //TODO Goodwy. Keyboard bottom padding
        ) {
            EmojiPaletteView(
                modifier = Modifier.weight(1f),
                fullEmojiMappings = emojiLayoutDataMap,
            )
            if (!bottomPanelMode) Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FlorisImeSizing.keyboardRowBaseHeight * 0.8f)
                    .padding(start = 6.dp, end = 6.dp),
            ) {
                KeyboardLikeButton(
                    inputEventDispatcher = keyboardManager.inputEventDispatcher,
                    keyData = TextKeyData.IME_UI_MODE_TEXT,
                ) {
                    Text(
                        text = "ABC",
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                KeyboardLikeButton(
                    inputEventDispatcher = keyboardManager.inputEventDispatcher,
                    keyData = TextKeyData.DELETE,
                ) {
                    Icon(
                        modifier = Modifier.autoMirrorForRtl(),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_backspace),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
internal fun KeyboardLikeButton(
    modifier: Modifier = Modifier,
    inputEventDispatcher: InputEventDispatcher,
    keyData: KeyData,
    element: String = FlorisImeUi.EmojiKey,
    content: @Composable RowScope.() -> Unit,
) {
    val inputFeedbackController = LocalInputFeedbackController.current
    var isPressed by remember { mutableStateOf(false) }
    val keyStyle = FlorisImeTheme.style.get(
        element = element,
        code = keyData.code,
        isPressed = isPressed,
    )
    SnyggSurface(
        modifier = modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false).also {
                    if (it.pressed != it.previousPressed) it.consume()
                }
                isPressed = true
                inputEventDispatcher.sendDown(keyData)
                inputFeedbackController.keyPress(keyData)
                val up = waitForUpOrCancellation()
                isPressed = false
                if (up != null) {
                    inputEventDispatcher.sendUp(keyData)
                } else {
                    inputEventDispatcher.sendCancel(keyData)
                }
            }
        },
        style = keyStyle,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
