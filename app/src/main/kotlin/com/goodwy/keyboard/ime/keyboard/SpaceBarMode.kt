package com.goodwy.keyboard.ime.keyboard

import androidx.compose.runtime.Composable
import com.goodwy.keyboard.R
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.listPrefEntries

enum class SpaceBarMode {
    NOTHING,
    CURRENT_LANGUAGE,
    SPACE_BAR_KEY;

    companion object {
        @Composable
        fun listEntries() = listPrefEntries {
            entry(
                key = NOTHING,
                label = stringRes(R.string.enum__space_bar_mode__nothing),
            )
            entry(
                key = CURRENT_LANGUAGE,
                label = stringRes(R.string.enum__space_bar_mode__current_language),
            )
            entry(
                key = SPACE_BAR_KEY,
                label = stringRes(R.string.enum__space_bar_mode__space_bar_key),
            )
        }
    }
}
