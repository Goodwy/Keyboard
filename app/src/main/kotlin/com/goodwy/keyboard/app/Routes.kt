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

package com.goodwy.keyboard.app

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.goodwy.keyboard.app.devtools.AndroidLocalesScreen
import com.goodwy.keyboard.app.devtools.AndroidSettingsScreen
import com.goodwy.keyboard.app.devtools.DevtoolsScreen
import com.goodwy.keyboard.app.devtools.ExportDebugLogScreen
import com.goodwy.keyboard.app.ext.CheckUpdatesScreen
import com.goodwy.keyboard.app.ext.ExtensionEditScreen
import com.goodwy.keyboard.app.ext.ExtensionExportScreen
import com.goodwy.keyboard.app.ext.ExtensionHomeScreen
import com.goodwy.keyboard.app.ext.ExtensionImportScreen
import com.goodwy.keyboard.app.ext.ExtensionImportScreenType
import com.goodwy.keyboard.app.ext.ExtensionListScreen
import com.goodwy.keyboard.app.ext.ExtensionListScreenType
import com.goodwy.keyboard.app.ext.ExtensionViewScreen
import com.goodwy.keyboard.app.settings.HomeScreen
import com.goodwy.keyboard.app.settings.about.AboutScreen
import com.goodwy.keyboard.app.settings.about.ProjectLicenseScreen
import com.goodwy.keyboard.app.settings.about.ThirdPartyLicensesScreen
import com.goodwy.keyboard.app.settings.advanced.AdvancedScreen
import com.goodwy.keyboard.app.settings.advanced.BackupScreen
import com.goodwy.keyboard.app.settings.advanced.RestoreScreen
import com.goodwy.keyboard.app.settings.clipboard.ClipboardScreen
import com.goodwy.keyboard.app.settings.dictionary.DictionaryScreen
import com.goodwy.keyboard.app.settings.dictionary.UserDictionaryScreen
import com.goodwy.keyboard.app.settings.dictionary.UserDictionaryType
import com.goodwy.keyboard.app.settings.gestures.GesturesScreen
import com.goodwy.keyboard.app.settings.keyboard.InputFeedbackScreen
import com.goodwy.keyboard.app.settings.keyboard.KeyboardScreen
import com.goodwy.keyboard.app.settings.localization.LanguagePackManagerScreen
import com.goodwy.keyboard.app.settings.localization.LanguagePackManagerScreenAction
import com.goodwy.keyboard.app.settings.localization.LocalizationScreen
import com.goodwy.keyboard.app.settings.localization.SelectLocaleScreen
import com.goodwy.keyboard.app.settings.localization.SubtypeEditorScreen
import com.goodwy.keyboard.app.settings.media.MediaScreen
import com.goodwy.keyboard.app.settings.purchase.PurchaseScreen
import com.goodwy.keyboard.app.settings.smartbar.SmartbarScreen
import com.goodwy.keyboard.app.settings.theme.ThemeManagerScreen
import com.goodwy.keyboard.app.settings.theme.ThemeManagerScreenAction
import com.goodwy.keyboard.app.settings.theme.ThemeScreen
import com.goodwy.keyboard.app.settings.typing.TypingScreen
import com.goodwy.keyboard.app.setup.SetupScreen
import com.goodwy.lib.kotlin.curlyFormat

@Suppress("FunctionName", "ConstPropertyName")
object Routes {
    object Setup {
        const val Screen = "setup"
    }

    object Settings {
        const val Home = "settings"

        const val Localization = "settings/localization"
        const val SelectLocale = "settings/localization/select-locale"
        const val LanguagePackManager = "settings/localization/language-pack-manage/{action}"
        fun LanguagePackManager(action: LanguagePackManagerScreenAction) =
            LanguagePackManager.curlyFormat("action" to action.id)
        const val SubtypeAdd = "settings/localization/subtype/add"
        const val SubtypeEdit = "settings/localization/subtype/edit/{id}"
        fun SubtypeEdit(id: Long) = SubtypeEdit.curlyFormat("id" to id)

