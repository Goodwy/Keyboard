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

package com.goodwy.keyboard

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import androidx.core.os.UserManagerCompat
import com.goodwy.keyboard.app.RuStoreModule
import com.goodwy.keyboard.app.florisPreferenceModel
import com.goodwy.keyboard.ime.clipboard.ClipboardManager
import com.goodwy.keyboard.ime.core.SubtypeManager
import com.goodwy.keyboard.ime.dictionary.DictionaryManager
import com.goodwy.keyboard.ime.editor.EditorInstance
import com.goodwy.keyboard.ime.keyboard.KeyboardManager
import com.goodwy.keyboard.ime.media.emoji.FlorisEmojiCompat
import com.goodwy.keyboard.ime.nlp.NlpManager
import com.goodwy.keyboard.ime.text.gestures.GlideTypingManager
import com.goodwy.keyboard.ime.theme.FlorisImeTheme
import com.goodwy.keyboard.ime.theme.ThemeManager
import com.goodwy.keyboard.lib.cache.CacheManager
import com.goodwy.keyboard.lib.crashutility.CrashUtility
import com.goodwy.keyboard.lib.devtools.Flog
import com.goodwy.keyboard.lib.devtools.LogTopic
import com.goodwy.keyboard.lib.devtools.flogError
import com.goodwy.keyboard.lib.ext.ExtensionManager
import com.goodwy.lib.kotlin.io.deleteContentsRecursively
import com.goodwy.lib.kotlin.tryOrNull
import com.goodwy.libnative.dummyAdd
import dev.patrickgold.jetpref.datastore.JetPref
import java.lang.ref.WeakReference

/**
 * Global weak reference for the [FlorisApplication] class. This is needed as in certain scenarios an application
 * reference is needed, but the Android framework hasn't finished setting up
 */
private var FlorisApplicationReference = WeakReference<FlorisApplication?>(null)

@Suppress("unused")
class FlorisApplication : Application() {
    companion object {
        init {
            try {
                System.loadLibrary("fl_native")
            } catch (_: Exception) {
            }
            FlorisImeTheme.init()
        }
    }

    private val prefs by florisPreferenceModel()
    private val mainHandler by lazy { Handler(mainLooper) }

    val cacheManager = lazy { CacheManager(this) }
    val clipboardManager = lazy { ClipboardManager(this) }
    val editorInstance = lazy { EditorInstance(this) }
    val extensionManager = lazy { ExtensionManager(this) }
    val glideTypingManager = lazy { GlideTypingManager(this) }
    val keyboardManager = lazy { KeyboardManager(this) }
    val nlpManager = lazy { NlpManager(this) }
    val subtypeManager = lazy { SubtypeManager(this) }
    val themeManager = lazy { ThemeManager(this) }

    override fun onCreate() {
        super.onCreate()
        FlorisApplicationReference = WeakReference(this)
        try {
            JetPref.configure(saveIntervalMs = 500)
            Flog.install(
                context = this,
                isFloggingEnabled = BuildConfig.DEBUG,
                flogTopics = LogTopic.ALL,
                flogLevels = Flog.LEVEL_ALL,
                flogOutputs = Flog.OUTPUT_CONSOLE,
            )
            CrashUtility.install(this)
            FlorisEmojiCompat.init(this)
            flogError { "dummy result: ${dummyAdd(3,4)}" }

            if (!UserManagerCompat.isUserUnlocked(this)) {
                cacheDir?.deleteContentsRecursively()
                extensionManager.value.init()
                registerReceiver(BootComplete(), IntentFilter(Intent.ACTION_USER_UNLOCKED))
                return
            }

            init()
        } catch (e: Exception) {
            CrashUtility.stageException(e)
            return
        }

        RuStoreModule.install(this, 1)
    }

    fun init() {
        cacheDir?.deleteContentsRecursively()
        prefs.initializeBlocking(this)
        extensionManager.value.init()
        clipboardManager.value.initializeForContext(this)
        DictionaryManager.init(this)
    }

    private inner class BootComplete : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            if (intent.action == Intent.ACTION_USER_UNLOCKED) {
                try {
                    unregisterReceiver(this)
                } catch (e: Exception) {
                    flogError { e.toString() }
                }
                mainHandler.post { init() }
            }
        }
    }
}

private tailrec fun Context.florisApplication(): FlorisApplication {
    return when (this) {
        is FlorisApplication -> this
        is ContextWrapper -> when {
            this.baseContext != null -> this.baseContext.florisApplication()
            else -> FlorisApplicationReference.get()!!
        }
        else -> tryOrNull { this.applicationContext as FlorisApplication } ?: FlorisApplicationReference.get()!!
    }
}

fun Context.appContext() = lazyOf(this.florisApplication())

fun Context.cacheManager() = this.florisApplication().cacheManager

fun Context.clipboardManager() = this.florisApplication().clipboardManager

fun Context.editorInstance() = this.florisApplication().editorInstance

fun Context.extensionManager() = this.florisApplication().extensionManager

fun Context.glideTypingManager() = this.florisApplication().glideTypingManager

fun Context.keyboardManager() = this.florisApplication().keyboardManager

fun Context.nlpManager() = this.florisApplication().nlpManager

fun Context.subtypeManager() = this.florisApplication().subtypeManager

fun Context.themeManager() = this.florisApplication().themeManager
