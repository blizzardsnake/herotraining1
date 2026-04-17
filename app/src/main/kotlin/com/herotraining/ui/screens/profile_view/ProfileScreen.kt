package com.herotraining.ui.screens.profile_view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.herotraining.data.db.entity.DiaryEntryEntity
import com.herotraining.data.db.entity.MeasurementEntity
import com.herotraining.data.db.entity.ProgressPhotoEntity
import com.herotraining.data.db.entity.WeightEntryEntity
import com.herotraining.data.repo.ProfileRepository
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike
import java.time.LocalDate

private enum class Tab(val label: String) {
    OVERVIEW("ОБЗОР"),
    MEASUREMENTS("ЗАМЕРЫ"),
    WEIGHT("ВЕС"),
    PHOTOS("ФОТО"),
    DIARY("ДНЕВНИК")
}

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val userState by vm.userState.collectAsStateWithLifecycle()
    val hero = userState.hero
    val accent = hero?.color ?: HeroPalette.Red500
    val bg = hero?.bgColor ?: HeroPalette.Black

    val measurements by vm.measurements.collectAsStateWithLifecycle()
    val weights by vm.weights.collectAsStateWithLifecycle()
    val photos by vm.photos.collectAsStateWithLifecycle()
    val diary by vm.diary.collectAsStateWithLifecycle()
    val auth by vm.auth.collectAsStateWithLifecycle()

    var tab by remember { mutableStateOf(Tab.OVERVIEW) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showMeasureDialog by remember { mutableStateOf(false) }
    var showDiaryDialog by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { vm.addPhoto(it.toString(), pose = null, note = null) }
    }

    Box(Modifier.fillMaxSize().background(bg)) {
        Column(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 60.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral900)
                    .padding(horizontal = 16.dp, vertical = 12.dp).padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.ArrowBack, null,
                    tint = HeroPalette.Neutral400, modifier = Modifier.size(20.dp).clickable { onBack() }
                )
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Filled.Person, null, tint = accent, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = auth?.displayName?.uppercase() ?: "ПРОФИЛЬ",
                        style = TextStyle(fontFamily = ImpactLike, fontSize = 16.sp, fontWeight = FontWeight.Black, color = accent)
                    )
                    Text(
                        text = auth?.email ?: "Локальный профиль",
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500)
                    )
                }
                if (auth == null) {
                    Text(
                        text = "ВОЙТИ →",
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, color = accent, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable { /* Firebase auth wire-up in v0.3 */ }
                    )
                }
            }

            // Weekly check-in status
            WeeklyCheckinBar(latestDiary = diary.firstOrNull(), accent = accent)

            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Tab.entries.forEach { t ->
                    val sel = t == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (sel) accent else HeroPalette.Neutral800)
                            .background(if (sel) accent.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { tab = t }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = t.label,
                            style = TextStyle(fontSize = 9.sp, letterSpacing = 1.sp, color = if (sel) accent else HeroPalette.Neutral500)
                        )
                    }
                }
            }

            when (tab) {
                Tab.OVERVIEW -> OverviewTab(
                    measurements = measurements, weights = weights, photos = photos, diary = diary,
                    accent = accent
                )
                Tab.MEASUREMENTS -> MeasurementsTab(
                    list = measurements, accent = accent,
                    onAdd = { showMeasureDialog = true },
                    onDelete = { vm.deleteMeasurement(it) }
                )
                Tab.WEIGHT -> WeightTab(
                    list = weights, accent = accent,
                    onAdd = { showWeightDialog = true },
                    onDelete = { vm.deleteWeight(it) }
                )
                Tab.PHOTOS -> PhotosTab(
                    list = photos, accent = accent,
                    onAdd = { picker.launch("image/*") },
                    onDelete = { vm.deletePhoto(it) }
                )
                Tab.DIARY -> DiaryTab(
                    list = diary, accent = accent,
                    onAdd = { showDiaryDialog = true },
                    onDelete = { vm.deleteDiary(it) }
                )
            }
        }
    }

    if (showWeightDialog) {
        WeightDialog(
            accent = accent,
            onDismiss = { showWeightDialog = false },
            onSave = { vm.logWeight(it); showWeightDialog = false }
        )
    }
    if (showMeasureDialog) {
        MeasurementDialog(
            accent = accent,
            onDismiss = { showMeasureDialog = false },
            onSave = { vm.addMeasurement(it); showMeasureDialog = false }
        )
    }
    if (showDiaryDialog) {
        DiaryDialog(
            accent = accent,
            onDismiss = { showDiaryDialog = false },
            onSave = { text, m, e -> vm.addDiary(text, m, e); showDiaryDialog = false }
        )
    }
}

