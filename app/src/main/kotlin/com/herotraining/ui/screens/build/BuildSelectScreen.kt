package com.herotraining.ui.screens.build

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.model.Hero
import com.herotraining.data.model.HeroBuild
import com.herotraining.data.model.visibleBuilds
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun BuildSelectScreen(
    hero: Hero,
    age: Int,
    onBack: () -> Unit,
    onSelect: (HeroBuild) -> Unit
) {
    val builds = hero.visibleBuilds(age)
    val has45 = builds.any { it.hiddenFor45 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(hero.bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 80.dp)
                .widthIn(max = 720.dp)
        ) {
            Text(
                text = "← НАЗАД",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = hero.name,
                style = TextStyle(
                    fontFamily = ImpactLike,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = hero.color
                )
            )

            Spacer(Modifier.height(16.dp))
            // Important tip box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeroPalette.Neutral950)
                    .border(1.dp, HeroPalette.Neutral800)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = hero.color, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "ВАЖНО",
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = hero.color)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Билд — цель и дисциплина, не текущий уровень.",
                    style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral300)
                )
            }

            if (has45) {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(hero.color.copy(alpha = 0.08f))
                        .border(2.dp, hero.color)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = hero.color, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "⭐ РАЗБЛОКИРОВАН БИЛД 45+",
                            style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = hero.color)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Жёсткая дисциплина, с адаптацией под зрелое тело.",
                        style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral300)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                builds.forEachIndexed { idx, build ->
                    BuildCard(
                        build = build,
                        index = idx,
                        hero = hero,
                        buildIndexInSource = hero.builds.indexOf(build),
                        onClick = { onSelect(build) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BuildCard(
    build: HeroBuild,
    index: Int,
    hero: Hero,
    buildIndexInSource: Int,
    onClick: () -> Unit
) {
    val portraitRes = HeroPortraits.resFor(hero.id, buildIndexInSource.coerceAtLeast(0))
    val isHidden45 = build.hiddenFor45

    val borderColor = if (isHidden45) hero.color else HeroPalette.Neutral800
    val bgTint = if (isHidden45) hero.color.copy(alpha = 0.05f) else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgTint)
            .border(1.dp, borderColor)
            .clickable(onClick = onClick)
    ) {
        // Portrait + overlay title
        if (portraitRes != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(portraitRes),
                    contentDescription = build.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Dark gradient overlay for text legibility at bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.55f to Color.Transparent,
                                1f to hero.bgColor.copy(alpha = 0.92f)
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isHidden45) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = hero.color, modifier = Modifier.size(11.dp))
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(
                            text = "БИЛД ${index + 1} · ${build.difficulty}${if (isHidden45) " · ЛУЧШИЙ" else ""}",
                            style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral400)
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = build.name,
                        style = TextStyle(
                            fontFamily = ImpactLike,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = hero.color
                        )
                    )
                }
                // Difficulty meter top-right
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    (1..4).forEach { i ->
                        Box(
                            modifier = Modifier
                                .size(width = 6.dp, height = 18.dp)
                                .background(if (i <= build.difficultyNum) hero.color else HeroPalette.Neutral800)
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = build.description,
                style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral400)
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = HeroPalette.Neutral500, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(text = build.training, style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral300))
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.Restaurant, contentDescription = null, tint = HeroPalette.Neutral500, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(text = build.nutrition, style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral300))
            }
            Spacer(Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral800))
            Spacer(Modifier.height(10.dp))
            Text(
                text = "\"${build.philosophy}\"",
                style = TextStyle(
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = HeroPalette.Neutral500
                )
            )
            if (build.perks.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    build.perks.forEach { perk ->
                        Text(
                            text = perk,
                            style = TextStyle(fontSize = 9.sp, letterSpacing = 1.sp, color = HeroPalette.Neutral400),
                            modifier = Modifier
                                .border(1.dp, HeroPalette.Neutral700)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
