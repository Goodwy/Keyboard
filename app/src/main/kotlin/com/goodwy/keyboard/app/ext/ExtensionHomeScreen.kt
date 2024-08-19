package com.goodwy.keyboard.app.ext

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.BuildConfig
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.extensionManager
import com.goodwy.keyboard.lib.util.launchUrl
import com.goodwy.keyboard.lib.compose.FlorisOutlinedBox
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.FlorisTextButton
import com.goodwy.keyboard.lib.compose.defaultFlorisOutlinedBox
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.Preference
import dev.patrickgold.jetpref.datastore.ui.PreferenceGroup

@Composable
fun ExtensionHomeScreen() = FlorisScreen {
    title = stringRes(R.string.ext__home__title)
    previewFieldVisible = false

    val context = LocalContext.current
    val navController = LocalNavController.current
    val extensionManager by context.extensionManager()
    val extensionIndex = extensionManager.combinedExtensionList()

    content {
        FlorisOutlinedBox(
            modifier = Modifier.defaultFlorisOutlinedBox(),
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
                text = stringRes(id = R.string.ext__home__info),
                style = MaterialTheme.typography.bodySmall,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
            ) {
                FlorisTextButton(
                    onClick = {
                        context.launchUrl("https://${BuildConfig.FLADDONS_STORE_URL}/")
                    },
                    icon = Icons.Default.Shop,
                    text = stringRes(id = R.string.ext__home__visit_store),
                )
                Spacer(modifier = Modifier.weight(1f))
                FlorisTextButton(
                    onClick = {
                        navController.navigate(Routes.Ext.Import(ExtensionImportScreenType.EXT_ANY, null))
                    },
                    icon = Icons.AutoMirrored.Filled.Input,
                    text = stringRes(R.string.action__import),
                )
            }
        }

        UpdateBox(extensionIndex = extensionIndex)

        PreferenceGroup(title = stringRes(id = R.string.ext__home__visit_store)) {
            Preference(
                icon = Icons.Default.Palette,
                title = stringRes(R.string.ext__list__ext_theme),
                onClick = {
                    navController.navigate(Routes.Ext.List(ExtensionListScreenType.EXT_THEME,false))
                },
            )
            Preference(
                icon = Icons.Default.Keyboard,
                title = stringRes(R.string.ext__list__ext_keyboard),
                onClick = {
                    navController.navigate(Routes.Ext.List(ExtensionListScreenType.EXT_KEYBOARD,false))
                },
            )
            Preference(
                icon = Icons.Default.Language,
                title = stringRes(R.string.ext__list__ext_languagepack),
                onClick = {
                    navController.navigate(Routes.Ext.List(ExtensionListScreenType.EXT_LANGUAGEPACK,false))
                },
            )
        }
    }
}
