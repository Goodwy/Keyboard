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

package com.goodwy.keyboard.lib.ext

import android.content.Context
import android.net.Uri
import android.os.FileObserver
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import com.goodwy.keyboard.appContext
import com.goodwy.keyboard.ime.keyboard.KeyboardExtension
import com.goodwy.keyboard.ime.nlp.LanguagePackExtension
import com.goodwy.keyboard.ime.text.composing.Appender
import com.goodwy.keyboard.ime.text.composing.Composer
import com.goodwy.keyboard.ime.text.composing.HangulUnicode
import com.goodwy.keyboard.ime.text.composing.KanaUnicode
import com.goodwy.keyboard.ime.text.composing.WithRules
import com.goodwy.keyboard.ime.theme.ThemeExtension
import com.goodwy.keyboard.lib.devtools.LogTopic
import com.goodwy.keyboard.lib.devtools.flogDebug
import com.goodwy.keyboard.lib.devtools.flogError
import com.goodwy.keyboard.lib.io.FlorisRef
import com.goodwy.keyboard.lib.io.ZipUtils
import com.goodwy.keyboard.lib.io.delete
import com.goodwy.keyboard.lib.io.listDirs
import com.goodwy.keyboard.lib.io.listFiles
import com.goodwy.keyboard.lib.io.loadJsonAsset
import com.goodwy.keyboard.lib.observeAsNonNullState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import com.goodwy.lib.android.FileObserver
import com.goodwy.lib.kotlin.io.FsFile
import com.goodwy.lib.kotlin.io.writeJson
import com.goodwy.lib.kotlin.throwOnFailure

@OptIn(ExperimentalSerializationApi::class)
val ExtensionJsonConfig = Json {
    classDiscriminator = "$"
    encodeDefaults = true
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    prettyPrintIndent = "  "
    encodeDefaults = false
    serializersModule = SerializersModule {
        polymorphic(Extension::class) {
            subclass(KeyboardExtension::class, KeyboardExtension.serializer())
            subclass(ThemeExtension::class, ThemeExtension.serializer())
            subclass(LanguagePackExtension::class, LanguagePackExtension.serializer())
        }
        polymorphic(Composer::class) {
            subclass(Appender::class, Appender.serializer())
            subclass(HangulUnicode::class, HangulUnicode.serializer())
            subclass(KanaUnicode::class, KanaUnicode.serializer())
            subclass(WithRules::class, WithRules.serializer())
            defaultDeserializer { Appender.serializer() }
        }
    }
}

class ExtensionManager(context: Context) {
    companion object {
        const val IME_KEYBOARD_PATH = "ime/keyboard"
        const val IME_THEME_PATH = "ime/theme"
        const val IME_LANGUAGEPACK_PATH = "ime/languagepack"

        private const val FILE_OBSERVER_MASK =
            FileObserver.CLOSE_WRITE or FileObserver.DELETE or FileObserver.MOVED_FROM or FileObserver.MOVED_TO
    }

    private val appContext by context.appContext()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    val keyboardExtensions = ExtensionIndex(KeyboardExtension.serializer(), IME_KEYBOARD_PATH)
    val themes = ExtensionIndex(ThemeExtension.serializer(), IME_THEME_PATH)
    val languagePacks = ExtensionIndex(LanguagePackExtension.serializer(), IME_LANGUAGEPACK_PATH)

    @Composable
    fun combinedExtensionList() = listOf(keyboardExtensions.observeAsNonNullState(), themes.observeAsNonNullState(), languagePacks.observeAsNonNullState()).map {
        it.value
    }.flatten()

    fun init() {
        keyboardExtensions.init()
        themes.init()
        languagePacks.init()
    }

    fun import(ext: Extension) {
        val workingDir = requireNotNull(ext.workingDir) { "No working dir specified" }
        val extFileName = ExtensionDefaults.createFlexName(ext.meta.id)
        val relGroupPath = when (ext) {
            is KeyboardExtension -> IME_KEYBOARD_PATH
            is ThemeExtension -> IME_THEME_PATH
            is LanguagePackExtension -> IME_LANGUAGEPACK_PATH
            else -> error("Unknown extension type")
        }
        ext.sourceRef = FlorisRef.internal(relGroupPath).subRef(extFileName)
        FsFile(workingDir, ExtensionDefaults.MANIFEST_FILE_NAME).writeJson(ext, ExtensionJsonConfig)
        writeExtension(ext).throwOnFailure()
        ext.unload(appContext)
        ext.workingDir = null
    }

