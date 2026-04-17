package com.herotraining.ui.screens.profile_view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
    onHardReset: () -> Unit = {},
    onSignOut: () -> Unit = {},
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

    com.herotraining.ui.components.HeroBackgroundScaffold {
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
                    accent = accent, userState = userState,
                    onClearCrash = { /* recomposed on next nav */ },
                    onHardReset = onHardReset,
                    onSignOut = onSignOut
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
    accent: Color,
    userState: com.herotraining.data.model.UserState,
    onClearCrash: () -> Unit,
    onHardReset: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val lastCrash = remember { com.herotraining.crash.CrashHandler.readLastCrash(ctx) }
    var crashVisible by remember { mutableStateOf(false) }

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

        // Base parameters from anketa — read-only snapshot
        userState.profile?.let { p ->
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "БАЗОВЫЕ ПАРАМЕТРЫ · BASELINE PROFILE",
                    style = TextStyle(fontSize = 9.sp, letterSpacing = 2.sp, color = accent, fontWeight = FontWeight.Bold)
                )
                ParamRow("ПОЛ", if (p.sex.key == "male") "Мужской" else "Женский")
                ParamRow("ВОЗРАСТ", "${p.age} лет")
                ParamRow("РОСТ", "${p.height} см")
                ParamRow("ВЕС (анкета)", "${p.weight} кг")
                ParamRow("ОПЫТ", p.experience.label)
                ParamRow("МЕСТО", p.equipment.label)
                ParamRow("СЕССИЯ", "${p.timePerSessionMinutes} мин")
                ParamRow("ТРАВМЫ", p.injuries.joinToString(", ") { it.label })
                Text(
                    text = "Редактирование в следующем релизе — пока вес/замеры обновляй через вкладки выше.",
                    style = TextStyle(fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = HeroPalette.Neutral500),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Google account card
        val heroApp = ctx.applicationContext as com.herotraining.HeroApp
        val scope = rememberCoroutineScope()
        val auth = heroApp.authRepository.status.collectAsStateWithLifecycle().value
        var showSignOutDialog by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Neutral800).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "GOOGLE-АККАУНТ",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = accent)
            )
            val signedInState = auth as? com.herotraining.data.auth.AuthStatus.SignedIn
            if (signedInState != null) {
                Text(
                    signedInState.email ?: signedInState.name ?: "UID: ${signedInState.uid}",
                    style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral300)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "↪ ВЫЙТИ ИЗ АККАУНТА",
                    style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = accent),
                    modifier = Modifier.clickable { showSignOutDialog = true }.padding(vertical = 6.dp)
                )
            } else {
                Text(
                    "Вход не выполнен. Прогресс сохраняется только локально.",
                    style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral500)
                )
            }
        }
        if (showSignOutDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showSignOutDialog = false },
                title = { Text("ВЫЙТИ ИЗ GOOGLE?", style = TextStyle(fontWeight = FontWeight.Bold, color = accent)) },
                text = {
                    Text(
                        "Облачные данные сохранятся в Google — при следующем входе этим же аккаунтом всё восстановится. Локальный кэш будет очищен, чтобы другой человек мог войти своим аккаунтом без пересечений.",
                        style = TextStyle(fontSize = 12.sp, color = Color.White, lineHeight = 16.sp)
                    )
                },
                confirmButton = {
                    Text("ВЫЙТИ",
                        style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = accent, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable {
                            scope.launch {
                                try { heroApp.authRepository.signOut() } catch (_: Throwable) {}
                                // Clear local state so the next user (or reinstall) starts fresh.
                                // Cloud doc is preserved — signing back into the same account
                                // will restore everything via FirestoreSync.pullAll on SignIn.
                                heroApp.stateRepository.hardReset()
                                showSignOutDialog = false
                                onSignOut()
                            }
                        }.padding(8.dp)
                    )
                },
                dismissButton = {
                    Text("ОТМЕНА",
                        style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral400),
                        modifier = Modifier.clickable { showSignOutDialog = false }.padding(8.dp)
                    )
                }
            )
        }

        // AI SMOKE TEST — verify Gemini API key + connectivity before we build on it
        AiSmokeTestCard(
            heroName = userState.hero?.name,
            accent = accent
        )

        // HARD RESET — wipes everything locally AND in Firestore
        var showHardResetDialog by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Red900).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "ОПАСНАЯ ЗОНА · DANGER ZONE",
                style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = HeroPalette.Red500)
            )
            Text(
                "Удалит ВСЁ — и локально, и в облаке. Нельзя откатить.",
                style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400)
            )
            Text(
                "🗑 ПОЛНЫЙ СБРОС",
                style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = HeroPalette.Red500),
                modifier = Modifier.clickable { showHardResetDialog = true }.padding(vertical = 8.dp)
            )
        }
        if (showHardResetDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showHardResetDialog = false },
                title = { Text("УДАЛИТЬ ВСЕ ДАННЫЕ?", style = TextStyle(fontWeight = FontWeight.Bold, color = HeroPalette.Red500)) },
                text = {
                    Text(
                        "Будет стёрто (локально И в облаке Google):\n• Анкета (возраст / рост / вес / пол / опыт / травмы)\n• Стиль питания и предпочтения\n• Baseline-тест\n• Замеры тела и вес-лог\n• Прогресс-фото и дневник\n• Герой, билд, серия, монеты, достижения\n\nЭто НЕЛЬЗЯ откатить.",
                        style = TextStyle(fontSize = 12.sp, color = Color.White, lineHeight = 16.sp)
                    )
                },
                confirmButton = {
                    Text(
                        "УДАЛИТЬ ВСЁ",
                        style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = HeroPalette.Red500, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable {
                            scope.launch {
                                val status = heroApp.authRepository.status.value
                                if (status is com.herotraining.data.auth.AuthStatus.SignedIn) {
                                    heroApp.firestoreSync.deleteUserDoc(status.uid)
                                }
                                heroApp.stateRepository.hardReset()
                                showHardResetDialog = false
                                onHardReset()
                            }
                        }.padding(8.dp)
                    )
                },
                dismissButton = {
                    Text(
                        "ОТМЕНА",
                        style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral400),
                        modifier = Modifier.clickable { showHardResetDialog = false }.padding(8.dp)
                    )
                }
            )
        }

        // Crash log access (moved from SignIn since SignIn may not show anymore)
        if (lastCrash != null) {
            Column(
                modifier = Modifier.fillMaxWidth().border(1.dp, HeroPalette.Red500).padding(12.dp)
            ) {
                Text(
                    "⚠ ЗАФИКСИРОВАНА ОШИБКА",
                    style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = HeroPalette.Red500)
                )
                Spacer(Modifier.height(6.dp))
                Row {
                    Text(
                        "ПОКАЗАТЬ",
                        style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Red500, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable { crashVisible = true }.padding(end = 20.dp)
                    )
                    Text(
                        "ОЧИСТИТЬ",
                        style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral400),
                        modifier = Modifier.clickable {
                            com.herotraining.crash.CrashHandler.clearLastCrash(ctx)
                            onClearCrash()
                        }
                    )
                }
            }
            if (crashVisible) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { crashVisible = false },
                    title = { Text("CRASH LOG", style = TextStyle(color = HeroPalette.Red500, fontWeight = FontWeight.Bold)) },
                    text = {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Text(
                                text = lastCrash.take(4000),
                                style = TextStyle(fontSize = 10.sp, color = HeroPalette.Neutral300, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            )
                        }
                    },
                    confirmButton = {
                        Text("ЗАКРЫТЬ",
                            style = TextStyle(color = HeroPalette.Neutral400),
                            modifier = Modifier.clickable { crashVisible = false }.padding(8.dp))
                    }
                )
            }
        }
    }
}

