package com.herotraining.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlagCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herotraining.ui.theme.HeroPalette
import com.herotraining.ui.theme.Rajdhani
import com.herotraining.ui.theme.heroTheme

/**
 * 5-tab bottom navigation using Material3 Scaffold + NavigationBar.
 *
 * Why Material3 instead of hand-rolled Column/Box? Because the custom version failed to
 * render on Xiaomi/MIUI Android 16 (visible only the active tab, centered). Material3's
 * Scaffold is battle-tested across every Android device class and correctly handles window
 * insets, gesture-nav bars, and tablet-vs-phone measurement.
 */
@Composable
fun MainTabsHost(
    onReset: () -> Unit,
    onHardReset: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var tab by remember { mutableStateOf(TabKind.HOME) }
    val accent = heroTheme().heroColor.takeIf { it != HeroPalette.Red500 } ?: HeroPalette.Red500

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF050505),
                modifier = Modifier.border(1.dp, accent.copy(alpha = 0.5f))
            ) {
                TabKind.entries.forEach { kind ->
                    NavigationBarItem(
                        selected = kind == tab,
                        onClick = { tab = kind },
                        icon = {
                            Icon(
                                imageVector = kind.icon,
                                contentDescription = kind.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = kind.label,
                                style = TextStyle(
                                    fontFamily = Rajdhani,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    fontWeight = if (kind == tab) FontWeight.Bold else FontWeight.Normal
                                ),
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = accent,
                            selectedTextColor = accent,
                            indicatorColor = accent.copy(alpha = 0.15f),
                            unselectedIconColor = HeroPalette.Neutral500,
                            unselectedTextColor = HeroPalette.Neutral500
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (tab) {
                TabKind.HOME -> HomeTabScreen(
                    onReset = onReset,
                    onOpenWorkouts = { tab = TabKind.WORKOUTS },
                    onOpenProgress = { tab = TabKind.PROGRESS },
                    onOpenProfile = { tab = TabKind.PROFILE }
                )
                TabKind.WORKOUTS -> WorkoutsTabScreen(onBackToHome = { tab = TabKind.HOME })
                TabKind.QUESTS -> QuestsTabScreen()
                TabKind.PROGRESS -> ProgressTabScreen()
                TabKind.PROFILE -> ProfileTabScreen(
                    onReset = onReset,
                    onHardReset = onHardReset,
                    onSignOut = onSignOut
                )
            }
        }
    }
}

enum class TabKind(val label: String, val icon: ImageVector) {
    HOME("ГЛАВНАЯ", Icons.Filled.Home),
    WORKOUTS("ТРЕНИР", Icons.Filled.FitnessCenter),
    QUESTS("КВЕСТЫ", Icons.Filled.FlagCircle),
    PROGRESS("ПРОГРЕСС", Icons.Filled.BarChart),
    PROFILE("ПРОФИЛЬ", Icons.Filled.Person)
}

typealias MainTab = TabKind
