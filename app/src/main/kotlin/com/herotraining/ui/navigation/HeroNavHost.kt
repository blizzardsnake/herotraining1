package com.herotraining.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.herotraining.data.catalog.HeroCatalog
import com.herotraining.data.model.Gender
import com.herotraining.ui.screens.gender.GenderSelectScreen
import com.herotraining.ui.screens.hero.HeroSelectScreen

@Composable
fun HeroNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Destinations.GENDER_SELECT) {
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
            val genderKey = backStack.arguments?.getString("gender")
            val gender = Gender.fromKey(genderKey) ?: Gender.MALE
            HeroSelectScreen(
                gender = gender,
                onBack = { navController.popBackStack() },
                onSelect = { hero ->
                    // Next screen (ProfileForm) will be wired when implemented.
                    // For now, re-use the route to keep navigation functional once ProfileFormScreen lands.
                    navController.navigate(Destinations.profileForm(gender.key, hero.id)) {
                        // keep
                    }
                }
            )
        }
        // Placeholder so navigating to profileForm doesn't crash while not implemented.
        composable(
            route = Destinations.PROFILE_FORM,
            arguments = listOf(
                navArgument("gender") { type = NavType.StringType },
                navArgument("heroId") { type = NavType.StringType }
            )
        ) { backStack ->
            val heroId = backStack.arguments?.getString("heroId").orEmpty()
            val hero = HeroCatalog.byId(heroId)
            com.herotraining.ui.screens.hero.HeroPlaceholder(heroName = hero?.name ?: heroId) {
                navController.popBackStack()
            }
        }
    }
}
