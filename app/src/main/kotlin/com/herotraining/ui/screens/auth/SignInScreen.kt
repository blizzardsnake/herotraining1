package com.herotraining.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.HeroApp
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.ImpactLike
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    onSignedIn: () -> Unit,
    onSkip: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as HeroApp
    val authRepo = app.authRepository
    val scope = rememberCoroutineScope()

    var status by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        scope.launch {
            busy = true
            val r = authRepo.handleSignInResult(result)
            busy = false
            r.onSuccess {
                status = "Вход выполнен: ${it.email ?: it.name ?: it.uid}"
                // Try pulling existing cloud profile
                val pulled = runCatching { app.firestoreSync.pullAll(it.uid) }.getOrDefault(false)
                onSignedIn()
            }.onFailure {
                status = "Ошибка входа: ${it.message}"
            }
        }
    }

    Box(Modifier.fillMaxSize().background(HeroPalette.Black)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.border(1.dp, HeroPalette.Red500).padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ПРОТОКОЛ ГЕРОЯ",
                    style = TextStyle(color = HeroPalette.Red500, fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Medium)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "ВХОД",
                style = TextStyle(fontFamily = ImpactLike, fontSize = 44.sp, fontWeight = FontWeight.Black, color = Color.White)
            )
            Text(
                text = "Чтобы прогресс не терялся",
                style = TextStyle(fontSize = 12.sp, color = HeroPalette.Neutral500)
            )

            Spacer(Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, HeroPalette.Red500)
                    .background(HeroPalette.Red500.copy(alpha = 0.08f))
                    .clickable(enabled = !busy) { launcher.launch(authRepo.createSignInIntent()) }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (busy) "ВХОД..." else "ВОЙТИ ЧЕРЕЗ GOOGLE",
                    style = TextStyle(
                        fontFamily = ImpactLike,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        color = HeroPalette.Red500
                    )
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "ПРОДОЛЖИТЬ БЕЗ ВХОДА →",
                style = TextStyle(fontSize = 11.sp, letterSpacing = 2.sp, color = HeroPalette.Neutral500),
                modifier = Modifier.clickable { onSkip() }.padding(vertical = 8.dp)
            )

            if (status.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = status,
                    style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral400)
                )
            }

            Spacer(Modifier.height(40.dp))
            Text(
                text = "При входе через Google твой профиль, прогресс, замеры\nи дневник будут автоматически синхронизироваться\nи не потеряются при переустановке.",
                style = TextStyle(fontSize = 11.sp, color = HeroPalette.Neutral600),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
