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
            RecipeWizardScreen(
                onNavigateBack = {
                    // Eğer back stack'te önceki bir destination varsa pop et,
                    // yoksa (direkt tab ile açıldıysa) envantere git
                    val didPop = navController.popBackStack()
                    if (!didPop) {
                        navController.navigate(NavigationRoutes.InventoryRoute) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

