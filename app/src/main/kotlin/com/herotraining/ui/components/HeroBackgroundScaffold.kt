package com.herotraining.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.herotraining.ui.theme.HeroTheme
import com.herotraining.ui.theme.heroTheme

/**
 * Full-screen container: hero bg image + gradient scrim + content.
 *
 * - Background image extends under the status bar (edge-to-edge looks nicer)
 * - [content] is wrapped in statusBarsPadding() so top UI doesn't overlap the notch/shade
 * - [applyStatusBarInset] allows a caller to opt out if they want full-bleed content
 */
@Composable
fun HeroBackgroundScaffold(
    modifier: Modifier = Modifier,
    theme: HeroTheme = heroTheme(),
    applyStatusBarInset: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(theme.heroBgColor)) {
        if (theme.backgroundRes != null) {
            Image(
                painter = painterResource(theme.backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.82f),
                            0.30f to Color.Black.copy(alpha = 0.55f),
                            0.50f to Color.Black.copy(alpha = 0.35f),
                            0.80f to theme.heroBgColor.copy(alpha = 0.85f),
                            1f to theme.heroBgColor
                        )
                    )
            )
        }
        Box(
            modifier = if (applyStatusBarInset) Modifier.fillMaxSize().statusBarsPadding()
                       else Modifier.fillMaxSize()
        ) {
            content()
        }
    }
}
