package com.herotraining.ui.tabs

import androidx.compose.runtime.Composable
import com.herotraining.ui.screens.dashboard.DashboardHost

/**
 * ГЛАВНАЯ tab. For v0.5.1 wraps the existing Dashboard.
 * In v0.5.2 will be rebuilt to match the new fitness-app home mockup.
 *
 * onOpenProfile wires the 👤 button inside Dashboard's top-bar to switch the PROFILE tab —
 * dual access to the profile (either via bottom nav or the in-dashboard icon).
 */
@Composable
fun HomeTabScreen(
    onReset: () -> Unit,
    onOpenWorkouts: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenProfile: () -> Unit
) {
    DashboardHost(
        onReset = onReset,
        onProfile = onOpenProfile
    )
}
