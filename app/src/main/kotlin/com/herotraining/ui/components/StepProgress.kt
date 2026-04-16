package com.herotraining.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.herotraining.ui.theme.HeroPalette

/** Horizontal strip of `total` segments; first `current+1` are colored with [activeColor]. */
@Composable
fun StepProgress(
    current: Int,
    total: Int,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth().height(4.dp)) {
        repeat(total) { i ->
            val color = if (i <= current) activeColor else HeroPalette.Neutral800
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .background(color)
                    .height(4.dp)
            )
        }
    }
}
