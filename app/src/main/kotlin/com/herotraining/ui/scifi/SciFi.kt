package com.herotraining.ui.scifi

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.Orbitron
import com.herotraining.ui.theme.Rajdhani

/* =========================================================================
 *  BASE PRIMITIVES — corner brackets, frames, glows
 * ========================================================================= */

/**
 * Draws four L-shaped corner brackets inside the layout bounds. Pure Canvas overlay,
 * does not affect content size or placement.
 */
@Composable
fun CornerBrackets(
    color: Color,
    modifier: Modifier = Modifier,
    armLength: Dp = 14.dp,
    thickness: Dp = 1.5.dp
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val arm = armLength.toPx()
        val t = thickness.toPx()
        val w = size.width
        val h = size.height
        // Top-left
        drawLine(color, Offset(0f, 0f), Offset(arm, 0f), strokeWidth = t)
        drawLine(color, Offset(0f, 0f), Offset(0f, arm), strokeWidth = t)
        // Top-right
        drawLine(color, Offset(w, 0f), Offset(w - arm, 0f), strokeWidth = t)
        drawLine(color, Offset(w, 0f), Offset(w, arm), strokeWidth = t)
        // Bottom-left
        drawLine(color, Offset(0f, h), Offset(arm, h), strokeWidth = t)
        drawLine(color, Offset(0f, h), Offset(0f, h - arm), strokeWidth = t)
        // Bottom-right
        drawLine(color, Offset(w, h), Offset(w - arm, h), strokeWidth = t)
        drawLine(color, Offset(w, h), Offset(w, h - arm), strokeWidth = t)
    }
}

/** Sci-fi panel: semi-transparent fill + 1dp border + corner brackets + depth gradient. */
@Composable
fun SciFiFrame(
    modifier: Modifier = Modifier,
    accent: Color = HeroPalette.Red500,
    borderAlpha: Float = 0.55f,
    fillAlpha: Float = 0.08f,
    bracketLength: Dp = 14.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(accent.copy(alpha = fillAlpha))
            .panelDepth(
                top = Color.White.copy(alpha = 0.03f),
                bottom = Color.Black.copy(alpha = 0.25f)
            )
            .border(1.dp, accent.copy(alpha = borderAlpha))
    ) {
        CornerBrackets(color = accent, armLength = bracketLength)
        content()
    }
}

/* =========================================================================
 *  HEADERS, CHIPS, BUTTONS
 * ========================================================================= */

/**
 * Big condensed headline + small Orbitron sub-label underneath.
 * Example:
 *    БАЗОВЫЕ ДАННЫЕ
 *    PRIMARY PARAMETERS
 */
@Composable
fun SciFiHeader(
    title: String,
    subtitle: String? = null,
    accent: Color = HeroPalette.Red500,
    onBg: Color = Color.White,
    titleFontSize: androidx.compose.ui.unit.TextUnit = 30.sp
) {
    Column {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = Rajdhani,
                fontWeight = FontWeight.Bold,
                fontSize = titleFontSize,
                color = onBg,
                letterSpacing = 1.sp
            )
        )
        if (subtitle != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = Orbitron,
                    fontSize = 9.sp,
                    letterSpacing = 3.sp,
                    color = accent.copy(alpha = 0.85f)
                )
            )
        }
    }
}

/**
 * Status chip: small pill with accent fill, Orbitron caps text.
 * Examples: COMPLETE · ACTIVE · ОЖИРЕНИЕ I · LOCKED
 */
@Composable
fun SciFiStatusChip(
    text: String,
    accent: Color,
    filled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val bg = if (filled) accent.copy(alpha = 0.2f) else Color.Transparent
    Box(
        modifier = modifier
            .background(bg)
            .border(1.dp, accent)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Orbitron,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                color = accent
            )
        )
    }
}

/** Primary CTA button — accent-tinted background + corner brackets + halo glow. */
@Composable
fun SciFiPrimaryButton(
    text: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val alpha = if (enabled) 1f else 0.3f
    val outerMod = if (enabled) modifier.scifiGlow(accent, spread = 24.dp, intensity = 0.45f) else modifier
    Box(
        modifier = outerMod
            .fillMaxWidth()
            .background(accent.copy(alpha = 0.22f * alpha))
            .panelDepth(
                top = accent.copy(alpha = 0.15f * alpha),
                bottom = Color.Black.copy(alpha = 0.3f)
            )
            .border(2.dp, accent.copy(alpha = alpha))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        CornerBrackets(color = accent.copy(alpha = alpha), armLength = 18.dp, thickness = 2.dp)
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Rajdhani,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                letterSpacing = 5.sp,
                color = accent.copy(alpha = alpha)
            )
        )
    }
}

