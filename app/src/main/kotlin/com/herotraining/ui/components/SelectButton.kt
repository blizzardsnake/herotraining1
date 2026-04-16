package com.herotraining.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.ui.theme.HeroPalette

/**
 * Rectangular select-style button. When [selected], frame uses [accentColor] with 15% fill.
 * Mirrors the prototype `<button ... style={{borderColor:sel?hero.color:'#333'}}>`.
 */
@Composable
fun SelectButton(
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null
) {
    val borderColor = if (selected) accentColor else HeroPalette.Neutral700
    val bg = if (selected) accentColor.copy(alpha = 0.1f) else Color.Transparent
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg)
            .border(1.dp, borderColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leading != null) {
            leading()
            androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
        }
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                color = if (selected) accentColor else HeroPalette.Neutral300
            ),
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Primary rectangular button for form navigation. `border-2` (2dp outline), accent color text.
 */
@Composable
fun PrimaryOutlinedButton(
    text: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val alpha = if (enabled) 1f else 0.3f
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, accentColor.copy(alpha = alpha))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = com.herotraining.ui.theme.ImpactLike,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                fontSize = 15.sp,
                letterSpacing = 3.sp,
                color = accentColor.copy(alpha = alpha)
            )
        )
    }
}
