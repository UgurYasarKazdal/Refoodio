package com.refoodio.recipe.navigation

import androidx.navigation.compose.composable
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.core.navigation.NavigationRoutes
import com.refoodio.recipe.presentation.RecipeWizardScreen
import javax.inject.Inject

internal class RecipeNavImpl @Inject constructor() : FeatureNavEntry {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.composable<NavigationRoutes.RecipeRoute> {
            RecipeWizardScreen()
        }
    }
}

