package com.herotraining

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.herotraining.ui.components.UpdateBanner
import com.herotraining.ui.navigation.HeroNavHost
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.HeroTrainingTheme
import com.herotraining.update.UpdateCheckViewModel

class MainActivity : ComponentActivity() {

    private val postNotificationsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        maybeRequestPostNotifications()
        setContent {
            HeroTrainingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    AppRoot()
                }
            }
        }
    }

    private fun maybeRequestPostNotifications() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) postNotificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Composable
private fun AppRoot() {
    val updateVm: UpdateCheckViewModel = viewModel()
    val updateState by updateVm.state.collectAsStateWithLifecycle()

    val navController = androidx.navigation.compose.rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val onBoot = currentRoute?.destination?.route == com.herotraining.ui.navigation.Destinations.BOOT

    Box(modifier = Modifier.fillMaxSize()) {
        HeroNavHost(navController = navController)

        // Update banner overlay. Suppressed on BOOT splash so the two screens don't visually collide.
        if (!onBoot) {
            Column(
                modifier = Modifier.fillMaxSize().statusBarsPadding().padding(top = 6.dp, start = 10.dp, end = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UpdateBanner(
                    state = updateState,
                    accent = HeroPalette.Red500,
                    onDownload = { updateVm.download(it) },
                    onInstall = { updateVm.install() },
                    onDismiss = { updateVm.dismiss() }
                )
            }
        }
    }
}