@Composable
private fun WeeklyCheckinBar(latestDiary: DiaryEntryEntity?, accent: Color) {
    val daysSince = latestDiary?.let {
        (LocalDate.now().toEpochDay() - LocalDate.ofEpochDay(it.recordedAt / 86_400_000L).toEpochDay()).toInt()
    } ?: 999
    val overdue = daysSince > 7
    val color = if (overdue) HeroPalette.Red500 else Color(0xFF10B981)
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = if (overdue) "⚠ ТРЕБУЕТСЯ ОБНОВИТЬ ДАННЫЕ"
                   else "✓ ДАННЫЕ АКТУАЛЬНЫ",
            style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = color)
        )
        Text(
            text = if (daysSince >= 999) "Первая запись ещё не сделана"
                   else "Последний чек-ин $daysSince дн. назад",
            style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400)
        )
    }
}

@Composable
private fun OverviewTab(
    measurements: List<MeasurementEntity>,
    weights: List<WeightEntryEntity>,
    photos: List<ProgressPhotoEntity>,
    diary: List<DiaryEntryEntity>,
    accent: Color
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val latestWeight = weights.firstOrNull()
        val firstWeight = weights.lastOrNull()
        val delta = if (latestWeight != null && firstWeight != null) latestWeight.weightKg - firstWeight.weightKg else 0.0

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniStat("ТЕКУЩИЙ ВЕС", latestWeight?.let { "${it.weightKg} кг" } ?: "—", accent, Modifier.weight(1f))
            MiniStat(
                "ИЗМЕНЕНИЕ",
                if (weights.size >= 2) (if (delta > 0) "+%.1f".format(delta) else "%.1f".format(delta)) + " кг"
                else "—",
                accent, Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniStat("ЗАМЕРЫ", measurements.size.toString(), accent, Modifier.weight(1f))
            MiniStat("ФОТО", photos.size.toString(), accent, Modifier.weight(1f))
            MiniStat("ЗАПИСИ", diary.size.toString(), accent, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, accent: Color, modifier: Modifier) {
    Column(modifier = modifier.border(1.dp, HeroPalette.Neutral800).padding(10.dp)) {
        Text(label, style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
        Text(value, style = TextStyle(fontFamily = ImpactLike, fontSize = 20.sp, fontWeight = FontWeight.Black, color = accent))
    }
}

@Composable
private fun MeasurementsTab(
    list: List<MeasurementEntity>, accent: Color,
    onAdd: () -> Unit, onDelete: (MeasurementEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AddButton("+ НОВЫЙ ЗАМЕР", accent, onAdd)
        if (list.isEmpty()) EmptyText("Пока нет замеров")
        list.forEach { m ->
            Column(modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(m.date, style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = accent, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.Delete, null, tint = HeroPalette.Neutral600,
                         modifier = Modifier.size(14.dp).clickable { onDelete(m) })
                }
                Spacer(Modifier.height(6.dp))
                val rows = listOf(
                    "Грудь" to m.chestCm, "Талия" to m.waistCm, "Бёдра" to m.hipsCm,
                    "Бицепс" to m.bicepCm, "Бедро" to m.thighCm, "Шея" to m.neckCm
                ).filter { it.second != null }
                rows.forEach { (name, v) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        Text(name, style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral400), modifier = Modifier.weight(1f))
                        Text("$v см", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HeroPalette.Neutral300))
                    }
                }
            }
        }
    }
}

