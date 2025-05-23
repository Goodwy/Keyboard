package com.goodwy.keyboard.ime.text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.extensionManager
import com.goodwy.keyboard.ime.keyboard.FlorisImeSizing
import com.goodwy.keyboard.keyboardManager
import com.goodwy.keyboard.lib.util.launchUrl
import com.goodwy.lib.snygg.SnyggPropertySet
import com.goodwy.lib.snygg.ui.SnyggButton
import com.goodwy.lib.snygg.ui.SnyggSurface
import com.goodwy.lib.snygg.ui.solidColor
import com.goodwy.lib.snygg.value.SnyggRoundedCornerDpShapeValue
import com.goodwy.lib.snygg.value.SnyggSolidColorValue

@Composable
fun HowDidWeGetHere() {
    val context = LocalContext.current
    val extensionManager by context.extensionManager()
    val keyboardManager by context.keyboardManager()

    val style = SnyggPropertySet(mapOf(
        "background" to SnyggSolidColorValue(Color.Yellow),
        "foreground" to SnyggSolidColorValue(Color.Black),
        "shape" to SnyggRoundedCornerDpShapeValue(16.dp, 16.dp, 16.dp, 16.dp, RoundedCornerShape(16.dp)),
    ))

    @Composable
    fun ColoredText(text: String) {
        Text(
            text = text,
            color = style.foreground.solidColor(context),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(FlorisImeSizing.keyboardUiHeight())
            .padding(8.dp),
    ) {
        SnyggSurface(style = style) {
            Column(modifier = Modifier.padding(8.dp)) {
                ColoredText(text = "Challenge Complete! - How did we get here?\n")
                ColoredText(text = "You landed in a state which shouldn't be reachable, possibly related to the \"All keys invisible\" bug. Please report this bug and the steps to reproduce to the devs using the button below. Thanks!")
                Row {
                    SnyggButton(
                        onClick = {
                            keyboardManager.activeState.rawValue = 0u
                            extensionManager.init()
                        },
                        text = "Try reset keyboard",
                        style = style,
                    )
                    SnyggButton(
                        onClick = {
                            context.launchUrl("https://github.com/Goodwy/Keyboard/issues")
                        },
                        text = "Report bug to devs",
                        style = style,
                    )
                }
            }
        }
    }
}
