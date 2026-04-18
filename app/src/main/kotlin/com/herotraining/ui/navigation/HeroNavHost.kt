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
import com.herotraining.ui.screens.disclaimer.DisclaimerScreen
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
 * Single routing predicate — used by BOOT, SignIn success, and any nav refresh.
 *
 * NEW ORDER (per user's product vision):
 *   SignIn → Disclaimer → Profile → Nutrition → Baseline → HeroSelect → Gear → Build → Tomorrow → Dashboard
 *
 * Baseline + Nutrition now happen BEFORE hero select (they're hero-agnostic data).
 * Disclaimer gate keeps personal questions behind explicit consent.
 */
internal fun routeForState(
    state: com.herotraining.data.model.UserState,
    signedIn: Boolean
): String {
    return when {
        !signedIn -> Destinations.SIGN_IN
        !state.disclaimerAccepted -> Destinations.DISCLAIMER
        state.profile == null -> Destinations.PROFILE_INTAKE
        state.nutrition == null -> Destinations.NUTRITION_INTAKE
        state.baseline == null -> Destinations.BASELINE_INTAKE
        state.hero == null -> Destinations.heroSelect(state.profile.sex.key)
        state.build == null -> Destinations.buildSelect(state.hero.id)
        // onboarded = true OR hero+build set — go to Dashboard. Tomorrow-lock is handled
        // inside Dashboard/HomeTab by checking programStartDate vs. now.
        else -> Destinations.DASHBOARD
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
                        val state = app.stateRepository.snapshot()
                        val dst = routeForState(state, signedIn = true)
                        navController.navigate(dst) {
                            popUpTo(Destinations.SIGN_IN) { inclusive = true }
                        }
                    }
                },
                onSkip = {
                    // Even skipping sign-in must first clear the disclaimer gate.
                    navController.navigate(Destinations.DISCLAIMER) {
                        popUpTo(Destinations.SIGN_IN) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.DISCLAIMER) {
            DisclaimerScreen(
                onAccepted = {
                    // Navigate IMMEDIATELY after local DB write — cloud push is fire-and-forget
                    // so a hanging network request can't strand the user on this screen.
                    scope.launch {
                        app.stateRepository.acceptDisclaimer()
                        navController.navigate(Destinations.PROFILE_INTAKE) {
                            popUpTo(Destinations.DISCLAIMER) { inclusive = true }
                        }
                    }
                    scope.launch {
                        val status = app.authRepository.status.value
                        if (status is com.herotraining.data.auth.AuthStatus.SignedIn) {
                            runCatching { app.firestoreSync.pushAll(status.uid) }
                        }
                    }
                }
            )
        }

        // Anketa — age, height, weight, sex, BMI, experience, equipment, injuries
        composable(Destinations.PROFILE_INTAKE) {
            ProfileFormScreen(
                onBack = { /* disclaimer is behind — don't bounce back there */ },
                onComplete = { profile ->
                    onboardingVm.setProfile(profile)
                    scope.launch {
                        app.stateRepository.saveProfile(profile)
                        navController.navigate(Destinations.NUTRITION_INTAKE)
                    }
                }
            )
        }

        // Food preferences — hero-agnostic, uses default accent
        composable(Destinations.NUTRITION_INTAKE) {
            NutritionFormScreen(
                onBack = { navController.popBackStack() },
                onComplete = { n ->
                    onboardingVm.setNutrition(n)
                    scope.launch {
                        app.stateRepository.saveNutrition(n)
                        navController.navigate(Destinations.BASELINE_INTAKE)
                    }
                }
            )
        }

        // Baseline test — sensory read of current level, skippable exercises
        composable(Destinations.BASELINE_INTAKE) {
            val draft by onboardingVm.draft.collectAsState()
            val injuries = draft.profile?.injuries.orEmpty()
            BaselineTestScreen(
                injuries = injuries,
                onBack = { navController.popBackStack() },
                onComplete = { b ->
                    onboardingVm.setBaseline(b)
                    scope.launch {
                        app.stateRepository.saveBaseline(b)
                        val state = app.stateRepository.snapshot()
                        val sex = state.profile?.sex?.key ?: Gender.MALE.key
                        navController.navigate(Destinations.heroSelect(sex))
                    }
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
                        // By the time we're here we already have profile/nutrition/baseline saved.
                        // Jump straight to the Tomorrow checkpoint.
                        navController.navigate(Destinations.tomorrow(heroId))
                    }
                )
            }
        }

        // TOMORROW — "Тест зачтён. Первая миссия завтра в 10:00."
        // Reuses existing OnboardingSummaryScreen which renders the recap + "НАЧАТЬ →".
        composable(
            route = Destinations.TOMORROW,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { bs ->
            val heroId = bs.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            val draft by onboardingVm.draft.collectAsState()
            val state by app.stateRepository.observeState()
                .collectAsState(initial = com.herotraining.data.model.DEFAULT_USER_STATE)
            val profile = draft.profile ?: state.profile
            val build = draft.build
            val nutrition = draft.nutrition ?: state.nutrition
            val baseline = draft.baseline ?: state.baseline
            if (hero == null || profile == null || build == null || nutrition == null || baseline == null) {
                HeroPlaceholder("${hero?.name ?: heroId} / ЗАВТРА") { navController.popBackStack() }
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
                        onboardingVm.clearDraft()
                        navController.navigate(Destinations.BOOT) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onSignOut = {
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
