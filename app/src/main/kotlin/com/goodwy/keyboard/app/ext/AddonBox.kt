package com.goodwy.keyboard.app.ext

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.goodwy.keyboard.R
import com.goodwy.keyboard.app.LocalNavController
import com.goodwy.keyboard.app.Routes
import com.goodwy.keyboard.lib.compose.FlorisOutlinedBox
import com.goodwy.keyboard.lib.compose.FlorisTextButton
import com.goodwy.keyboard.lib.compose.defaultFlorisOutlinedBox
import com.goodwy.keyboard.lib.compose.stringRes
import com.goodwy.keyboard.lib.ext.Extension
import com.goodwy.keyboard.lib.ext.generateUpdateUrl
import com.goodwy.keyboard.lib.util.launchUrl
import com.goodwy.lib.kotlin.curlyFormat

@Composable
fun UpdateBox(extensionIndex: List<Extension>) {
    val context = LocalContext.current
    FlorisOutlinedBox(
        modifier = Modifier.defaultFlorisOutlinedBox(),
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
            text = stringRes(id = R.string.ext__update_box__internet_permission_hint),
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
        ) {
            FlorisTextButton(
                onClick = {
                    context.launchUrl(extensionIndex.generateUpdateUrl())
                },
                icon = Icons.Outlined.FileDownload,
                text = stringRes(id = R.string.ext__update_box__search_for_updates)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun AddonManagementReferenceBox(
    type: ExtensionListScreenType
) {
    val navController = LocalNavController.current

    FlorisOutlinedBox(
        modifier = Modifier.defaultFlorisOutlinedBox(),
        title = stringRes(id = R.string.ext__addon_management_box__managing_placeholder).curlyFormat(
            "extensions" to type.let { stringRes(id = it.titleResId).lowercase() }
        )
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            text = stringRes(id = R.string.ext__addon_management_box__addon_manager_info),
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            FlorisTextButton(
                onClick = {
                    val route = Routes.Ext.List(type, showUpdate = true)
                    navController.navigate(
                        route
                    )
                },
                icon = Icons.Default.Shop,
                text = stringRes(id = R.string.ext__addon_management_box__go_to_page).curlyFormat(
                    "ext_home_title" to stringRes(type.titleResId),
                ),
            )
        }
    }
}
