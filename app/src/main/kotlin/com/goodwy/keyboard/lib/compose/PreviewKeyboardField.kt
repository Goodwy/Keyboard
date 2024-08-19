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

package com.goodwy.keyboard.lib.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.florisPreferenceModel
import com.goodwy.keyboard.lib.util.InputMethodUtils
import com.goodwy.lib.android.showShortToast
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.datastore.ui.LocalDefaultDialogPrefStrings
import dev.patrickgold.jetpref.material.ui.JetPrefAlertDialog

private const val AnimationDuration = 200

private val PreviewEnterTransition = EnterTransition.verticalTween(AnimationDuration)
private val PreviewExitTransition = ExitTransition.verticalTween(AnimationDuration)

val LocalPreviewFieldController = staticCompositionLocalOf<PreviewFieldController?> { null }

@Composable
fun rememberPreviewFieldController(): PreviewFieldController {
    return remember { PreviewFieldController() }
}

class PreviewFieldController {
    val focusRequester = FocusRequester()
    var isVisible by mutableStateOf(false)
    var text by mutableStateOf(TextFieldValue(""))
}

@Composable
fun PreviewKeyboardField(
    controller: PreviewFieldController,
    modifier: Modifier = Modifier,
    hint: String = stringRes(R.string.settings__preview_keyboard),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val prefs by florisPreferenceModel()
    val previewKeyboardTypePref = prefs.internal.previewKeyboardType
    val previewKeyboardType by prefs.internal.previewKeyboardType.observeAsState()
    val isDialogOpen = remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = controller.isVisible,
        enter = PreviewEnterTransition,
        exit = PreviewExitTransition,
    ) {
        SelectionContainer {
            HorizontalDivider(thickness = Dp.Hairline)
            TextField(
                modifier = modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .onPreviewKeyEvent { event ->
                        if (event.key == Key.Back) {
                            focusManager.clearFocus()
                        }
                        false
                    }
                    .focusRequester(controller.focusRequester),
                value = controller.text,
                onValueChange = { controller.text = it },
                textStyle = LocalTextStyle.current.copy(textDirection = TextDirection.ContentOrLtr),
                placeholder = {
                    Text(
                        text = hint,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                leadingIcon = {
                    IconButton(onClick = {
                        isDialogOpen.value = true
                    }) {
                        Icon(
                            imageVector = keyboardTypeIcon(previewKeyboardType),
                            contentDescription = null,
                        )
                    }
                },
                trailingIcon = {
                    Row {
                        IconButton(onClick = {
                            if (!InputMethodUtils.showImePicker(context)) {
                                context.showShortToast("Error: InputMethodManager service not available!")
                            }
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_keyboard), //Icons.Default.Keyboard,
                                contentDescription = null,
                            )
                        }
                    }
                },
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
                keyboardOptions = KeyboardOptions(autoCorrect = true, keyboardType = when(previewKeyboardType) {
                    3 -> KeyboardType.Number
                    4 -> KeyboardType.Phone
                    5 -> KeyboardType.Uri
                    6 -> KeyboardType.Email
                    else -> KeyboardType.Text
                }),
                singleLine = true,
                shape = RectangleShape,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        }
    }

    if (isDialogOpen.value) {
        val typeList = arrayListOf(1,3,4,5,6)
        val dialogStrings = LocalDefaultDialogPrefStrings.current
        val (tmpListPrefValue, setTmpListPrefValue) = remember { mutableIntStateOf(previewKeyboardTypePref.get()) }
        JetPrefAlertDialog(
            title = stringRes(id = com.goodwy.keyboard.strings.R.string.settings__keyboard_type),
            confirmLabel = dialogStrings.confirmLabel,
            onConfirm = {
                previewKeyboardTypePref.set(tmpListPrefValue)
                isDialogOpen.value = false
            },
            dismissLabel = dialogStrings.dismissLabel,
            onDismiss = { isDialogOpen.value = false },
            neutralLabel = dialogStrings.neutralLabel,
            onNeutral = {
                previewKeyboardTypePref.reset()
                isDialogOpen.value = false
            },
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            Column {
                for (type in typeList) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = type == tmpListPrefValue,
                                onClick = {
                                    setTmpListPrefValue(type)
                                }
                            )
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp,
                            )
                    ) {
                        RadioButton(
                            selected = type == tmpListPrefValue,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                            ),
                            modifier = Modifier.padding(end = 12.dp),
                        )
                        val typeText = when(type) {
                            3 -> com.goodwy.keyboard.strings.R.string.settings__number
                            4 -> R.string.clipboard__item_description_phone
                            5 -> R.string.clipboard__item_description_url
                            6 -> R.string.clipboard__item_description_email
                            else -> com.goodwy.keyboard.strings.R.string.settings__text
                        }
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            text = stringRes(id = typeText),
                            maxLines = 1,
                            lineHeight = 15.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = keyboardTypeIcon(type),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }
    }
}

private fun keyboardTypeIcon(type: Int) : ImageVector {
    return when(type) {
        3 -> Icons.Rounded.Numbers
        4 -> Icons.Rounded.Dialpad
        5 -> Icons.Rounded.Link
        6 -> Icons.Rounded.MailOutline
        else -> Icons.Rounded.TextFields
    }
}