@Composable
private fun ParamRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TextStyle(fontSize = 11.sp, letterSpacing = 1.sp, color = HeroPalette.Neutral500),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral300, fontWeight = FontWeight.Bold)
        )
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
        // Sliced CTA from progress.png mockup — replaces the hand-rolled "+ НОВЫЙ ЗАМЕР"
        // pill with the actual "ДОБАВИТЬ ЗАМЕРЫ +" art (btn_progress_add.webp, 860x95)
        ArtCtaButton(
            resId = com.herotraining.R.drawable.btn_progress_add,
            aspectRatio = 860f / 95f,
            onClick = onAdd
        )
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

/**
 * Tiny AI smoke-test card in profile — proves that Gemini API key + SDK + network path all work.
 * Will be removed once the real mentor/food/workout-gen features land on their own screens.
 *
 * Shows:
 *   - current configured state (key present in BuildConfig?)
 *   - "⚡ ПРОВЕРИТЬ" button that sends a single mentor-greeting request
 *   - last response (or error message) below
 */
@Composable
private fun AiSmokeTestCard(heroName: String?, accent: Color) {
    val configured = com.herotraining.data.ai.GeminiClient.isConfigured
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth().border(1.dp, accent.copy(alpha = 0.6f)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            "AI · GEMINI SMOKE TEST",
            style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = accent)
        )
        Text(
            if (configured) "Ключ подхвачен. Жми чтобы запросить приветствие ментора."
            else "КЛЮЧ НЕ НАСТРОЕН — нужен GEMINI_API_KEY в local.properties.",
            style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400)
        )
        Text(
            if (busy) "ЗАПРОС..." else "⚡ ПРОВЕРИТЬ",
            style = TextStyle(
                fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold,
                color = if (configured && !busy) accent else HeroPalette.Neutral600
            ),
            modifier = Modifier
                .then(
                    if (configured && !busy) Modifier.clickable {
                        busy = true
                        error = null
                        scope.launch {
                            com.herotraining.data.ai.GeminiClient.mentorGreeting(heroName)
                                .onSuccess { result = it; error = null }
                                .onFailure { error = "${it.javaClass.simpleName}: ${it.message ?: "—"}"; result = null }
                            busy = false
                        }
                    } else Modifier
                )
                .padding(vertical = 6.dp)
        )
        result?.let {
            Text(
                text = "«$it»",
                style = TextStyle(fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)
            )
        }
        error?.let {
            Text(
                text = "⚠ $it",
                style = TextStyle(fontSize = 11.sp, color = HeroPalette.Red500, lineHeight = 15.sp)
            )
        }
    }
}

/**
 * Sliced-art button from a mockup crop. Press = scale(0.96). Takes full width by default,
 * height derived from [aspectRatio] so the art stays sharp at any screen size.
 */
@Composable
private fun ArtCtaButton(
    @DrawableRes resId: Int,
    aspectRatio: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        label = "art-cta-scale"
    )
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .scale(scale)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
    )
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
