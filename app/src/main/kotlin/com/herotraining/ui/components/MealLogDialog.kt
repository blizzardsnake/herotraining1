package com.herotraining.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.ui.theme.HeroPalette

/** Kind of meal the user tagged — stored as a prefix in [MealEntity.text]. */
enum class MealKind(val label: String, val emoji: String) {
    BREAKFAST("ЗАВТРАК", "🍳"),
    LUNCH("ОБЕД", "🍜"),
    DINNER("УЖИН", "🍲"),
    SNACK("ПЕРЕКУС", "🍎")
}

/**
 * Quick entry for "what I ate now".
 *
 * Deliberately minimal: meal type chip + what you ate + optional kcal.
 * No portion enum, no scale picker, no photo — all that comes later via Gemini Vision.
 * For week 1 ("observation mode") we just want to make logging friction = zero so the
 * app can learn the user's eating rhythm.
 *
 * On save, [onSave] gets invoked with the meal text (kind prefix + description) and
 * the estimated kcal (0 if user didn't fill it in — flagged as `untracked = true`).
 */
@Composable
fun MealLogDialog(
    accent: Color,
    onDismiss: () -> Unit,
    onSave: (text: String, kcal: Int, untracked: Boolean) -> Unit,
    defaultKind: MealKind = MealKind.BREAKFAST
) {
    var kind by remember { mutableStateOf(defaultKind) }
    var text by remember { mutableStateOf("") }
    var kcalStr by remember { mutableStateOf("") }

    val canSave = text.isNotBlank()

    AlertDialog(
        containerColor = Color(0xFF0A0A0A),
        onDismissRequest = onDismiss,
        title = {
            Text(
                "📝 ЗАПИСАТЬ ПРИЁМ ПИЩИ",
                style = TextStyle(fontSize = 14.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = accent)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Meal-kind chips row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MealKind.entries.forEach { k ->
                        val sel = kind == k
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (sel) accent.copy(alpha = 0.18f) else Color.Transparent)
                                .border(1.dp, if (sel) accent else HeroPalette.Neutral800)
                                .clickable { kind = k }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(k.emoji, style = TextStyle(fontSize = 14.sp))
                                Text(
                                    k.label,
                                    style = TextStyle(
                                        fontSize = 8.sp, letterSpacing = 1.sp,
                                        color = if (sel) accent else HeroPalette.Neutral500
                                    )
                                )
                            }
                        }
                    }
                }

                // Main text field
                TextField(
                    value = text,
                    onValueChange = { text = it.take(200) },
                    placeholder = { Text("что ел, примерно сколько", color = HeroPalette.Neutral700) },
                    textStyle = TextStyle(fontSize = 14.sp, color = HeroPalette.Neutral300),
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogFieldColors(accent),
                    minLines = 2,
                    maxLines = 4
                )

                // Kcal estimator (optional)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "КАЛОРИИ (ОПЦ)",
                        style = TextStyle(fontSize = 10.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                        modifier = Modifier.weight(1f)
                    )
                    TextField(
                        value = kcalStr,
                        onValueChange = { kcalStr = it.filter(Char::isDigit).take(4) },
                        placeholder = { Text("—", color = HeroPalette.Neutral700) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontSize = 14.sp, color = accent, fontWeight = FontWeight.Bold),
                        modifier = Modifier.width(120.dp),
                        colors = dialogFieldColors(accent),
                        singleLine = true
                    )
                }

                Text(
                    text = "В v0.10 подсчёт ккал будет делать Gemini по описанию — пока вручную или пропусти (запишется как «не подсчитано»).",
                    style = TextStyle(fontSize = 10.sp, color = HeroPalette.Neutral600, lineHeight = 13.sp)
                )
            }
        },
        confirmButton = {
            Text(
                if (canSave) "✓ СОХРАНИТЬ" else "НАПИШИ ЧТО ЕЛ",
                style = TextStyle(
                    fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold,
                    color = if (canSave) accent else HeroPalette.Neutral600
                ),
                modifier = Modifier
                    .then(
                        if (canSave) Modifier.clickable {
                            val kcal = kcalStr.toIntOrNull() ?: 0
                            val fullText = "[${kind.label}] ${text.trim()}"
                            onSave(fullText, kcal, kcal == 0)
                        } else Modifier
                    )
                    .padding(8.dp)
            )
        },
        dismissButton = {
            Text(
                "ОТМЕНА",
                style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { onDismiss() }.padding(8.dp)
            )
        }
    )
}

@Composable
private fun dialogFieldColors(accent: Color) = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = accent, unfocusedIndicatorColor = HeroPalette.Neutral700,
    cursorColor = accent
)