        const val Theme = "settings/theme"
        const val ThemeManager = "settings/theme/manage/{action}"
        fun ThemeManager(action: ThemeManagerScreenAction) = ThemeManager.curlyFormat("action" to action.id)

        const val Keyboard = "settings/keyboard"
        const val InputFeedback = "settings/keyboard/input-feedback"

        const val Smartbar = "settings/smartbar"

        const val Typing = "settings/typing"

        const val Dictionary = "settings/dictionary"
        const val UserDictionary = "settings/dictionary/user-dictionary/{type}"
        fun UserDictionary(type: UserDictionaryType) = UserDictionary.curlyFormat("type" to type.id)

        const val Gestures = "settings/gestures"

        const val Clipboard = "settings/clipboard"

        const val Media = "settings/media"

        const val Advanced = "settings/advanced"
        const val Backup = "settings/advanced/backup"
        const val Restore = "settings/advanced/restore"

        const val About = "settings/about"
        const val ProjectLicense = "settings/about/project-license"
        const val ThirdPartyLicenses = "settings/about/third-party-licenses"

        const val Purchase = "settings/purchase"
    }

    object Devtools {
        const val Home = "devtools"

        const val AndroidLocales = "devtools/android/locales"
        const val AndroidSettings = "devtools/android/settings/{name}"
        fun AndroidSettings(name: String) = AndroidSettings.curlyFormat("name" to name)

        const val ExportDebugLog = "export-debug-log"
    }

    object Ext {
        const val Home = "ext"

        const val List = "ext/list/{type}?showUpdate={showUpdate}"
        fun List(
            type: ExtensionListScreenType,
            showUpdate: Boolean
        ) = List.curlyFormat("type" to type.id, "showUpdate" to showUpdate)

        const val Edit = "ext/edit/{id}?create={serial_type}"
        fun Edit(id: String, serialType: String? = null): String {
            return Edit.curlyFormat("id" to id, "serial_type" to (serialType ?: ""))
        }

        const val Export = "ext/export/{id}"
        fun Export(id: String) = Export.curlyFormat("id" to id)

        const val Import = "ext/import/{type}?uuid={uuid}"
        fun Import(
            type: ExtensionImportScreenType,
            uuid: String?,
        ) = Import.curlyFormat("type" to type.id, "uuid" to uuid.toString())

        const val View = "ext/view/{id}"
        fun View(id: String) = View.curlyFormat("id" to id)

        const val CheckUpdates = "ext/check-updates"
    }

