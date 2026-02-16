package com.refoodio.inventory.navigation

import androidx.navigation.compose.composable
import com.refoodio.inventory.presentation.inventory_list.InventoryScreen
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.core.navigation.NavigationRoutes
import com.refoodio.inventory.presentation.add_inventory.AddInventoryScreen
import javax.inject.Inject

internal class InventoryNavImpl @Inject constructor() :
    FeatureNavEntry {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.composable<NavigationRoutes.InventoryRoute> { // <--- Generic tip olarak veriyoruz
            InventoryScreen(onNavigateToAddInventory = {
                // Compose Navigation'da tip güvenli (Type Safe) navigasyon kullanıyorsan:
                navController.navigate(NavigationRoutes.InventoryAddRoute)
            })
        }
        navGraphBuilder.composable<NavigationRoutes.InventoryAddRoute> {
            AddInventoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}