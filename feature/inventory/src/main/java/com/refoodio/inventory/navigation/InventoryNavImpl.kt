package com.refoodio.inventory.navigation

import androidx.navigation.compose.composable
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.core.navigation.NavigationRoutes
import com.refoodio.core.navigation.navigateAsBottomNav
import com.refoodio.inventory.presentation.add_inventory.AddInventoryScreen
import com.refoodio.inventory.presentation.inventory_list.InventoryScreen
import com.refoodio.inventory.presentation.receipt_scan.ReceiptScanScreen
import javax.inject.Inject

internal class InventoryNavImpl @Inject constructor() : FeatureNavEntry {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.composable<NavigationRoutes.InventoryRoute> {
            InventoryScreen(
                onNavigateToAddInventory = {
                    navController.navigate(NavigationRoutes.InventoryAddRoute)
                },
                onNavigateToEditInventory = { itemId ->
                    navController.navigate(NavigationRoutes.InventoryEditRoute(itemId))
                },
                onNavigateToReceiptScan = {
                    navController.navigate(NavigationRoutes.ReceiptScanRoute)
                },
                onNavigateToRecipes = { ids ->
                    navController.navigateAsBottomNav(NavigationRoutes.RecipeRoute(selectedIds = ids))
                }
            )
        }
        navGraphBuilder.composable<NavigationRoutes.InventoryAddRoute> {
            AddInventoryScreen(onNavigateBack = { navController.popBackStack() })
        }
        navGraphBuilder.composable<NavigationRoutes.InventoryEditRoute> {
            AddInventoryScreen(onNavigateBack = { navController.popBackStack() })
        }
        navGraphBuilder.composable<NavigationRoutes.ReceiptScanRoute> {
            ReceiptScanScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
