package com.goodwy.keyboard.ime.input

import androidx.compose.runtime.Composable
import com.goodwy.keyboard.R
import com.goodwy.keyboard.lib.compose.stringRes
import dev.patrickgold.jetpref.datastore.ui.listPrefEntries

enum class CapitalizationBehavior {
    CAPSLOCK_BY_DOUBLE_TAP,
    CAPSLOCK_BY_CYCLE;

    companion object {
        @Composable
        fun listEntries() = listPrefEntries {
            entry(
                key = CAPSLOCK_BY_DOUBLE_TAP,
                label = stringRes(R.string.enum__capitalization_behavior__capslock_by_double_tap),
            )
            entry(
                key = CAPSLOCK_BY_CYCLE,
                label = stringRes(R.string.enum__capitalization_behavior__capslock_by_cycle),
            )
        }
    }
}