/** Ghost button — thin outline only, no fill. */
@Composable
fun SciFiGhostButton(
    text: String,
    accent: Color = HeroPalette.Neutral400,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(1.dp, accent.copy(alpha = 0.6f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Orbitron,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = accent
            )
        )
    }
}

/* =========================================================================
 *  NUMERIC FIELD — big red number + blinking caret + monospace sub-label
 * ========================================================================= */

@Composable
fun SciFiNumericField(
    label: String,
    subLabel: String,    // e.g. "AGE // YEARS"
    value: String,
    onChange: (String) -> Unit,
    accent: Color,
    suffix: String,
    modifier: Modifier = Modifier,
    maxLen: Int = 3
) {
    val infinite = rememberInfiniteTransition(label = "caret")
    val caretAlpha by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "caretAlpha"
    )

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = TextStyle(fontFamily = Rajdhani, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = HeroPalette.Neutral400),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = subLabel,
                style = TextStyle(fontFamily = Orbitron, fontSize = 8.sp, letterSpacing = 2.sp, color = accent.copy(alpha = 0.7f))
            )
        }
        Spacer(Modifier.height(4.dp))
        SciFiFrame(
            accent = accent,
            borderAlpha = 0.6f,
            fillAlpha = 0.05f,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { raw -> onChange(raw.filter { it.isDigit() }.take(maxLen)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        fontFamily = Rajdhani,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = accent
                    ),
                    cursorBrush = SolidColor(accent),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                // Blinking caret separator
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(accent.copy(alpha = caretAlpha * 0.9f))
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = suffix,
                    style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                    modifier = Modifier.width(40.dp)
                )
            }
        }
    }
}

/* =========================================================================
 *  CARD — selectable panel with corner brackets + optional COMPLETE chip
 * ========================================================================= */

@Composable
fun SciFiCard(
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showCompleteChip: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderColor = if (selected) accent else HeroPalette.Neutral700
    val fillAlpha = if (selected) 0.14f else 0f
    val outerMod = if (selected) modifier.scifiGlow(accent, spread = 18.dp, intensity = 0.35f) else modifier
    Box(
        modifier = outerMod
            .background(accent.copy(alpha = fillAlpha))
            .panelDepth(
                top = if (selected) accent.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.02f),
                bottom = Color.Black.copy(alpha = 0.30f)
            )
            .border(if (selected) 2.dp else 1.dp, borderColor)
            .clickable(onClick = onClick)
    ) {
        if (selected) CornerBrackets(color = accent, armLength = 16.dp, thickness = 2.dp)
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
        if (selected && showCompleteChip) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
            ) {
                SciFiStatusChip(text = "COMPLETE", accent = accent)
            }
        }
    }
}

/* =========================================================================
 *  TOP BAR — "← НАЗАД" left, status indicators right
 * ========================================================================= */

@Composable
fun SciFiTopBar(
    title: String,
    onBack: (() -> Unit)?,
    rightStatus: String? = null,   // e.g. "POWER: READY"
    accent: Color = HeroPalette.Red500,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Text(
                text = "← НАЗАД",
                style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral400),
                modifier = Modifier.clickable { onBack() }.padding(end = 10.dp)
            )
        }
        Text(
            text = title.uppercase(),
            style = TextStyle(fontFamily = Orbitron, fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold, color = accent),
            modifier = Modifier.weight(1f)
        )
        if (rightStatus != null) {
            Text(
                text = rightStatus,
                style = TextStyle(fontFamily = Orbitron, fontSize = 8.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral600)
            )
        }
    }
}

/* =========================================================================
 *  STEP PROGRESS — sci-fi take with numbered ticks
 * ========================================================================= */

@Composable
fun SciFiStepProgress(current: Int, total: Int, accent: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { i ->
            val done = i <= current
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier.weight(1f).height(2.dp)
                        .background(if (done) accent else HeroPalette.Neutral800)
                )
            }
            if (i < total - 1) Spacer(Modifier.width(3.dp))
        }
    }
}

/* =========================================================================
 *  RING STAT — circular progress (for Dashboard v0.4.2)
 * ========================================================================= */

