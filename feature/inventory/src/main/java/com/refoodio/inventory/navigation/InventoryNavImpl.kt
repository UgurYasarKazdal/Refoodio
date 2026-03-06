package com.refoodio.inventory.navigation

import androidx.navigation.compose.composable
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.core.navigation.NavigationRoutes
import com.refoodio.inventory.presentation.add_inventory.AddInventoryScreen
import com.refoodio.inventory.presentation.inventory_list.InventoryScreen
import javax.inject.Inject

internal class InventoryNavImpl @Inject constructor() : FeatureNavEntry {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.composable<NavigationRoutes.InventoryRoute> { // <--- Generic tip olarak veriyoruz
            InventoryScreen(onNavigateToAddInventory = {
                if (navController.currentDestination?.route != NavigationRoutes.InventoryAddRoute::class.qualifiedName) {
                    navController.navigate(NavigationRoutes.InventoryAddRoute)
                }
            }, onNavigateToRecipes = { ids ->
                navController.navigate(NavigationRoutes.RecipeRoute(selectedIds = ids))
            })
        }
        navGraphBuilder.composable<NavigationRoutes.InventoryAddRoute> {
            AddInventoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                })
        }
    }
}