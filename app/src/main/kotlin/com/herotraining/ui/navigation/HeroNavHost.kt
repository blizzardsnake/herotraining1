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
import com.herotraining.ui.screens.gear.HeroGearFormScreen
import com.herotraining.ui.screens.hero.HeroPlaceholder
import com.herotraining.ui.screens.hero.HeroSelectScreen
import com.herotraining.ui.screens.nutrition.NutritionFormScreen
import com.herotraining.ui.screens.profile.ProfileFormScreen
import com.herotraining.ui.screens.summary.OnboardingSummaryScreen
import kotlinx.coroutines.launch

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
                    val onboarded = app.stateRepository.snapshot().onboarded
                    val target = if (onboarded) Destinations.DASHBOARD else Destinations.PROFILE_INTAKE
                    navController.navigate(target) {
                        popUpTo(Destinations.BOOT) { inclusive = true }
                    }
                }
            })
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
            else HeroGearFormScreen(
                hero = hero,
                onBack = { navController.popBackStack() },
                onComplete = { g ->
                    onboardingVm.setGear(g)
                    navController.navigate(Destinations.buildSelect(heroId))
                }
            )
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
            else BuildSelectScreen(
                hero = hero, age = age,
                onBack = { navController.popBackStack() },
                onSelect = { b ->
                    onboardingVm.setBuild(b)
                    navController.navigate(Destinations.nutritionForm(heroId))
                }
            )
        }

        composable(
            route = Destinations.NUTRITION_FORM,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { bs ->
            val heroId = bs.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            if (hero == null) HeroPlaceholder(heroId) { navController.popBackStack() }
            else NutritionFormScreen(
                hero = hero,
                onBack = { navController.popBackStack() },
                onComplete = { n ->
                    onboardingVm.setNutrition(n)
                    navController.navigate(Destinations.baselineTest(heroId))
                }
            )
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
            else BaselineTestScreen(
                hero = hero, injuries = injuries,
                onBack = { navController.popBackStack() },
                onComplete = { b ->
                    onboardingVm.setBaseline(b)
                    navController.navigate(Destinations.summary(heroId))
                }
            )
        }

        composable(
            route = Destinations.SUMMARY,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { bs ->
            val heroId = bs.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            val draft by onboardingVm.draft.collectAsState()
            val profile = draft.profile
            val build = draft.build
            val nutrition = draft.nutrition
            val baseline = draft.baseline
            if (hero == null || profile == null || build == null || nutrition == null || baseline == null) {
                HeroPlaceholder("${hero?.name ?: heroId} / Сводка") { navController.popBackStack() }
            } else {
                OnboardingSummaryScreen(
                    hero = hero, build = build, profile = profile, gear = draft.gear,
                    onContinue = {
                        scope.launch {
                            app.stateRepository.completeOnboarding(
                                heroId = hero.id, buildId = build.id,
                                profile = profile, nutrition = nutrition,
                                baseline = baseline, gear = draft.gear
                            )
                            navController.navigate(Destinations.DASHBOARD) {
                                popUpTo(Destinations.BOOT) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }

        composable(Destinations.DASHBOARD) {
            DashboardHost(
                onReset = {
                    scope.launch {
                        app.stateRepository.reset()
                        navController.navigate(Destinations.PROFILE_INTAKE) {
                            popUpTo(Destinations.DASHBOARD) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
