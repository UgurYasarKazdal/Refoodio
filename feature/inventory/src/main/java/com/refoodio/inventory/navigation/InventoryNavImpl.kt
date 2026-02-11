package com.refoodio.inventory.navigation

import androidx.navigation.compose.composable
import com.refoodio.inventory.presentation.inventory_list.InventoryScreen
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.core.navigation.NavigationRoutes
import javax.inject.Inject

internal class InventoryNavImpl @Inject constructor() :
    FeatureNavEntry {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.composable<NavigationRoutes.InventoryRoute> { // <--- Generic tip olarak veriyoruz
            InventoryScreen()
        }
    }
}