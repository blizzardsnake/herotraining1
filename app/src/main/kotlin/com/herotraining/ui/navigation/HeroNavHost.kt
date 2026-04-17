package com.herotraining.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.herotraining.HeroApp
import com.herotraining.data.catalog.HeroCatalog
import com.herotraining.data.model.Gender
import com.herotraining.ui.screens.baseline.BaselineTestScreen
import com.herotraining.ui.screens.boot.BootSplashScreen
import com.herotraining.ui.screens.build.BuildSelectScreen
import com.herotraining.ui.screens.dashboard.DashboardHost
import com.herotraining.ui.tabs.MainTabsHost
import com.herotraining.ui.screens.gear.HeroGearFormScreen
import com.herotraining.ui.screens.hero.HeroPlaceholder
import com.herotraining.ui.screens.hero.HeroSelectScreen
import com.herotraining.ui.screens.auth.SignInScreen
import com.herotraining.ui.screens.nutrition.NutritionFormScreen
import com.herotraining.ui.screens.profile.ProfileFormScreen
import com.herotraining.ui.screens.profile_view.ProfileScreen
import com.herotraining.ui.screens.summary.OnboardingSummaryScreen
import com.herotraining.ui.theme.ProvideHeroTheme
import kotlinx.coroutines.launch

/**
 * Single routing predicate used everywhere — guarantees consistent behaviour for:
 *   - Boot → signed-in user with full state
 *   - SignIn success → where to go based on what was pulled from cloud
 *   - "Сменить героя" → back to HeroSelect, NOT anketa
 */
internal fun routeForState(
    state: com.herotraining.data.model.UserState,
    signedIn: Boolean
): String {
    val profile = state.profile
    val hero = state.hero
    val build = state.build
    return when {
        profile != null && hero != null && build != null -> Destinations.DASHBOARD
        profile != null -> Destinations.heroSelect(profile.sex.key)   // anketa done, pick hero
        signedIn -> Destinations.PROFILE_INTAKE                        // signed in, no profile yet
        else -> Destinations.SIGN_IN                                   // cold start
    }
}

