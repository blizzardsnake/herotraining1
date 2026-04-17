package com.herotraining.ui.screens.dashboard

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.data.catalog.PortionSize
import com.herotraining.data.catalog.PortionSizes
import com.herotraining.data.model.FoodItem
import com.herotraining.data.model.LoggedMeal
import com.herotraining.data.model.UserState
import com.herotraining.domain.calc.calcMacros
import com.herotraining.ui.components.PrimaryOutlinedButton
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike

@Composable
fun NutritionViewScreen(
    state: UserState,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onAddLibraryItem: (FoodItem) -> Unit,
    onAddCustom: (text: String, portion: PortionSize) -> Unit,
    onRemoveMeal: (LoggedMeal) -> Unit
) {
    val hero = state.hero ?: return
    val build = state.build ?: return
    val profile = state.profile ?: return
    val macros = calcMacros(profile, build, hero)
    val dayKcal = state.todayMeals.sumOf { it.kcal }
    val remaining = macros.calories - dayKcal
    val percent = (dayKcal.toFloat() / macros.calories.coerceAtLeast(1)).coerceIn(0f, 1.2f)
    val untrackedCount = state.todayMeals.count { it.untracked }

    var mealText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(true) }
    var selectedMealType by remember { mutableStateOf("breakfast") }
    var portionModalFor by remember { mutableStateOf<String?>(null) }

    com.herotraining.ui.components.HeroBackgroundScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 80.dp)
                .widthIn(max = 640.dp)
        ) {
            Text(
                text = "← ДАШБОРД",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Restaurant, contentDescription = null, tint = hero.color, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ПИТАНИЕ",
                    style = TextStyle(fontFamily = ImpactLike, fontSize = 28.sp, fontWeight = FontWeight.Black, color = hero.color)
                )
            }
            Text(
                text = build.name,
                style = TextStyle(fontSize = 10.sp, letterSpacing = 3.sp, color = HeroPalette.Neutral500)
            )

            Spacer(Modifier.height(14.dp))
            // Calorie card
            Column(
                modifier = Modifier.fillMaxWidth().border(2.dp, hero.color).padding(14.dp)
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text("СЪЕДЕНО", style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
                        Text(dayKcal.toString(), style = TextStyle(fontFamily = ImpactLike, fontSize = 36.sp, fontWeight = FontWeight.Black, color = hero.color))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ОСТАЛОСЬ", style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
                        Text(
                            text = if (remaining >= 0) remaining.toString() else "+${-remaining}",
                            style = TextStyle(
                                fontFamily = ImpactLike, fontSize = 20.sp, fontWeight = FontWeight.Black,
                                color = if (remaining >= 0) hero.color else HeroPalette.Red500
                            )
                        )
                        Text("из ${macros.calories}", style = TextStyle(fontSize = 10.sp, color = HeroPalette.Neutral500))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(HeroPalette.Neutral900)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percent.coerceAtMost(1f))
                            .height(6.dp)
                            .background(if (percent > 1.1f) HeroPalette.Red500 else hero.color)
                    )
                }
                if (untrackedCount > 0) {
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Text("БЕЗ ПОДСЧЁТА", style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500), modifier = Modifier.weight(1f))
                        Text("~ $untrackedCount", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeroPalette.Neutral400))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            // Macro tiles
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MacroSmall("Б", "${macros.protein}г", hero.color, Modifier.weight(1f))
                MacroSmall("Ж", "${macros.fat}г", hero.color, Modifier.weight(1f))
                MacroSmall("У", "${macros.carb}г", hero.color, Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))
            // Signature perk
            Row(
                modifier = Modifier.fillMaxWidth().border(1.dp, hero.color).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = hero.signaturePerk.icon, style = TextStyle(fontSize = 22.sp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(hero.signaturePerk.name, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = hero.color))
                    Text(hero.signaturePerk.desc, style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500))
                }
            }

            Spacer(Modifier.height(12.dp))
            // Menu toggle + library
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showMenu = !showMenu }.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🍴", style = TextStyle(fontSize = 14.sp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "МЕНЮ ${hero.name}",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HeroPalette.Neutral300),
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = if (showMenu) "▼" else "▶", style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500))
                }
                if (showMenu) {
                    Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral800))
                    // Meal type tabs
                    Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            "breakfast" to ("🌅" to "завтрак"),
                            "lunch" to ("☀️" to "обед"),
                            "dinner" to ("🌙" to "ужин"),
                            "snack" to ("🍎" to "перекус")
                        ).forEach { (id, pair) ->
                            val sel = selectedMealType == id
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, if (sel) hero.color else HeroPalette.Neutral700)
                                    .background(if (sel) hero.color.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { selectedMealType = id }
                                    .padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(pair.first, style = TextStyle(fontSize = 14.sp))
                                Text(pair.second, style = TextStyle(fontSize = 9.sp, letterSpacing = 1.sp, color = if (sel) hero.color else HeroPalette.Neutral500))
                            }
                        }
                    }
                    // Items
                    val items = when (selectedMealType) {
                        "breakfast" -> hero.foodLibrary.breakfast
                        "lunch" -> hero.foodLibrary.lunch
                        "dinner" -> hero.foodLibrary.dinner
                        else -> hero.foodLibrary.snack
                    }
                    val added = state.todayMeals.map { it.text }
                    Column(modifier = Modifier.padding(horizontal = 10.dp).padding(bottom = 10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items.forEach { item ->
                            val isAdded = item.name in added
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isAdded) hero.color.copy(alpha = 0.1f) else Color.Transparent)
                                    .border(1.dp, if (isAdded) hero.color else HeroPalette.Neutral800)
                                    .clickable(enabled = !isAdded) { onAddLibraryItem(item) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isAdded) Icons.Filled.Check else Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = if (isAdded) hero.color else HeroPalette.Neutral600,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = item.name,
                                    style = TextStyle(fontSize = 12.sp, color = if (isAdded) hero.color else HeroPalette.Neutral300),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${item.kcal}",
                                    style = TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 10.sp, color = if (isAdded) hero.color else HeroPalette.Neutral500)
                                )
                            }
                        }
                        // Treat row
                        Spacer(Modifier.height(6.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(HeroPalette.Neutral800))
                        Spacer(Modifier.height(6.dp))
                        val treatUnlocked = state.combo >= 75
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (treatUnlocked) hero.color else HeroPalette.Neutral700)
                                .clickable(enabled = treatUnlocked) { onAddLibraryItem(hero.foodLibrary.treat) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (treatUnlocked) Icons.Filled.Check else Icons.Filled.Lock,
                                contentDescription = null,
                                tint = if (treatUnlocked) hero.color else HeroPalette.Neutral600,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = hero.foodLibrary.treat.name,
                                style = TextStyle(fontSize = 12.sp, color = if (treatUnlocked) hero.color else HeroPalette.Neutral500),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (treatUnlocked) "${hero.foodLibrary.treat.kcal}" else "75% combo",
                                style = TextStyle(fontSize = 10.sp, color = if (treatUnlocked) hero.color else HeroPalette.Neutral600)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            // Alcohol warning
            Row(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Red900).background(HeroPalette.Red500.copy(alpha = 0.05f)).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = HeroPalette.Red500, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(8.dp))
                Text("Алкоголь запрещён.", style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral300))
            }

            Spacer(Modifier.height(12.dp))
            // Custom meal entry
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = mealText,
                        onValueChange = { mealText = it.take(60) },
                        placeholder = { Text("свой вариант...", color = HeroPalette.Neutral600) },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300),
                        modifier = Modifier.weight(1f).border(1.dp, HeroPalette.Neutral800),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = hero.color
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, hero.color)
                            .clickable(enabled = mealText.trim().isNotEmpty()) { portionModalFor = mealText.trim() }
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = hero.color, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Enter → выбор размера: S / M / L / XL / ~",
                    style = TextStyle(fontSize = 10.sp, fontStyle = FontStyle.Italic, color = HeroPalette.Neutral500)
                )
            }

            if (state.todayMeals.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    state.todayMeals.forEach { m -> LoggedMealRow(m, hero.color, onRemoveMeal) }
                }
            }

            Spacer(Modifier.height(20.dp))
            PrimaryOutlinedButton(
                text = if (state.todayNutritionDone) "✓ ВЫПОЛНЕНО" else "✓ ЗАКРЫТЬ",
                accentColor = hero.color,
                onClick = { if (!state.todayNutritionDone && state.todayMeals.isNotEmpty()) { onComplete(); onBack() } },
                enabled = !state.todayNutritionDone && state.todayMeals.isNotEmpty()
            )
        }

        if (portionModalFor != null) {
            PortionModal(
                text = portionModalFor!!,
                hero = hero,
                onConfirm = { p -> onAddCustom(portionModalFor!!, p); mealText = ""; portionModalFor = null },
                onDismiss = { portionModalFor = null }
            )
        }
    }
}

