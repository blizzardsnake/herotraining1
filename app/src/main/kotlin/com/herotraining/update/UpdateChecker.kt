package com.herotraining.update

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

/** Minimal subset of the GitHub /releases/latest payload. */
@Serializable
private data class GhRelease(
    val tag_name: String,
    val name: String? = null,
    val body: String? = null,
    val html_url: String,
    val prerelease: Boolean = false,
    val draft: Boolean = false,
    val assets: List<GhAsset> = emptyList()
)

@Serializable
private data class GhAsset(
    val name: String,
    val browser_download_url: String,
    val size: Long
)

data class UpdateInfo(
    val versionName: String,
    val releaseNotes: String,
    val apkUrl: String,
    val apkSizeBytes: Long,
    val releasePageUrl: String
)

object UpdateChecker {

    // Public repo — no token needed for reads. Rate limit is 60/hr from one IP,
    // way more than enough for a check on app start + once/day.
    private const val API =
        "https://api.github.com/repos/blizzardsnake/herotraining1/releases/latest"

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Checks GitHub for the latest release. Returns UpdateInfo if a NEWER version than
     * the currently-installed versionName is available, or null otherwise.
     */
    suspend fun check(context: Context): UpdateInfo? = withContext(Dispatchers.IO) {
        runCatching {
            val conn = (URL(API).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "HeroTraining-UpdateChecker")
            }
            if (conn.responseCode != 200) return@runCatching null
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val release = json.decodeFromString<GhRelease>(body)
            if (release.prerelease || release.draft) return@runCatching null

            val apk = release.assets.firstOrNull { it.name.endsWith(".apk") }
                ?: return@runCatching null

            val current = context.packageManager
                .getPackageInfo(context.packageName, 0).versionName.orEmpty()
            val remote = release.tag_name.removePrefix("v")

            if (!isNewer(remote, current)) return@runCatching null

            UpdateInfo(
                versionName = remote,
                releaseNotes = release.body.orEmpty().take(400),
                apkUrl = apk.browser_download_url,
                apkSizeBytes = apk.size,
                releasePageUrl = release.html_url
            )
        }.getOrNull()
    }

    /** Naive semver-ish compare: 0.2.0 > 0.1.5, 1.0 > 0.9, etc. */
    private fun isNewer(remote: String, current: String): Boolean {
        val r = remote.split('.', '-').mapNotNull { it.toIntOrNull() }
        val c = current.split('.', '-').mapNotNull { it.toIntOrNull() }
        val n = maxOf(r.size, c.size)
        for (i in 0 until n) {
            val a = r.getOrElse(i) { 0 }
            val b = c.getOrElse(i) { 0 }
            if (a != b) return a > b
        }
        return false
    }
}
