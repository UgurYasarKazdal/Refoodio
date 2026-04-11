package com.refoodio.recipe.navigation

import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.core.navigation.NavigationRoutes
import com.refoodio.recipe.presentation.RecipeWizardScreen
import javax.inject.Inject

internal class RecipeNavImpl @Inject constructor() : FeatureNavEntry {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.composable<NavigationRoutes.RecipeRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<NavigationRoutes.RecipeRoute>()
            // selectedIds varsa → envantardan gelindi, geri oku göster
            val fromInventory = !route.selectedIds.isNullOrBlank()

            RecipeWizardScreen(
                showBackButton = fromInventory,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