@Composable
private fun WeightTab(
    list: List<WeightEntryEntity>, accent: Color,
    onAdd: () -> Unit, onDelete: (WeightEntryEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AddButton("+ ЗАПИСАТЬ ВЕС", accent, onAdd)
        if (list.isEmpty()) EmptyText("Пока нет записей")
        list.forEach { w ->
            Row(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.MonitorWeight, null, tint = accent, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(10.dp))
                Text(w.date, style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500), modifier = Modifier.weight(1f))
                Text(
                    "${w.weightKg} кг",
                    style = TextStyle(fontFamily = ImpactLike, fontSize = 20.sp, fontWeight = FontWeight.Black, color = accent)
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.Delete, null, tint = HeroPalette.Neutral600,
                     modifier = Modifier.size(14.dp).clickable { onDelete(w) })
            }
        }
    }
}

@Composable
private fun PhotosTab(
    list: List<ProgressPhotoEntity>, accent: Color,
    onAdd: () -> Unit, onDelete: (ProgressPhotoEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AddButton("+ ДОБАВИТЬ ФОТО", accent, onAdd)
        if (list.isEmpty()) EmptyText("Пока нет фотографий")
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().height((((list.size + 2) / 3) * 120).dp.coerceAtLeast(0.dp)),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(list.size) { i ->
                val p = list[i]
                Box(
                    modifier = Modifier.aspectRatio(0.75f).border(1.dp, HeroPalette.Neutral800)
                ) {
                    AsyncImage(
                        model = p.localUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = p.date,
                        style = TextStyle(fontSize = 9.sp, color = Color.White),
                        modifier = Modifier.align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.6f)).padding(2.dp)
                    )
                    Icon(
                        Icons.Filled.Close, null, tint = Color.White,
                        modifier = Modifier.align(Alignment.TopEnd).size(18.dp)
                            .background(Color.Black.copy(alpha = 0.6f)).clickable { onDelete(p) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryTab(
    list: List<DiaryEntryEntity>, accent: Color,
    onAdd: () -> Unit, onDelete: (DiaryEntryEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AddButton("+ НОВАЯ ЗАПИСЬ", accent, onAdd)
        if (list.isEmpty()) EmptyText("Дневник пуст")
        list.forEach { e ->
            Column(modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(e.date, style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = accent, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                    e.mood?.let { Text("😊×$it", style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500)) }
                    e.energy?.let { Spacer(Modifier.width(6.dp)); Text("⚡×$it", style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500)) }
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.Delete, null, tint = HeroPalette.Neutral600,
                         modifier = Modifier.size(14.dp).clickable { onDelete(e) })
                }
                Spacer(Modifier.height(6.dp))
                Text(e.text, style = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300))
            }
        }
    }
}

@Composable
private fun AddButton(text: String, accent: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().border(2.dp, accent).clickable(onClick = onClick).padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = TextStyle(fontFamily = ImpactLike, fontSize = 13.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Black, color = accent))
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(text, style = TextStyle(fontSize = 12.sp, fontStyle = FontStyle.Italic, color = HeroPalette.Neutral600),
         modifier = Modifier.padding(vertical = 16.dp))
}

/* ---- Dialogs ---- */

@Composable
private fun WeightDialog(accent: Color, onDismiss: () -> Unit, onSave: (Double) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ЗАПИСАТЬ ВЕС", style = TextStyle(fontFamily = ImpactLike, fontWeight = FontWeight.Black, color = accent)) },
        text = {
            TextField(
                value = text, onValueChange = { text = it.filter { c -> c.isDigit() || c == '.' }.take(5) },
                placeholder = { Text("75.5") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = TextStyle(fontSize = 20.sp, color = accent),
                colors = dialogFieldColors(accent)
            )
        },
        confirmButton = {
            Text("СОХРАНИТЬ", style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = accent, fontWeight = FontWeight.Bold),
                 modifier = Modifier.clickable { text.toDoubleOrNull()?.let(onSave) }.padding(8.dp))
        },
        dismissButton = {
            Text("ОТМЕНА", style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                 modifier = Modifier.clickable { onDismiss() }.padding(8.dp))
        }
    )
}

