package com.goodwy.keyboard.ime.media.emoji

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.stream.Collectors
import android.content.Context
import com.goodwy.keyboard.app.florisPreferenceModel
import com.goodwy.keyboard.ime.core.Subtype
import com.goodwy.keyboard.ime.editor.EditorContent
import com.goodwy.keyboard.ime.nlp.EmojiSuggestionCandidate
import com.goodwy.keyboard.ime.nlp.SuggestionCandidate
import com.goodwy.keyboard.ime.nlp.SuggestionProvider
import com.goodwy.keyboard.lib.FlorisLocale
import io.github.reactivecircus.cache4k.Cache

const val EMOJI_SUGGESTION_INDICATOR = ':'
const val EMOJI_SUGGESTION_MAX_COUNT = 5
private const val EMOJI_SUGGESTION_QUERY_MIN_LENGTH = 3

/**
 * Provides emoji suggestions within a text input context.
 *
 * This class handles the following tasks:
 * - Initializes and maintains a list of supported emojis.
 * - Generates and returns emoji suggestions based on user input and preferences.
 *
 * @param context The application context.
 */
class EmojiSuggestionProvider(private val context: Context) : SuggestionProvider {
    override val providerId = "org.florisboard.nlp.providers.emoji"

    private val prefs by florisPreferenceModel()
    private val lettersRegex = "^:[A-Za-z]*$".toRegex()

    private val cachedEmojiMappings = Cache.Builder().build<FlorisLocale, EmojiDataBySkinTone>()

    override suspend fun create() {
    }

    override suspend fun preload(subtype: Subtype) {
        subtype.locales().forEach { locale ->
            cachedEmojiMappings.get(locale) {
                EmojiData.get(context, locale).bySkinTone
            }
        }
    }

    override suspend fun suggest(
        subtype: Subtype,
        content: EditorContent,
        maxCandidateCount: Int,
        allowPossiblyOffensive: Boolean,
        isPrivateSession: Boolean
    ): List<SuggestionCandidate> {
        val preferredSkinTone = prefs.media.emojiPreferredSkinTone.get()
        val query = validateInputQuery(content.composingText) ?: return emptyList()
        val emojis = cachedEmojiMappings.get(subtype.primaryLocale)?.get(preferredSkinTone) ?: emptyList()
        val candidates = withContext(Dispatchers.Default) {
            emojis.parallelStream()
                .filter { emoji ->
                    emoji.name.contains(query, ignoreCase = true) &&
                        emoji.keywords.any { it.contains(query, ignoreCase = true) }
                }
                .limit(maxCandidateCount.toLong())
                .map { EmojiSuggestionCandidate(it) }
                .collect(Collectors.toList())
        }
        return candidates
    }

    override suspend fun notifySuggestionAccepted(subtype: Subtype, candidate: SuggestionCandidate) {
        // No-op
    }

    override suspend fun notifySuggestionReverted(subtype: Subtype, candidate: SuggestionCandidate) {
        // No-op
    }

    override suspend fun removeSuggestion(subtype: Subtype, candidate: SuggestionCandidate) = false

    override suspend fun getListOfWords(subtype: Subtype) = emptyList<String>()

    override suspend fun getFrequencyForWord(subtype: Subtype, word: String) = 0.0

    override suspend fun destroy() {
        cachedEmojiMappings.invalidateAll()
    }

    /**
     * Validates the user input query for emoji suggestions.
     */
    private fun validateInputQuery(composingText: CharSequence): String? {
        if (!composingText.startsWith(EMOJI_SUGGESTION_INDICATOR)) {
            return null
        }
        if (composingText.length <= EMOJI_SUGGESTION_QUERY_MIN_LENGTH) {
            return null
        }
        if (!lettersRegex.matches(composingText)) {
            return null
        }
        return composingText.substring(1)
    }
}
