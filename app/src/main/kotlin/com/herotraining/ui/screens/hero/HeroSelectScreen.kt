package com.herotraining.ui.screens.hero

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.catalog.HeroCatalog
import com.herotraining.data.model.Gender
import com.herotraining.data.model.Hero
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun HeroSelectScreen(
    gender: Gender,
    onBack: () -> Unit,
    onSelect: (Hero) -> Unit
) {
    val heroes = HeroCatalog.forGender(gender)
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
                .padding(top = 24.dp, bottom = 80.dp)
                .widthIn(max = 760.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "← АНКЕТА",
                style = TextStyle(
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = HeroPalette.Neutral500
                ),
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable { onBack() }
                    .padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) { append("ВЫБЕРИ ") }
                    withStyle(SpanStyle(color = HeroPalette.Red500)) { append("ПУТЬ") }
                },
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (gender == Gender.FEMALE) "ЖЕНСКИЕ ГЕРОИНИ" else "МУЖСКИЕ ГЕРОИ",
                style = TextStyle(
                    fontSize = 11.sp,
                    letterSpacing = 3.sp,
                    color = HeroPalette.Neutral500
                )
            )
            Spacer(Modifier.height(24.dp))

            heroes.forEach { hero ->
                HeroCard(hero = hero, onClick = { onSelect(hero) })
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun HeroCard(hero: Hero, onClick: () -> Unit) {
    val icon = iconForKey(hero.iconKey)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(hero.bgColor)
            .border(1.dp, HeroPalette.Neutral800)
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        // Watermark icon top-right (5% opacity)
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = hero.color,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(140.dp)
                .alpha(0.08f)
        )
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .border(1.dp, hero.color)
                    .padding(10.dp)
            ) {
                Icon(icon, contentDescription = null, tint = hero.color, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = hero.name,
                    style = TextStyle(
                        fontFamily = ImpactLike,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = hero.color
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = hero.tagline,
                    style = TextStyle(
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        color = hero.color.copy(alpha = 0.7f)
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = hero.description,
                    style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral400)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "КОМБО: ${hero.comboName}",
                    style = TextStyle(
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = hero.color.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

/** Maps catalog iconKey → Compose Material icon. */
private fun iconForKey(key: String): ImageVector = when (key) {
    "shield" -> Icons.Filled.Shield
    "swords" -> Icons.AutoMirrored.Filled.Logout      // swords not in core; approximation
    "sword" -> Icons.AutoMirrored.Filled.Logout
    "sparkles" -> Icons.Filled.AutoAwesome
    "cat" -> Icons.Filled.Pets
    "compass" -> Icons.Filled.Explore
    "cpu" -> Icons.Filled.Memory
    "crosshair" -> Icons.Filled.GpsFixed
    else -> Icons.Filled.Whatshot
}
