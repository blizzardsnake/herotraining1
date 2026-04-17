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

    const val DEFAULT_MODEL = "gemini-1.5-flash"

    val isConfigured: Boolean
        get() = BuildConfig.GEMINI_API_KEY.isNotBlank()

    private val apiKey: String get() = BuildConfig.GEMINI_API_KEY

    /** Fast, general-purpose model. Use for mentor chat and text-only tasks. */
    val flash: GenerativeModel by lazy { buildModel(DEFAULT_MODEL) }

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
     * Connectivity / credentials smoke-test. Asks the model to greet the user as their
     * chosen hero archetype. Returns the text response or a human-readable error.
     */
    suspend fun mentorGreeting(heroName: String?): Result<String> = runCatching {
        val persona = heroName?.let { " Ты играешь роль наставника по имени $it." } ?: ""
        val prompt = """
            $persona
            Поприветствуй воина который только что открыл приложение Протокол Героя.
            Одна-две короткие фразы. На русском. С драйвом и характером — как бы это сказал персонаж из боевика.
            Не используй markdown.
        """.trimIndent()
        flash.generateContent(prompt).text?.trim()
            ?: error("Модель вернула пустой ответ")
    }
}