@Composable
fun HeroNavHost(navController: NavHostController = rememberNavController()) {
    val onboardingVm: OnboardingViewModel = viewModel()
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as HeroApp
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = Destinations.BOOT) {

        composable(Destinations.BOOT) {
            BootSplashScreen(onReady = {
                scope.launch {
                    val signedIn = app.authRepository.status.value is com.herotraining.data.auth.AuthStatus.SignedIn
                    // If signed in, pull latest cloud state first
                    if (signedIn) {
                        val uid = (app.authRepository.status.value as com.herotraining.data.auth.AuthStatus.SignedIn).uid
                        runCatching { app.firestoreSync.pullAll(uid) }
                    }
                    val state = app.stateRepository.snapshot()
                    val target = routeForState(state, signedIn)
                    navController.navigate(target) {
                        popUpTo(Destinations.BOOT) { inclusive = true }
                    }
                }
            })
        }

        composable(Destinations.SIGN_IN) {
            SignInScreen(
                onSignedIn = {
                    scope.launch {
                        // pullAll already ran inside SignInScreen handleSignInResult
                        val state = app.stateRepository.snapshot()
                        val dst = routeForState(state, signedIn = true)
                        navController.navigate(dst) {
                            popUpTo(Destinations.SIGN_IN) { inclusive = true }
                        }
                    }
                },
                onSkip = {
                    navController.navigate(Destinations.PROFILE_INTAKE) {
                        popUpTo(Destinations.SIGN_IN) { inclusive = true }
                    }
                }
            )
        }

        // Anketa entry point — age, height, weight, sex, BMI + rest of profile steps
        composable(Destinations.PROFILE_INTAKE) {
            ProfileFormScreen(
                onBack = { /* first real screen — nothing behind */ },
                onComplete = { profile ->
                    onboardingVm.setProfile(profile)
                    navController.navigate(Destinations.heroSelect(profile.sex.key))
                }
            )
        }

        composable(
            route = Destinations.HERO_SELECT,
            arguments = listOf(navArgument("gender") { type = NavType.StringType })
        ) { bs ->
            val gender = Gender.fromKey(bs.arguments?.getString("gender")) ?: Gender.MALE
            HeroSelectScreen(
                gender = gender,
                onBack = { navController.popBackStack() },
                onSelect = { hero -> navController.navigate(Destinations.heroGearForm(hero.id)) }
            )
        }

        composable(
            route = Destinations.HERO_GEAR_FORM,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { bs ->
            val heroId = bs.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            if (hero == null) HeroPlaceholder(heroId) { navController.popBackStack() }
            else ProvideHeroTheme(heroId) {
                HeroGearFormScreen(
                    hero = hero,
                    onBack = { navController.popBackStack() },
                    onComplete = { g ->
                        onboardingVm.setGear(g)
                        navController.navigate(Destinations.buildSelect(heroId))
                    }
                )
            }
        }

        composable(
            route = Destinations.BUILD_SELECT,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { bs ->
            val heroId = bs.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            val draft by onboardingVm.draft.collectAsState()
            val age = draft.profile?.age ?: 0
            if (hero == null) HeroPlaceholder(heroId) { navController.popBackStack() }
            else ProvideHeroTheme(heroId) {
                BuildSelectScreen(
                    hero = hero, age = age,
                    onBack = { navController.popBackStack() },
                    onSelect = { b ->
                        onboardingVm.setBuild(b)
                        // Nutrition + Baseline are filled ONCE per user and frozen.
                        // If already present in saved state, skip straight to Summary.
                        scope.launch {
                            val state = app.stateRepository.snapshot()
                            val dst = when {
                                state.nutrition != null && state.baseline != null -> Destinations.summary(heroId)
                                state.nutrition != null -> Destinations.baselineTest(heroId)
                                else -> Destinations.nutritionForm(heroId)
                            }
                            navController.navigate(dst)
                        }
                    }
                )
            }
        }

        composable(
            route = Destinations.NUTRITION_FORM,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { bs ->
            val heroId = bs.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            if (hero == null) HeroPlaceholder(heroId) { navController.popBackStack() }
            else ProvideHeroTheme(heroId) {
                NutritionFormScreen(
                    hero = hero,
                    onBack = { navController.popBackStack() },
                    onComplete = { n ->
                        onboardingVm.setNutrition(n)
                        // If baseline already taken (test frozen), skip to Summary
                        scope.launch {
                            val state = app.stateRepository.snapshot()
                            val dst = if (state.baseline != null) Destinations.summary(heroId)
                                     else Destinations.baselineTest(heroId)
                            navController.navigate(dst)
                        }
                    }
                )
            }
        }

        composable(
            route = Destinations.BASELINE_TEST,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { bs ->
            val heroId = bs.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            val draft by onboardingVm.draft.collectAsState()
            val injuries = draft.profile?.injuries.orEmpty()
            if (hero == null) HeroPlaceholder(heroId) { navController.popBackStack() }
            else ProvideHeroTheme(heroId) {
                BaselineTestScreen(
                    hero = hero, injuries = injuries,
                    onBack = { navController.popBackStack() },
                    onComplete = { b ->
                        onboardingVm.setBaseline(b)
                        navController.navigate(Destinations.summary(heroId))
                    }
                )
            }
        }

        composable(
            route = Destinations.SUMMARY,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { bs ->
            val heroId = bs.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            val draft by onboardingVm.draft.collectAsState()
            val state by app.stateRepository.observeState()
                .collectAsState(initial = com.herotraining.data.model.DEFAULT_USER_STATE)
            // Prefer draft (just-picked values) but fall back to frozen state so skipped screens don't block Summary
            val profile = draft.profile ?: state.profile
            val build = draft.build
            val nutrition = draft.nutrition ?: state.nutrition
            val baseline = draft.baseline ?: state.baseline
            if (hero == null || profile == null || build == null || nutrition == null || baseline == null) {
                HeroPlaceholder("${hero?.name ?: heroId} / Сводка") { navController.popBackStack() }
            } else ProvideHeroTheme(heroId) {
                OnboardingSummaryScreen(
                    hero = hero, build = build, profile = profile, gear = draft.gear,
                    onContinue = {
                        scope.launch {
                            app.stateRepository.completeOnboarding(
                                heroId = hero.id, buildId = build.id,
                                profile = profile, nutrition = nutrition,
                                baseline = baseline, gear = draft.gear
                            )
                            // Push to cloud if signed in
                            val status = app.authRepository.status.value
                            if (status is com.herotraining.data.auth.AuthStatus.SignedIn) {
                                runCatching { app.firestoreSync.pushAll(status.uid) }
                            }
                            navController.navigate(Destinations.DASHBOARD) {
                                popUpTo(Destinations.BOOT) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }

        composable(Destinations.DASHBOARD) {
            val state by app.stateRepository.observeState()
                .collectAsState(initial = com.herotraining.data.model.DEFAULT_USER_STATE)
            ProvideHeroTheme(state.hero?.id) {
                MainTabsHost(
                    onReset = {
                        scope.launch {
                            app.stateRepository.resetHeroChoice()
                            onboardingVm.clearDraft()
                            val fresh = app.stateRepository.snapshot()
                            fresh.profile?.let { onboardingVm.setProfile(it) }
                            fresh.nutrition?.let { onboardingVm.setNutrition(it) }
                            fresh.baseline?.let { onboardingVm.setBaseline(it) }
                            val dst = fresh.profile?.let { Destinations.heroSelect(it.sex.key) }
                                ?: Destinations.PROFILE_INTAKE
                            navController.navigate(dst) {
                                popUpTo(Destinations.DASHBOARD) { inclusive = true }
                            }
                        }
                    },
                    onHardReset = {
                        // Full wipe (local + cloud) already happened in ProfileScreen.
                        // Now: clear onboarding draft and bounce back to BOOT so routing decides
                        // whether to show SignIn (if signed out) or ProfileIntake (if still signed in).
                        onboardingVm.clearDraft()
                        navController.navigate(Destinations.BOOT) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onSignOut = {
                        // Sign-out preserves local state (user can re-login and keep progress).
                        // But route directly to SIGN_IN so the user can pick a different account
                        // — bypassing routeForState which would route back to DASHBOARD if state
                        // still has profile+hero+build.
                        navController.navigate(Destinations.SIGN_IN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Destinations.PROFILE_VIEW) {
            val state by app.stateRepository.observeState()
                .collectAsState(initial = com.herotraining.data.model.DEFAULT_USER_STATE)
            ProvideHeroTheme(state.hero?.id) {
                ProfileScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