@Composable
private fun MeasurementDialog(accent: Color, onDismiss: () -> Unit, onSave: (MeasurementEntity) -> Unit) {
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var hips by remember { mutableStateOf("") }
    var bicep by remember { mutableStateOf("") }
    var thigh by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("НОВЫЙ ЗАМЕР", style = TextStyle(fontFamily = ImpactLike, fontWeight = FontWeight.Black, color = accent)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MField("Грудь (см)", chest, accent) { chest = it }
                MField("Талия (см)", waist, accent) { waist = it }
                MField("Бёдра (см)", hips, accent) { hips = it }
                MField("Бицепс (см)", bicep, accent) { bicep = it }
                MField("Бедро (см)", thigh, accent) { thigh = it }
            }
        },
        confirmButton = {
            Text("СОХРАНИТЬ", style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = accent, fontWeight = FontWeight.Bold),
                 modifier = Modifier.clickable {
                     onSave(MeasurementEntity(
                         date = ProfileRepository.today(),
                         chestCm = chest.toDoubleOrNull(),
                         waistCm = waist.toDoubleOrNull(),
                         hipsCm = hips.toDoubleOrNull(),
                         bicepCm = bicep.toDoubleOrNull(),
                         thighCm = thigh.toDoubleOrNull(),
                         recordedAt = System.currentTimeMillis()
                     ))
                 }.padding(8.dp))
        },
        dismissButton = {
            Text("ОТМЕНА", style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                 modifier = Modifier.clickable { onDismiss() }.padding(8.dp))
        }
    )
}

@Composable
private fun MField(label: String, value: String, accent: Color, onChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500), modifier = Modifier.width(110.dp))
        TextField(
            value = value,
            onValueChange = { onChange(it.filter { c -> c.isDigit() || c == '.' }.take(5)) },
            placeholder = { Text("0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = TextStyle(fontSize = 15.sp, color = accent, fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
            colors = dialogFieldColors(accent)
        )
    }
}

@Composable
private fun DiaryDialog(
    accent: Color, onDismiss: () -> Unit,
    onSave: (text: String, mood: Int?, energy: Int?) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf<Int?>(null) }
    var energy by remember { mutableStateOf<Int?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("НОВАЯ ЗАПИСЬ", style = TextStyle(fontFamily = ImpactLike, fontWeight = FontWeight.Black, color = accent)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TextField(
                    value = text, onValueChange = { text = it.take(500) },
                    placeholder = { Text("Как прошёл день? Мысли, наблюдения...") },
                    textStyle = TextStyle(fontSize = 13.sp, color = HeroPalette.Neutral300),
                    modifier = Modifier.fillMaxWidth(), colors = dialogFieldColors(accent)
                )
                Text("НАСТРОЕНИЕ 1-5", style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..5).forEach { n ->
                        val sel = mood == n
                        Box(
                            Modifier.size(28.dp)
                                .background(if (sel) accent.copy(alpha = 0.2f) else Color.Transparent)
                                .border(1.dp, if (sel) accent else HeroPalette.Neutral700)
                                .clickable { mood = n },
                            contentAlignment = Alignment.Center
                        ) { Text("$n", style = TextStyle(color = if (sel) accent else HeroPalette.Neutral400)) }
                    }
                }
                Text("ЭНЕРГИЯ 1-5", style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..5).forEach { n ->
                        val sel = energy == n
                        Box(
                            Modifier.size(28.dp)
                                .background(if (sel) accent.copy(alpha = 0.2f) else Color.Transparent)
                                .border(1.dp, if (sel) accent else HeroPalette.Neutral700)
                                .clickable { energy = n },
                            contentAlignment = Alignment.Center
                        ) { Text("$n", style = TextStyle(color = if (sel) accent else HeroPalette.Neutral400)) }
                    }
                }
            }
        },
        confirmButton = {
            Text("СОХРАНИТЬ", style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = accent, fontWeight = FontWeight.Bold),
                 modifier = Modifier.clickable { if (text.isNotBlank()) onSave(text, mood, energy) }.padding(8.dp))
        },
        dismissButton = {
            Text("ОТМЕНА", style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                 modifier = Modifier.clickable { onDismiss() }.padding(8.dp))
        }
    )
}

@Composable
private fun dialogFieldColors(accent: Color) = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = accent, unfocusedIndicatorColor = HeroPalette.Neutral700,
    cursorColor = accent
)
