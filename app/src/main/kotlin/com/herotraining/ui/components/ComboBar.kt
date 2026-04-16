package com.herotraining.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.model.Hero
import com.herotraining.domain.calc.getComboBonus
import com.herotraining.domain.calc.getComboStage
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun ComboBar(combo: Int, hero: Hero, modifier: Modifier = Modifier) {
    val stage = getComboStage(combo, hero)
    val bonus = getComboBonus(combo)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, hero.color)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "КОМБО · ${hero.comboName}",
                    style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = hero.color.copy(alpha = 0.7f))
                )
                Text(
                    text = stage.name,
                    style = TextStyle(
                        fontFamily = ImpactLike,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = hero.color
                    )
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${combo}%",
                    style = TextStyle(
                        fontFamily = ImpactLike,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = hero.color
                    )
                )
                Text(
                    text = bonus.label,
                    style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        // 20 segments
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            (1..20).forEach { i ->
                val filled = i * 5 <= combo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(if (filled) hero.color else HeroPalette.Neutral800)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            hero.comboStages.forEachIndexed { i, s ->
                Text(
                    text = s,
                    style = TextStyle(
                        fontSize = 8.sp,
                        letterSpacing = 2.sp,
                        color = if (i <= stage.index) hero.color else HeroPalette.Neutral700
                    )
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral800))
        Spacer(Modifier.height(6.dp))
        Text(
            text = bonus.desc,
            style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400)
        )
    }
}
