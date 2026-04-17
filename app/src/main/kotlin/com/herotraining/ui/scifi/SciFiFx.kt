package com.herotraining.ui.scifi

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Adds a soft halo behind the content — radial gradient of [color] spreading outward.
 * Used on selected cards / primary buttons to give the HUD that "lit from within" look.
 */
fun Modifier.scifiGlow(
    color: Color,
    spread: Dp = 32.dp,
    intensity: Float = 0.4f
): Modifier = drawBehind {
    val sp = spread.toPx()
    val cx = size.width / 2f
    val cy = size.height / 2f
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = intensity),
                color.copy(alpha = intensity * 0.35f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = maxOf(size.width, size.height) / 2f + sp
        ),
        topLeft = Offset(-sp, -sp),
        size = Size(size.width + sp * 2, size.height + sp * 2)
    )
}

/**
 * Vertical depth gradient inside a panel — slightly lit at top, darker toward bottom.
 * Makes flat backgrounds feel three-dimensional without changing the base color.
 */
fun Modifier.panelDepth(
    top: Color = Color.White.copy(alpha = 0.03f),
    bottom: Color = Color.Black.copy(alpha = 0.22f)
): Modifier = drawBehind {
    drawRect(
        brush = Brush.verticalGradient(
            0f to top,
            0.45f to Color.Transparent,
            1f to bottom
        ),
        size = size
    )
}

/** Diagonal accent cuts in the top-left and bottom-right corners — decorative HUD detail. */
fun Modifier.cornerCuts(
    color: Color,
    size: Dp = 10.dp
): Modifier = drawBehind {
    val s = size.toPx()
    // top-left cut
    val pathTL = androidx.compose.ui.graphics.Path().apply {
        moveTo(0f, s)
        lineTo(s, 0f)
        lineTo(s * 0.6f, 0f)
        lineTo(0f, s * 0.6f)
        close()
    }
    drawPath(pathTL, color.copy(alpha = 0.8f))
    // bottom-right cut
    val w = this.size.width; val h = this.size.height
    val pathBR = androidx.compose.ui.graphics.Path().apply {
        moveTo(w, h - s)
        lineTo(w - s, h)
        lineTo(w - s * 0.6f, h)
        lineTo(w, h - s * 0.6f)
        close()
    }
    drawPath(pathBR, color.copy(alpha = 0.8f))
}
