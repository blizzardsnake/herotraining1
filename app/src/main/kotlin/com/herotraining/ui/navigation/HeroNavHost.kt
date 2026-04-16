package com.herotraining.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.herotraining.data.model.Gender
import com.herotraining.ui.screens.gender.GenderSelectScreen

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
        // Subsequent destinations are added as screens are implemented.
    }
}
