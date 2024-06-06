/*
 * Copyright (C) 2024 Goodwy
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

package com.goodwy.keyboard.app.settings.purchase

import androidx.compose.runtime.saveable.Saver
import com.goodwy.keyboard.lib.ext.ExtensionComponentName
import dev.patrickgold.jetpref.datastore.model.PreferenceSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.florisboard.lib.kotlin.tryOrNull

@Serializable(with = Product.Serializer::class)
data class Product(val id: String, var price: String, var purchased: Boolean = false, var valid: Boolean = false) {

    override fun toString(): String {
        return "Product { id=$id, price=$price, purchased=$purchased, valid=$valid }"
    }

    object SerializerList : PreferenceSerializer<List<Product>> {
        private const val DELIMITER = ";"

        override fun serialize(value: List<Product>): String {
            return value.joinToString(DELIMITER) { it.id }
        }

        override fun deserialize(value: String): List<Product> {
            return value.split(DELIMITER).mapNotNull { rawValue ->
                rawValue.trim().let { if (it.isBlank()) null else Product(it.trim(), "", false) }
            }
        }
    }

    companion object {
        private const val DELIMITER = ";"

        fun from(str: String): Product {
            val data = str.split(DELIMITER)
            check(data.size == 2) { "Product must be of format <id>;<price>" }
            return Product(data[0], data[1])
        }

        val Saver = Saver<Product?, String>(
            save = { it.toString() },
            restore = { tryOrNull { from(it) } },
        )
    }

    object Serializer : PreferenceSerializer<Product>, KSerializer<Product> {
        override val descriptor = PrimitiveSerialDescriptor("Product", PrimitiveKind.STRING)

        override fun serialize(value: Product): String {
            return value.toString()
        }

        override fun serialize(encoder: Encoder, value: Product) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(value: String): Product? {
            return tryOrNull { from(value) }
        }

        override fun deserialize(decoder: Decoder): Product {
            return from(decoder.decodeString())
        }
    }
}
