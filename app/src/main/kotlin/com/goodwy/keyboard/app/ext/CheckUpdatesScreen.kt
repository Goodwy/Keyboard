package com.goodwy.keyboard.app.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.goodwy.keyboard.R
import com.goodwy.keyboard.extensionManager
import com.goodwy.keyboard.lib.compose.FlorisScreen
import com.goodwy.keyboard.lib.compose.stringRes

@Composable
fun CheckUpdatesScreen() = FlorisScreen {
    title = stringRes(R.string.ext__check_updates__title)

    val context = LocalContext.current
    val extensionManager by context.extensionManager()
    val extensionIndex = extensionManager.combinedExtensionList()

    content {
        UpdateBox(extensionIndex)
    }
}
