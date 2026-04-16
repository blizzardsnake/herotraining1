package com.herotraining.ui.screens.gender

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.herotraining.data.model.Gender
import com.herotraining.ui.components.HeroRectButton
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun GenderSelectScreen(
    onSelect: (Gender) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HeroPalette.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp, bottom = 80.dp)
                .widthIn(max = 640.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge
            Box(
                modifier = Modifier
                    .border(1.dp, HeroPalette.Red500)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ПРОТОКОЛ ГЕРОЯ",
                    style = TextStyle(
                        color = HeroPalette.Red500,
                        fontSize = 11.sp,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Spacer(Modifier.height(16.dp))
            // Title: ТВОЙ ПУТЬ (red accent)
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) { append("ТВОЙ ") }
                    withStyle(SpanStyle(color = HeroPalette.Red500)) { append("ПУТЬ") }
                },
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black
                )
            )
            Spacer(Modifier.height(32.dp))

            HeroRectButton(
                onClick = { onSelect(Gender.MALE) },
                borderColor = HeroPalette.Neutral800
            ) {
                Text(
                    text = "МУЖСКИЕ ГЕРОИ",
                    style = TextStyle(
                        fontFamily = ImpactLike,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Леон · Данте · Кратос · Сон Джин-У",
                    style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral400)
                )
            }

            Spacer(Modifier.height(12.dp))

            HeroRectButton(
                onClick = { onSelect(Gender.FEMALE) },
                borderColor = HeroPalette.Neutral800
            ) {
                Text(
                    text = "ЖЕНСКИЕ ГЕРОИНИ",
                    style = TextStyle(
                        fontFamily = ImpactLike,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Ада · Лара · 2B · Цири",
                    style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral400)
                )
            }
        }
    }
}