@Composable
fun RingStat(
    percent: Float,  // 0f..1f
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
    ringSize: Dp = 72.dp
) {
    Box(modifier = modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // background ring
            drawArc(
                color = HeroPalette.Neutral800,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx()),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height)
            )
            // progress
            drawArc(
                brush = Brush.sweepGradient(
                    0f to accent.copy(alpha = 0.5f),
                    percent to accent,
                    1f to accent.copy(alpha = 0.5f)
                ),
                startAngle = -90f,
                sweepAngle = 360f * percent.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = 4.dp.toPx()),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = accent)
            )
            Text(
                text = label,
                style = TextStyle(fontFamily = Orbitron, fontSize = 7.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
            )
        }
    }
}

/* =========================================================================
 *  ATHLETE SILHOUETTE — simple wire outline for analysis panels
 * ========================================================================= */

@Composable
fun AthleteSilhouette(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(60.dp, 100.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 1.5.dp.toPx())
        // Head
        drawCircle(color, radius = w * 0.12f, center = Offset(w / 2f, h * 0.12f), style = stroke)
        // Torso
        drawLine(color, Offset(w / 2f, h * 0.22f), Offset(w / 2f, h * 0.55f), strokeWidth = stroke.width)
        // Arms (down-flexed, athletic stance)
        drawLine(color, Offset(w / 2f, h * 0.28f), Offset(w * 0.15f, h * 0.48f), strokeWidth = stroke.width)
        drawLine(color, Offset(w / 2f, h * 0.28f), Offset(w * 0.85f, h * 0.48f), strokeWidth = stroke.width)
        drawLine(color, Offset(w * 0.15f, h * 0.48f), Offset(w * 0.05f, h * 0.30f), strokeWidth = stroke.width)  // left forearm up
        drawLine(color, Offset(w * 0.85f, h * 0.48f), Offset(w * 0.95f, h * 0.30f), strokeWidth = stroke.width)  // right forearm up
        // Hips
        drawLine(color, Offset(w * 0.28f, h * 0.55f), Offset(w * 0.72f, h * 0.55f), strokeWidth = stroke.width)
        // Legs
        drawLine(color, Offset(w * 0.36f, h * 0.55f), Offset(w * 0.30f, h * 0.95f), strokeWidth = stroke.width)
        drawLine(color, Offset(w * 0.64f, h * 0.55f), Offset(w * 0.70f, h * 0.95f), strokeWidth = stroke.width)
    }
}

/* =========================================================================
 *  SYSTEM ANALYSIS PANEL — used for the BMI block on anketa
 * ========================================================================= */

@Composable
fun SystemAnalysisPanel(
    bmi: Double,
    bmiCategory: String,
    categoryColor: Color,
    idealMin: Int,
    idealMax: Int,
    recommendation: String,
    modifier: Modifier = Modifier
) {
    SciFiFrame(
        accent = categoryColor,
        borderAlpha = 0.8f,
        fillAlpha = 0.06f,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "АНАЛИЗ СИСТЕМЫ",
                style = TextStyle(fontFamily = Orbitron, fontSize = 9.sp, letterSpacing = 3.sp, color = categoryColor)
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ИНДЕКС МАССЫ ТЕЛА",
                        style = TextStyle(fontFamily = Rajdhani, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = HeroPalette.Neutral400, letterSpacing = 1.sp)
                    )
                    Text(
                        text = bmi.toString(),
                        style = TextStyle(fontFamily = Rajdhani, fontWeight = FontWeight.Bold, fontSize = 44.sp, color = categoryColor)
                    )
                    Spacer(Modifier.height(4.dp))
                    SciFiStatusChip(text = bmiCategory.uppercase(), accent = categoryColor)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = HeroPalette.Neutral500)) { append("ЦЕЛЕВОЙ ДИАПАЗОН  ") }
                            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) { append("$idealMin–$idealMax кг") }
                        },
                        style = TextStyle(fontFamily = Rajdhani, fontSize = 11.sp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "РЕКОМЕНДАЦИЯ",
                        style = TextStyle(fontFamily = Orbitron, fontSize = 8.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                    )
                    Text(
                        text = recommendation,
                        style = TextStyle(fontFamily = Rajdhani, fontSize = 11.sp, color = HeroPalette.Neutral300)
                    )
                }
                Spacer(Modifier.width(12.dp))
                AthleteSilhouette(color = categoryColor.copy(alpha = 0.7f))
            }
        }
    }
}
