package com.herotraining.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun StatTile(icon: ImageVector, label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, HeroPalette.Neutral800)
            .padding(10.dp)
    ) {
        Row {
            Icon(icon, contentDescription = null, tint = HeroPalette.Neutral500, modifier = Modifier.size(10.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = label,
                style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
            )
        }
        Text(
            text = value,
            style = TextStyle(
                fontFamily = ImpactLike,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = accent
            )
        )
    }
}
