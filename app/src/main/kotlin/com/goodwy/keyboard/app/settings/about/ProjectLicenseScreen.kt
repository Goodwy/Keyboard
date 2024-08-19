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

package com.goodwy.keyboard.app.settings.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.florisHorizontalScroll
import com.goodwy.keyboard.lib.compose.florisVerticalScroll
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.keyboard.lib.io.FlorisRef
import com.goodwy.keyboard.lib.io.loadTextAsset

@Composable
fun ProjectLicenseScreen() = FlorisScreen {
    title = stringRes(R.string.about__project_license__title)
    scrollable = false

    val context = LocalContext.current

    content {
        // Forcing LTR because the Apache 2.0 License shipped and displayed
        // is hard to read if rendered in RTL. Also it is in English so forcing
        // LTR here makes most sense.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            SelectionContainer(
                modifier = Modifier
                    .fillMaxSize()
                    .florisVerticalScroll()
                    .florisHorizontalScroll(),
            ) {
                val licenseText = FlorisRef.assets("license/project_license.txt").loadTextAsset(
                    context
                ).getOrElse {
                    stringRes(R.string.about__project_license__error_license_text_failed, "error_message" to (it.message ?: ""))
                }
                Text(
                    text = licenseText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    softWrap = false,
                )
            }
        }
    }
}