    @Composable
    fun AppNavHost(
        modifier: Modifier,
        navController: NavHostController,
        startDestination: String,
    ) {
        fun NavGraphBuilder.composableWithDeepLink(
            route: String,
            content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit),
        ) {
            composable(
                route = route,
                deepLinks = listOf(navDeepLink { uriPattern = "ui://florisboard/$route" }),
                content = content,
            )
        }

        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                slideIn { IntOffset(it.width, 0) } + fadeIn()
            },
            exitTransition = {
                slideOut { IntOffset(-it.width, 0) } + fadeOut()
            },
            popEnterTransition = {
                slideIn { IntOffset(-it.width, 0) } + fadeIn()
            },
            popExitTransition = {
                slideOut { IntOffset(it.width, 0) } + fadeOut()
            }
        ) {
            composable(Setup.Screen) { SetupScreen() }

            composableWithDeepLink(Settings.Home) { HomeScreen() }

            composableWithDeepLink(Settings.Localization) { LocalizationScreen() }
            composableWithDeepLink(Settings.SelectLocale) { SelectLocaleScreen() }
            composableWithDeepLink(Settings.LanguagePackManager) { navBackStack ->
                val action = navBackStack.arguments?.getString("action")?.let { actionId ->
                    LanguagePackManagerScreenAction.entries.firstOrNull { it.id == actionId }
                }
                LanguagePackManagerScreen(action)
            }
            composableWithDeepLink(Settings.SubtypeAdd) { SubtypeEditorScreen(null) }
            composableWithDeepLink(Settings.SubtypeEdit) { navBackStack ->
                val id = navBackStack.arguments?.getString("id")?.toLongOrNull()
                SubtypeEditorScreen(id)
            }

            composableWithDeepLink(Settings.Theme) { ThemeScreen() }
            composableWithDeepLink(Settings.ThemeManager) { navBackStack ->
                val action = navBackStack.arguments?.getString("action")?.let { actionId ->
                    ThemeManagerScreenAction.entries.firstOrNull { it.id == actionId }
                }
                ThemeManagerScreen(action)
            }

            composableWithDeepLink(Settings.Keyboard) { KeyboardScreen() }
            composableWithDeepLink(Settings.InputFeedback) { InputFeedbackScreen() }

            composableWithDeepLink(Settings.Smartbar) { SmartbarScreen() }

            composableWithDeepLink(Settings.Typing) { TypingScreen() }

            composableWithDeepLink(Settings.Dictionary) { DictionaryScreen() }
            composableWithDeepLink(Settings.UserDictionary) { navBackStack ->
                val type = navBackStack.arguments?.getString("type")?.let { typeId ->
                    UserDictionaryType.entries.firstOrNull { it.id == typeId }
                }
                UserDictionaryScreen(type!!)
            }

            composableWithDeepLink(Settings.Gestures) { GesturesScreen() }

            composableWithDeepLink(Settings.Clipboard) { ClipboardScreen() }

            composableWithDeepLink(Settings.Media) { MediaScreen() }

            composableWithDeepLink(Settings.Advanced) { AdvancedScreen() }
            composableWithDeepLink(Settings.Backup) { BackupScreen() }
            composableWithDeepLink(Settings.Restore) { RestoreScreen() }

            composableWithDeepLink(Settings.About) { AboutScreen() }
            composableWithDeepLink(Settings.ProjectLicense) { ProjectLicenseScreen() }
            composableWithDeepLink(Settings.ThirdPartyLicenses) { ThirdPartyLicensesScreen() }

            composableWithDeepLink(Settings.Purchase) { PurchaseScreen() }

            composableWithDeepLink(Devtools.Home) { DevtoolsScreen() }
            composableWithDeepLink(Devtools.AndroidLocales) { AndroidLocalesScreen() }
            composableWithDeepLink(Devtools.AndroidSettings) { navBackStack ->
                val name = navBackStack.arguments?.getString("name")
                AndroidSettingsScreen(name)
            }
            composableWithDeepLink(Devtools.ExportDebugLog) { ExportDebugLogScreen() }

            composableWithDeepLink(Ext.Home) { ExtensionHomeScreen() }
            composableWithDeepLink(Ext.List) { navBackStack ->
                val type = navBackStack.arguments?.getString("type")?.let { typeId ->
                    ExtensionListScreenType.entries.firstOrNull { it.id == typeId }
                } ?: error("unknown type")
                val showUpdate = navBackStack.arguments?.getString("showUpdate")
                ExtensionListScreen(type, showUpdate == "true")
            }
            composableWithDeepLink(Ext.Edit) { navBackStack ->
                val extensionId = navBackStack.arguments?.getString("id")
                val serialType = navBackStack.arguments?.getString("serial_type")
                ExtensionEditScreen(
                    id = extensionId.toString(),
                    createSerialType = serialType.takeIf { !it.isNullOrBlank() },
                )
            }
            composableWithDeepLink(Ext.Export) { navBackStack ->
                val extensionId = navBackStack.arguments?.getString("id")
                ExtensionExportScreen(id = extensionId.toString())
            }
            composableWithDeepLink(Ext.Import) { navBackStack ->
                val type = navBackStack.arguments?.getString("type")?.let { typeId ->
                    ExtensionImportScreenType.entries.firstOrNull { it.id == typeId }
                } ?: ExtensionImportScreenType.EXT_ANY
                val uuid = navBackStack.arguments?.getString("uuid")?.takeIf { it != "null" }
                ExtensionImportScreen(type, uuid)
            }
            composableWithDeepLink(Ext.View) { navBackStack ->
                val extensionId = navBackStack.arguments?.getString("id")
                ExtensionViewScreen(id = extensionId.toString())
            }
            composableWithDeepLink(Ext.CheckUpdates) {
                CheckUpdatesScreen()
            }
        }
    }
}
