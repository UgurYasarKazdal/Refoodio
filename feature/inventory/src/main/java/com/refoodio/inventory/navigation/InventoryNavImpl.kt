package com.refoodio.inventory.navigation

import androidx.navigation.compose.composable
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.core.navigation.NavigationRoutes
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
                    navController.navigate(NavigationRoutes.RecipeRoute(selectedIds = ids)) {
                        // Tab davranışını koru: her zaman InventoryRoute'a kadar temizle
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // launchSingleTop = false: selectedIds değişti, her seferinde
                        // yeni bir RecipeRoute instance'ı oluştur
                        launchSingleTop = false
                        // restoreState = false: eski ViewModel'i restore etme,
                        // yeni selectedIds'li taze bir ViewModel başlasın
                        restoreState = false
                    }
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
