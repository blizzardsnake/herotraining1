package com.herotraining.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.herotraining.BuildConfig

/**
 * Thin wrapper around Google's Generative AI SDK for Gemini.
 *
 * Why Flash and not Pro:
 *   - Flash has the generous free tier (15 RPM, 1500 RPD) — fits a small userbase easily.
 *   - Flash is ~3x faster than Pro and good enough for mentor chat / food parsing /
 *     workout generation. We can promote to Pro later for specific use cases if needed.
 *
 * The key lives in local.properties → BuildConfig.GEMINI_API_KEY. When the key is empty
 * (clean checkout, no credentials), [isConfigured] returns false so callers can show a
 * "setup needed" message instead of a cryptic auth error.
 */
object GeminiClient {

    /**
     * Ordered list of model names to try. Google rotates/retires model aliases in the
     * v1beta endpoint (e.g. the plain "gemini-1.5-flash" started 404'ing in 2025).
     * We attempt each in order — first that accepts the request wins.
     * Add new ones at the TOP of the list when Google ships them.
     */
    val MODEL_CANDIDATES = listOf(
        "gemini-2.0-flash-exp",     // generous free tier through 2025/2026
        "gemini-2.0-flash",         // stable if GA
        "gemini-1.5-flash-002",     // pinned recent 1.5 flash
        "gemini-1.5-flash-latest",
        "gemini-2.5-flash",         // optimistic — if it exists, try it
        "gemini-1.5-flash-8b"       // cheapest, smallest
    )

    val isConfigured: Boolean
        get() = BuildConfig.GEMINI_API_KEY.isNotBlank()

    private val apiKey: String get() = BuildConfig.GEMINI_API_KEY

    private fun buildModel(modelName: String): GenerativeModel {
        require(isConfigured) {
            "Gemini API key is missing. Add GEMINI_API_KEY to local.properties."
        }
        return GenerativeModel(
            modelName = modelName,
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.8f          // a bit creative for mentor persona
                topK = 40
                topP = 0.95f
                maxOutputTokens = 512       // hard cap for single-turn responses
            }
        )
    }

    /**
     * Try each candidate model in order until one responds. Returns (modelName, response).
     * Throws only if ALL candidates failed — last error is re-thrown so UI can show it.
     */
    private suspend fun generateWithFallback(prompt: String): Pair<String, String> {
        require(isConfigured) { "Gemini API key is missing — set GEMINI_API_KEY in local.properties." }
        var lastError: Throwable? = null
        for (modelName in MODEL_CANDIDATES) {
            try {
                val text = buildModel(modelName).generateContent(prompt).text?.trim().orEmpty()
                if (text.isNotBlank()) return modelName to text
                lastError = IllegalStateException("'$modelName' вернула пустой ответ")
            } catch (t: Throwable) {
                lastError = t
                // Keep trying — only bail if none of the candidates work.
            }
        }
        throw lastError ?: IllegalStateException("Все модели из MODEL_CANDIDATES недоступны")
    }

    /**
     * Connectivity / credentials smoke-test. Asks the model to greet the user as their
     * chosen hero archetype. Returns "[модель] текст" on success, or Error result.
     */
    suspend fun mentorGreeting(heroName: String?): Result<String> = runCatching {
        val persona = heroName?.let { " Ты играешь роль наставника по имени $it." } ?: ""
        val prompt = """
            $persona
            Поприветствуй воина который только что открыл приложение Протокол Героя.
            Одна-две короткие фразы. На русском. С драйвом и характером — как бы это сказал персонаж из боевика.
            Не используй markdown.
        """.trimIndent()
        val (model, text) = generateWithFallback(prompt)
        "[$model] $text"
    }
}