    fun export(ext: Extension, uri: Uri) {
        ext.load(appContext).throwOnFailure()
        val workingDir = requireNotNull(ext.workingDir) { "No working dir specified" }
        ZipUtils.zip(appContext, workingDir, uri).throwOnFailure()
        ext.unload(appContext)
    }

    private fun writeExtension(ext: Extension) = runCatching {
        val workingDir = requireNotNull(ext.workingDir) { "No working dir specified" }
        val sourceRef = requireNotNull(ext.sourceRef) { "No source ref specified" }
        ZipUtils.zip(appContext, workingDir, sourceRef).throwOnFailure()
    }

    fun getExtensionById(id: String): Extension? {
        keyboardExtensions.value?.find { it.meta.id == id }?.let { return it }
        themes.value?.find { it.meta.id == id }?.let { return it }
        languagePacks.value?.find { it.meta.id == id }?.let { return it }
        return null
    }

    fun canDelete(ext: Extension): Boolean {
        return ext.sourceRef?.isInternal == true
    }

    fun delete(ext: Extension) {
        check(canDelete(ext)) { "Cannot delete extension!" }
        ext.unload(appContext)
        ext.sourceRef!!.delete(appContext)
    }

    inner class ExtensionIndex<T : Extension>(
        private val serializer: KSerializer<T>,
        modulePath: String,
    ) : LiveData<List<T>>() {

        private val assetsModuleRef = FlorisRef.assets(modulePath)
        private val internalModuleRef = FlorisRef.internal(modulePath)
        var internalModuleDir = internalModuleRef.absoluteFile(appContext)

        private var staticExtensions = listOf<T>()
        private var fileObserver: FileObserver? = null
        private val initGuard = Mutex()
        private val refreshGuard = Mutex()

        init {
            value = emptyList()
            ioScope.launch {
                refreshGuard.withLock {
                    staticExtensions = indexAssetsModule()
                }
            }
        }

        fun init() {
            ioScope.launch {
                initGuard.withLock {
                    // Update internal module dir to actual path and make directory if not exists
                    internalModuleDir = internalModuleRef.absoluteFile(appContext)
                    internalModuleDir.mkdirs()

                    // Refresh index to new state
                    refresh()

                    // Stop watching on old file observer if one exists and start new observer on new path
                    fileObserver?.stopWatching()
                    fileObserver = FileObserver(internalModuleDir, FILE_OBSERVER_MASK) { event, path ->
                        flogDebug(LogTopic.EXT_INDEXING) { "FileObserver.onEvent { event=$event path=$path }" }
                        if (path == null) return@FileObserver
                        ioScope.launch {
                            refresh()
                        }
                    }.also { it.startWatching() }
                }
            }
        }

        private suspend fun refresh() {
            refreshGuard.withLock {
                val dynamicExtensions = staticExtensions + indexInternalModule()
                postValue(dynamicExtensions)
            }
        }

        private fun indexAssetsModule(): List<T> {
            val list = mutableListOf<T>()
            assetsModuleRef.listDirs(appContext).fold(
                onSuccess = { extRefs ->
                    for (extRef in extRefs) {
                        val fileRef = extRef.subRef(ExtensionDefaults.MANIFEST_FILE_NAME)
                        fileRef.loadJsonAsset(appContext, serializer, ExtensionJsonConfig).fold(
                            onSuccess = { ext ->
                                ext.sourceRef = extRef
                                list.add(ext)
                            },
                            onFailure = { error ->
                                flogError { error.toString() }
                            },
                        )
                    }
                },
                onFailure = { error ->
                    flogError { error.toString() }
                },
            )
            return list.toList()
        }

        private fun indexInternalModule(): List<T> {
            val list = mutableListOf<T>()
            internalModuleRef.listFiles(appContext).fold(
                onSuccess = { extRefs ->
                    for (extRef in extRefs) {
                        val fileRef = extRef.absoluteFile(appContext)
                        if (!fileRef.name.endsWith(ExtensionDefaults.FILE_EXTENSION)) {
                            continue
                        }
                        ZipUtils.readFileFromArchive(appContext, extRef, ExtensionDefaults.MANIFEST_FILE_NAME).fold(
                            onSuccess = { metaStr ->
                                loadJsonAsset(metaStr, serializer, ExtensionJsonConfig).fold(
                                    onSuccess = { ext ->
                                        ext.sourceRef = extRef
                                        list.add(ext)
                                    },
                                    onFailure = { error ->
                                        flogError { error.toString() }
                                    },
                                )
                            },
                            onFailure = { error ->
                                flogError { error.toString() }
                            },
                        )
                    }
                },
                onFailure = { error ->
                    flogError { error.toString() }
                },
            )
            return list.toList()
        }
    }
}