@Composable
private fun MacroSmall(label: String, value: String, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier.border(1.dp, HeroPalette.Neutral800).padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
        Text(value, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color))
    }
}

@Composable
private fun LoggedMealRow(m: LoggedMeal, accent: Color, onRemove: (LoggedMeal) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(m.time, style = TextStyle(fontSize = 10.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = HeroPalette.Neutral500))
        Spacer(Modifier.width(10.dp))
        Text(m.text, style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral300), modifier = Modifier.weight(1f))
        if (m.portion != null && !m.untracked) {
            Text(
                text = m.portion,
                style = TextStyle(fontSize = 9.sp, letterSpacing = 1.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.border(1.dp, HeroPalette.Neutral700).padding(horizontal = 5.dp, vertical = 2.dp)
            )
            Spacer(Modifier.width(6.dp))
        }
        when {
            m.untracked -> Text("~", style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500))
            m.kcal > 0 -> Text("${m.kcal}", style = TextStyle(fontSize = 10.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = accent))
        }
        Spacer(Modifier.width(6.dp))
        Icon(
            Icons.Filled.Close,
            contentDescription = null,
            tint = HeroPalette.Neutral600,
            modifier = Modifier.size(14.dp).clickable { onRemove(m) }
        )
    }
}

@Composable
private fun PortionModal(
    text: String,
    hero: com.herotraining.data.model.Hero,
    onConfirm: (PortionSize) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = hero.bgColor,
        title = {
            Column {
                Text("РАЗМЕР ПОРЦИИ", style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
                Text(
                    text = text,
                    style = TextStyle(fontFamily = ImpactLike, fontSize = 20.sp, fontWeight = FontWeight.Black, color = hero.color)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                PortionSizes.ALL.filter { !it.untracked }.forEach { p ->
                    Row(
                        modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral700).clickable { onConfirm(p) }.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(p.label, style = TextStyle(fontFamily = ImpactLike, fontSize = 24.sp, fontWeight = FontWeight.Black, color = hero.color), modifier = Modifier.width(36.dp))
                        Column {
                            Text(p.desc, style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral300))
                            Text("~${p.kcal} ккал", style = TextStyle(fontSize = 10.sp, color = HeroPalette.Neutral500))
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).clickable { onConfirm(PortionSizes.ALL.first { it.untracked }) }.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("~ Без подсчёта (только факт)", style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral400))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Text("ОТМЕНА",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { onDismiss() }.padding(8.dp)
            )
        }
    )
}
