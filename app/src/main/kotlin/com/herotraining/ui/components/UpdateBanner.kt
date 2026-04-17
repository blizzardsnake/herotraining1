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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Icon
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
import com.herotraining.update.UpdateInfo

/**
 * UI banner shown on the Dashboard when a new APK is available on GitHub.
 * Three states: AVAILABLE (idle) → DOWNLOADING (progress bar) → READY (install CTA).
 */
sealed interface UpdateState {
    data object Hidden : UpdateState
    data class Available(val info: UpdateInfo) : UpdateState
    data class Downloading(val info: UpdateInfo, val percent: Int) : UpdateState
    data class ReadyToInstall(val info: UpdateInfo, val apkPath: String) : UpdateState
}

@Composable
fun UpdateBanner(
    state: UpdateState,
    accent: Color,
    onDownload: (UpdateInfo) -> Unit,
    onInstall: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    if (state is UpdateState.Hidden) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0A))          // opaque, not translucent — so boot terminal doesn't bleed through
            .background(accent.copy(alpha = 0.1f))  // then accent tint on top
            .border(2.dp, accent)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.SystemUpdate, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "ДОСТУПНО ОБНОВЛЕНИЕ",
                style = TextStyle(fontFamily = com.herotraining.ui.theme.Rajdhani, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold, color = accent),
                modifier = Modifier.weight(1f)
            )
            if (state is UpdateState.Available) {
                Icon(
                    Icons.Filled.Close, null, tint = HeroPalette.Neutral500,
                    modifier = Modifier.size(14.dp)
                        .clickable { onDismiss() }
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        val info = when (state) {
            is UpdateState.Available -> state.info
            is UpdateState.Downloading -> state.info
            is UpdateState.ReadyToInstall -> state.info
            else -> null
        }
        if (info != null) {
            Text(
                text = "Версия ${info.versionName} · ${info.apkSizeBytes / 1024 / 1024} МБ",
                style = TextStyle(fontFamily = com.herotraining.ui.theme.Rajdhani, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HeroPalette.Neutral300)
            )
            if (info.releaseNotes.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = info.releaseNotes.take(120) + if (info.releaseNotes.length > 120) "…" else "",
                    style = TextStyle(fontFamily = com.herotraining.ui.theme.Rajdhani, fontSize = 11.sp, color = HeroPalette.Neutral500)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        when (state) {
            is UpdateState.Available -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, accent)
                        .clickable { onDownload(state.info) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "СКАЧАТЬ →",
                        style = TextStyle(
                            fontFamily = ImpactLike,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp,
                            color = accent
                        )
                    )
                }
            }
            is UpdateState.Downloading -> {
                Text(
                    text = "Загрузка... ${state.percent}%",
                    style = TextStyle(fontFamily = com.herotraining.ui.theme.Rajdhani, fontSize = 12.sp, letterSpacing = 2.sp, color = accent)
                )
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth().height(4.dp).background(HeroPalette.Neutral900)) {
                    Box(
                        Modifier
                            .fillMaxWidth(state.percent / 100f)
                            .height(4.dp)
                            .background(accent)
                    )
                }
            }
            is UpdateState.ReadyToInstall -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(accent)
                        .clickable { onInstall() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "УСТАНОВИТЬ →",
                        style = TextStyle(
                            fontFamily = ImpactLike,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp,
                            color = Color.Black
                        )
                    )
                }
            }
            else -> {}
        }
    }
}
