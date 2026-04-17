package com.herotraining.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Full-bleed background image + clickable button crops overlaid at exact pixel positions.
 *
 * Usage:
 *   LayeredArtScreen(
 *       backgroundRes = R.drawable.bg_signin_full,
 *       imageAspectRatio = 941f / 1672f,
 *   ) {
 *       Hotspot(
 *           imageRes = R.drawable.btn_signin_google,
 *           topPct = 0.5921f, leftPct = 0.0882f,
 *           widthPct = 0.8247f, heightPct = 0.1065f,
 *           onClick = { ... }
 *       )
 *   }
 *
 * Why layered instead of a single image + invisible hotspots:
 *   - Button can animate on press (scale 0.97, feels tactile)
 *   - Button can dim when disabled / during loading
 *   - Button can be swapped to a different drawable for state changes
 *   - The art is still authored in one AI tool — we just crop pieces via Python/PIL.
 *
 * How positioning works:
 *   - `BoxWithConstraints` measures the available screen
 *   - Image is laid out with `ContentScale.Fit` — matches its aspect ratio, letterboxes
 *     the remaining space with solid [letterboxColor]
 *   - Hotspots are positioned as percentages of the IMAGE display bounds (not screen),
 *     so they stay aligned on any device/orientation
 */
@Composable
fun LayeredArtScreen(
    backgroundRes: Int,
    imageAspectRatio: Float,   // width / height of the source image
    modifier: Modifier = Modifier,
    letterboxColor: Color = Color.Black,
    content: @Composable LayeredArtScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(letterboxColor)) {
        val screenW = maxWidth
        val screenH = maxHeight
        val screenAspect = screenW / screenH   // Dp / Dp -> Float

        // Decide whether to fit-by-width or fit-by-height so nothing gets cropped.
        val imgW: Dp
        val imgH: Dp
        val offX: Dp
        val offY: Dp
        if (imageAspectRatio >= screenAspect) {
            // Image is relatively wider than screen -> fit width, letterbox top/bottom
            imgW = screenW
            imgH = screenW / imageAspectRatio
            offX = 0.dp
            offY = (screenH - imgH) / 2
        } else {
            // Image is relatively taller than screen -> fit height, letterbox sides
            imgH = screenH
            imgW = screenH * imageAspectRatio
            offX = (screenW - imgW) / 2
            offY = 0.dp
        }

        // Background art
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(imgW, imgH).offset(x = offX, y = offY)
        )

        // Hotspots live in the same coordinate system as the image
        val scope = remember(imgW, imgH, offX, offY) {
            LayeredArtScope(imgW = imgW, imgH = imgH, offsetX = offX, offsetY = offY)
        }
        scope.content()
    }
}

/**
 * Scope for placing hotspots / overlay text inside a [LayeredArtScreen].
 * Coordinates are expressed as fractions (0..1) of the image's displayed size.
 */
class LayeredArtScope(
    val imgW: Dp,
    val imgH: Dp,
    val offsetX: Dp,
    val offsetY: Dp
) {
    /**
     * Clickable button made from a pre-cropped image asset.
     * The image is drawn 1:1 at the given position (percent of parent image),
     * with a subtle press animation (scale 0.96) and optional disabled dim.
     */
    @Composable
    fun Hotspot(
        imageRes: Int,
        topPct: Float,
        leftPct: Float,
        widthPct: Float,
        heightPct: Float,
        onClick: () -> Unit,
        enabled: Boolean = true
    ) {
        val interaction = remember { MutableInteractionSource() }
        val pressed by interaction.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (pressed) 0.96f else 1f,
            label = "hotspot-scale"
        )

        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(
                    x = offsetX + imgW * leftPct,
                    y = offsetY + imgH * topPct
                )
                .size(imgW * widthPct, imgH * heightPct)
                .scale(scale)
                .alpha(if (enabled) 1f else 0.35f)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                )
        )
    }

    /**
     * Transparent clickable zone (no image) — use when an element is already baked into
     * the background art but you want a nothing-visible tap target on top of it.
     */
    @Composable
    fun InvisibleHotspot(
        topPct: Float,
        leftPct: Float,
        widthPct: Float,
        heightPct: Float,
        onClick: () -> Unit,
        enabled: Boolean = true
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = offsetX + imgW * leftPct,
                    y = offsetY + imgH * topPct
                )
                .size(imgW * widthPct, imgH * heightPct)
                .clickable(enabled = enabled, onClick = onClick)
        )
    }

    /**
     * Container for overlay Composables positioned as a % of the image.
     * Good for status text, spinners, or any UI that should sit on top of the art
     * but be drawn via Compose (not baked into the image).
     */
    @Composable
    fun Overlay(
        topPct: Float,
        leftPct: Float,
        widthPct: Float,
        heightPct: Float,
        alignment: Alignment = Alignment.Center,
        content: @Composable () -> Unit
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = offsetX + imgW * leftPct,
                    y = offsetY + imgH * topPct
                )
                .size(imgW * widthPct, imgH * heightPct),
            contentAlignment = alignment
        ) {
            content()
        }
    }
}
