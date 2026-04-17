package com.herotraining.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.HeroApp
import com.herotraining.R
import com.herotraining.ui.components.LayeredArtScreen
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.Orbitron
import kotlinx.coroutines.launch

/**
 * Full-mockup sign-in screen.
 *
 * The design (ПРОТОКОЛ ГЕРОЯ, воин на фоне руин, "ВОЙТИ ЧЕРЕЗ GOOGLE" неон, features внизу)
 * is baked into pre-rendered images that came out of the AI design tool. Compose only
 * handles layering + tap zones + loading overlay — the "look" lives in the .webp files.
 *
 * Files:
 *   res/drawable-nodpi/bg_signin_full.webp      — entire background (941x1672)
 *   res/drawable-nodpi/btn_signin_google.webp   — crop of the Google button
 *   res/drawable-nodpi/btn_signin_email.webp    — crop of the Email button
 *
 * % positions were found by scripts/slice_signin.py (red-glow auto-detector). If the
 * mockup is replaced, re-run that script and paste the new percentages below.
 */
private const val IMG_ASPECT = 941f / 1672f

// Google button region (percent of image)
private const val GOOGLE_TOP    = 0.5921f
private const val GOOGLE_LEFT   = 0.0882f
private const val GOOGLE_WIDTH  = 0.8247f
private const val GOOGLE_HEIGHT = 0.1065f

// Email button region
private const val EMAIL_TOP    = 0.6878f
private const val EMAIL_LEFT   = 0.2232f
private const val EMAIL_WIDTH  = 0.5526f
private const val EMAIL_HEIGHT = 0.0897f

@Composable
fun SignInScreen(
    onSignedIn: () -> Unit,
    onSkip: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as HeroApp
    val authRepo = app.authRepository
    val scope = rememberCoroutineScope()

    var status by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        scope.launch {
            try {
                busy = true
                val r = authRepo.handleSignInResult(result)
                r.onSuccess {
                    status = "Вход выполнен: ${it.email ?: it.name ?: it.uid}"
                    try { app.firestoreSync.pullAll(it.uid) }
                    catch (e: Throwable) { status = "Вход ок, облако недоступно: ${e.javaClass.simpleName}" }
                    onSignedIn()
                }.onFailure { t ->
                    status = "Ошибка входа: ${t.javaClass.simpleName} · ${t.message ?: "нет сообщения"}"
                }
            } catch (e: Throwable) {
                status = "Непредвиденная ошибка: ${e.javaClass.simpleName}"
            } finally {
                busy = false
            }
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {

        LayeredArtScreen(
            backgroundRes = R.drawable.bg_signin_full,
            imageAspectRatio = IMG_ASPECT
        ) {
            // Google sign-in — main CTA
            Hotspot(
                imageRes = R.drawable.btn_signin_google,
                topPct = GOOGLE_TOP, leftPct = GOOGLE_LEFT,
                widthPct = GOOGLE_WIDTH, heightPct = GOOGLE_HEIGHT,
                onClick = { launcher.launch(authRepo.createSignInIntent()) },
                enabled = !busy
            )

            // Email sign-in — placeholder for now
            Hotspot(
                imageRes = R.drawable.btn_signin_email,
                topPct = EMAIL_TOP, leftPct = EMAIL_LEFT,
                widthPct = EMAIL_WIDTH, heightPct = EMAIL_HEIGHT,
                onClick = { status = "Email-вход появится в v0.6. Пока — Google." },
                enabled = !busy
            )
        }

        // ----- Loading / status / skip overlays (drawn in Compose, on top of the art) -----

        if (busy) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(44.dp).align(Alignment.Center),
                    color = HeroPalette.Red500,
                    strokeWidth = 3.dp
                )
            }
        }

        // Error / status text — pinned bottom, above the skip link
        if (status.isNotEmpty()) {
            Text(
                text = status,
                style = TextStyle(
                    fontFamily = Orbitron,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = HeroPalette.Neutral400,
                    lineHeight = 14.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 42.dp, start = 20.dp, end = 20.dp)
            )
        }

        // Discreet skip — for testing offline flow
        Text(
            text = "ПРОПУСТИТЬ →",
            style = TextStyle(
                fontFamily = Orbitron,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                color = HeroPalette.Neutral700
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .clickable { onSkip() }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}
