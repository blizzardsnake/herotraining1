package com.herotraining.ui.screens.hero

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

/** Temporary placeholder used while subsequent screens are not yet implemented. */
@Composable
fun HeroPlaceholder(heroName: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = heroName,
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = HeroPalette.Red500
                )
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "ЭКРАН В РАЗРАБОТКЕ",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )
            Spacer(Modifier.height(32.dp))
            Box(
                Modifier
                    .border(2.dp, HeroPalette.Red500)
                    .clickable { onBack() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "← НАЗАД",
                    style = TextStyle(
                        fontFamily = ImpactLike,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 3.sp,
                        color = HeroPalette.Red500
                    )
                )
            }
        }
    }
}
