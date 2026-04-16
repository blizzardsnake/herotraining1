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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.model.Hero
import com.herotraining.data.model.HeroBuild
import com.herotraining.data.model.UserState
import com.herotraining.domain.calc.getTimeUntilDeadline
import com.herotraining.ui.theme.HeroPalette

@Composable
fun QuestWindow(hero: Hero, build: HeroBuild, state: UserState, modifier: Modifier = Modifier) {
    val dl = getTimeUntilDeadline()
    val d = Triple(state.todayTrainingDone, state.todayNutritionDone, state.todayBonusDone)
    val allDone = d.first && d.second && d.third
    val headerText = when {
        allDone -> "✓ ВЫПОЛНЕНО"
        dl.beforeQuestStart -> "СТАРТ В 10:00"
        else -> "ДЕДЛАЙН 23:00"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, hero.color)
            .padding(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "[ КВЕСТ ]",
                    style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = hero.color.copy(alpha = 0.7f))
                )
                Text(
                    text = headerText,
                    style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timer, contentDescription = null, tint = hero.color, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${dl.hours}:${dl.minutes.toString().padStart(2, '0')}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = hero.color
                        )
                    )
                }
                Text(
                    text = if (dl.beforeQuestStart) "ДО СТАРТА" else "ОСТАЛОСЬ",
                    style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral800))
        Spacer(Modifier.height(10.dp))

        QItem(Icons.Filled.FitnessCenter, "Тренировка", build.training, d.first, "+15% combo", hero)
        Spacer(Modifier.height(8.dp))
        QItem(Icons.Filled.Restaurant, "Питание", build.nutrition, d.second, "+10% combo", hero)
        Spacer(Modifier.height(8.dp))
        QItem(Icons.Filled.Whatshot, hero.bonusQuest.title, hero.bonusQuest.desc, d.third, "+8% combo", hero)

        if (allDone) {
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral800))
            Spacer(Modifier.height(8.dp))
            Text(
                text = "✓ +50 XP · +10% combo",
                style = TextStyle(
                    fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = hero.color
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun QItem(icon: ImageVector, title: String, desc: String, done: Boolean, reward: String, hero: Hero) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(1.dp, if (done) hero.color else HeroPalette.Neutral700)
                .background(if (done) hero.color.copy(alpha = 0.1f) else androidx.compose.ui.graphics.Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (done) Icons.Filled.Check else icon,
                contentDescription = null,
                tint = if (done) hero.color else HeroPalette.Neutral500,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (done) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (done) HeroPalette.Neutral400 else androidx.compose.ui.graphics.Color.White
                )
            )
            Text(
                text = desc,
                style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500)
            )
            Text(
                text = reward,
                style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral600)
            )
        }
    }
}
