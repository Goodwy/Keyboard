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

package com.goodwy.keyboard.app.settings.theme

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.florisPreferenceModel
import com.goodwy.keyboard.extensionManager
import com.goodwy.keyboard.ime.theme.ThemeExtensionComponent
import com.goodwy.keyboard.lib.compose.FlorisOutlinedBox
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.defaultFlorisOutlinedBox
import com.goodwy.keyboard.lib.compose.rippleClickable
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.keyboard.lib.ext.ExtensionComponentName
import com.goodwy.keyboard.lib.observeAsNonNullState
import com.goodwy.keyboard.themeManager
import com.goodwy.lib.android.AndroidVersion.ATLEAST_API31_S
import dev.patrickgold.jetpref.datastore.model.observeAsState
import dev.patrickgold.jetpref.material.ui.JetPrefListItem

enum class ThemeManagerScreenAction(val id: String) {
    SELECT_DAY("select-day"),
    SELECT_NIGHT("select-night");
}

@Composable
fun ThemeManagerScreen(action: ThemeManagerScreenAction?) = FlorisScreen {
    title = stringRes(when (action) {
        ThemeManagerScreenAction.SELECT_DAY -> R.string.settings__theme_manager__title_day
        ThemeManagerScreenAction.SELECT_NIGHT -> R.string.settings__theme_manager__title_night
        else -> error("Theme manager screen action must not be null")
    })
    previewFieldVisible = true

    val prefs by florisPreferenceModel()
    val context = LocalContext.current
    val extensionManager by context.extensionManager()
    val themeManager by context.themeManager()

    val indexedThemeExtensions by extensionManager.themes.observeAsNonNullState()
    val extGroupedThemes = remember(indexedThemeExtensions) {
        buildMap<String, List<ThemeExtensionComponent>> {
            for (ext in indexedThemeExtensions) {
                put(ext.meta.id, ext.themes)
            }
        }.mapValues { (_, configs) -> configs.sortedBy { it.id } } //it.label.toLowerCase() //it.label
    }

    fun getThemeIdPref() = when (action) {
        ThemeManagerScreenAction.SELECT_DAY -> prefs.theme.dayThemeId
        ThemeManagerScreenAction.SELECT_NIGHT -> prefs.theme.nightThemeId
    }

    fun setTheme(extId: String, componentId: String) {
        val extComponentName = ExtensionComponentName(extId, componentId)
        when (action) {
            ThemeManagerScreenAction.SELECT_DAY,
            ThemeManagerScreenAction.SELECT_NIGHT -> {
                getThemeIdPref().set(extComponentName)
            }
        }
    }

    val activeThemeId by when (action) {
        ThemeManagerScreenAction.SELECT_DAY,
        ThemeManagerScreenAction.SELECT_NIGHT -> getThemeIdPref().observeAsState()
    }

    content {
        DisposableEffect(activeThemeId) {
            themeManager.previewThemeId = activeThemeId
            onDispose {
                themeManager.previewThemeId = null
            }
        }
        val grayColor = LocalContentColor.current.copy(alpha = 0.56f)

        if (ATLEAST_API31_S) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 36.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_colors),
                    contentDescription = null,
                    tint = grayColor,
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "—",
                    color = grayColor,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = stringRes(id = com.goodwy.keyboard.strings.R.string.settings__uses_material_you__label),
                    color = grayColor,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        for ((extensionId, configs) in extGroupedThemes) key(extensionId) {
            val ext = extensionManager.getExtensionById(extensionId)!!
            FlorisOutlinedBox(
                modifier = Modifier.defaultFlorisOutlinedBox(),
                title = ext.meta.title,
                subtitle = extensionId,
            ) {
                for (config in configs) key(extensionId, config.id) {
                    JetPrefListItem(
                        modifier = Modifier.rippleClickable {
                            setTheme(extensionId, config.id)
                        },
                        icon = {
                            RadioButton(
                                selected = activeThemeId.extensionId == extensionId &&
                                    activeThemeId.componentId == config.id,
                                onClick = null,
                            )
                        },
                        text = config.label,
                        trailing = {
                            Row {
                                if (config.isMaterialYouAware && ATLEAST_API31_S) {
                                    Icon(
                                        modifier = Modifier.size(ButtonDefaults.IconSize),
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_colors),
                                        contentDescription = null,
                                        tint = grayColor,
                                    )
                                    Spacer(modifier = Modifier.size(12.dp))
                                }
                                Icon(
                                    modifier = Modifier.size(ButtonDefaults.IconSize),
                                    imageVector = if (config.isNightTheme) {
                                        Icons.Rounded.DarkMode
                                    } else {
                                        Icons.Rounded.LightMode
                                    },
                                    contentDescription = null,
                                    tint = grayColor,
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}
