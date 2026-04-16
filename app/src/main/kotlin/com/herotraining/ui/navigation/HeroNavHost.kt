package com.herotraining.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.herotraining.data.catalog.HeroCatalog
import com.herotraining.data.model.Gender
import com.herotraining.ui.screens.boot.BootSplashScreen
import com.herotraining.ui.screens.build.BuildSelectScreen
import com.herotraining.ui.screens.gear.HeroGearFormScreen
import com.herotraining.ui.screens.gender.GenderSelectScreen
import com.herotraining.ui.screens.hero.HeroPlaceholder
import com.herotraining.ui.screens.hero.HeroSelectScreen
import com.herotraining.ui.screens.profile.ProfileFormScreen

@Composable
fun HeroNavHost(navController: NavHostController = rememberNavController()) {
    // Shared VM for draft onboarding data across all form screens.
    val onboardingVm: OnboardingViewModel = viewModel()

    NavHost(navController = navController, startDestination = Destinations.BOOT) {

        composable(Destinations.BOOT) {
            BootSplashScreen(onReady = {
                navController.navigate(Destinations.GENDER_SELECT) {
                    popUpTo(Destinations.BOOT) { inclusive = true }
                }
            })
        }

        composable(Destinations.GENDER_SELECT) {
            GenderSelectScreen(
                onSelect = { gender ->
                    navController.navigate(Destinations.heroSelect(gender.key))
                }
            )
        }

        composable(
            route = Destinations.HERO_SELECT,
            arguments = listOf(navArgument("gender") { type = NavType.StringType })
        ) { backStack ->
            val gender = Gender.fromKey(backStack.arguments?.getString("gender")) ?: Gender.MALE
            HeroSelectScreen(
                gender = gender,
                onBack = { navController.popBackStack() },
                onSelect = { hero ->
                    navController.navigate(Destinations.profileForm(gender.key, hero.id))
                }
            )
        }

        composable(
            route = Destinations.PROFILE_FORM,
            arguments = listOf(
                navArgument("gender") { type = NavType.StringType },
                navArgument("heroId") { type = NavType.StringType }
            )
        ) { backStack ->
            val heroId = backStack.arguments?.getString("heroId").orEmpty()
            val genderKey = backStack.arguments?.getString("gender").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            val gender = Gender.fromKey(genderKey) ?: Gender.MALE
            if (hero == null) {
                HeroPlaceholder(heroName = heroId) { navController.popBackStack() }
            } else {
                ProfileFormScreen(
                    hero = hero,
                    gender = gender,
                    onBack = { navController.popBackStack() },
                    onComplete = { profile ->
                        onboardingVm.setProfile(profile)
                        navController.navigate(Destinations.heroGearForm(heroId))
                    }
                )
            }
        }

        composable(
            route = Destinations.HERO_GEAR_FORM,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { backStack ->
            val heroId = backStack.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            if (hero == null) {
                HeroPlaceholder(heroName = heroId) { navController.popBackStack() }
            } else {
                HeroGearFormScreen(
                    hero = hero,
                    onBack = { navController.popBackStack() },
                    onComplete = { selection ->
                        onboardingVm.setGear(selection)
                        navController.navigate(Destinations.buildSelect(heroId))
                    }
                )
            }
        }

        composable(
            route = Destinations.BUILD_SELECT,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { backStack ->
            val heroId = backStack.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            val draft by onboardingVm.draft.collectAsState()
            val age = draft.profile?.age ?: 0
            if (hero == null) {
                HeroPlaceholder(heroName = heroId) { navController.popBackStack() }
            } else {
                BuildSelectScreen(
                    hero = hero,
                    age = age,
                    onBack = { navController.popBackStack() },
                    onSelect = { build ->
                        onboardingVm.setBuild(build)
                        navController.navigate(Destinations.nutritionForm(heroId))
                    }
                )
            }
        }

        // Remaining screens — placeholders for now.
        composable(
            route = Destinations.NUTRITION_FORM,
            arguments = listOf(navArgument("heroId") { type = NavType.StringType })
        ) { backStack ->
            val heroId = backStack.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            HeroPlaceholder(heroName = "${hero?.name ?: heroId} / NutritionForm") {
                navController.popBackStack()
            }
        }
    }
}
