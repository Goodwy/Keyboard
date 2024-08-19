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

package com.goodwy.keyboard.app.settings.typing

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.lib.compose.FlorisCanvasIcon
import com.goodwy.keyboard.lib.compose.FlorisErrorCard
import com.goodwy.keyboard.lib.compose.FlorisSimpleCard
import com.goodwy.keyboard.lib.compose.FlorisWarningCard
import com.goodwy.keyboard.lib.compose.observeAsState
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.keyboard.lib.util.launchActivity
import com.goodwy.lib.android.AndroidSettings

@Composable
fun SpellCheckerServiceSelector(florisSpellCheckerEnabled: MutableState<Boolean>) {
    val context = LocalContext.current

    val systemSpellCheckerId by AndroidSettings.Secure.observeAsState(
        key = "selected_spell_checker",
        foregroundOnly = true,
    )
    val systemSpellCheckerEnabled by AndroidSettings.Secure.observeAsState(
        key = "spell_checker_enabled",
        foregroundOnly = true,
    )
    val systemSpellCheckerPkgName = remember(systemSpellCheckerId) {
        runCatching {
            ComponentName.unflattenFromString(systemSpellCheckerId!!)!!.packageName
        }.getOrDefault("null")
    }
    val openSystemSpellCheckerSettings = {
        val componentToLaunch = ComponentName(
            "com.android.settings",
            "com.android.settings.Settings\$SpellCheckersSettingsActivity",
        )
        context.launchActivity {
            it.addCategory(Intent.CATEGORY_DEFAULT)
            it.component = componentToLaunch
        }
    }
    florisSpellCheckerEnabled.value =
        systemSpellCheckerEnabled == "1" &&
        systemSpellCheckerPkgName == context.packageName

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        if (systemSpellCheckerEnabled == "1") {
            if (systemSpellCheckerId == null) {
                FlorisWarningCard(
                    text = stringRes(R.string.pref__spelling__active_spellchecker__summary_none),
                    onClick = openSystemSpellCheckerSettings,
                    corners = 6.dp,
                )
            } else {
                var spellCheckerIcon: Drawable?
                var spellCheckerLabel = "Unknown"
                try {
                    val pm = context.packageManager
                    val remoteAppInfo = pm.getApplicationInfo(systemSpellCheckerPkgName, 0)
                    spellCheckerIcon = pm.getApplicationIcon(remoteAppInfo)
                    spellCheckerLabel = pm.getApplicationLabel(remoteAppInfo).toString()
                } catch (e: Exception) {
                    spellCheckerIcon = null
                }
                Spacer(modifier = Modifier.size(8.dp))
                FlorisSimpleCard(
                    icon = {
                        if (spellCheckerIcon != null) {
                            FlorisCanvasIcon(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .requiredSize(32.dp),
                                drawable = spellCheckerIcon,
                            )
                        } else {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .requiredSize(32.dp),
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = null,
                            )
                        }
                    },
                    text = spellCheckerLabel,
                    secondaryText = systemSpellCheckerPkgName,
                    contentPadding = PaddingValues(all = 8.dp),
                    onClick = openSystemSpellCheckerSettings,
                    corners = 6.dp,
                    iconSpacer = 8.dp,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                )
                Spacer(modifier = Modifier.size(6.dp))
            }
        } else {
            Spacer(modifier = Modifier.size(6.dp))
            FlorisErrorCard(
                text = stringRes(R.string.pref__spelling__active_spellchecker__summary_disabled),
                onClick = openSystemSpellCheckerSettings,
                corners = 6.dp
            )
            Spacer(modifier = Modifier.size(6.dp))
        }
    }
}
