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

package com.goodwy.keyboard.ime.popup

import com.goodwy.keyboard.ime.keyboard.AbstractKeyData
import com.goodwy.keyboard.ime.text.key.KeyVariation
import com.goodwy.keyboard.lib.ext.ExtensionComponent
import kotlinx.serialization.Serializable

/**
 * An object which maps each base key to its extended popups. This can be done for each
 * key variation. [KeyVariation.ALL] is always the fallback for each key.
 */
typealias PopupMapping = Map<KeyVariation, Map<String, PopupSet<AbstractKeyData>>>

@Serializable
data class PopupMappingComponent(
    override val id: String,
    override val label: String = id,
    override val authors: List<String>,
    val mappingFile: String? = null,
) : ExtensionComponent {
    fun mappingFile() = "popupMappings/$id.json"
}
