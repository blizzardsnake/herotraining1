package com.herotraining.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.herotraining.ui.theme.HeroPalette

/**
 * Rectangle button with a 1dp colored border, no rounded corners, text left-aligned by default.
 * Mirrors prototype `<button className="border border-neutral-800 hover:border-red-600 p-6 text-left">`.
 */
@Composable
fun HeroRectButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = HeroPalette.Neutral800,
    backgroundColor: Color = Color.Transparent,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (backgroundColor != Color.Transparent) Modifier.background(backgroundColor) else Modifier)
            .border(BorderStroke(1.dp, borderColor), RectangleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        content()
    }
}
