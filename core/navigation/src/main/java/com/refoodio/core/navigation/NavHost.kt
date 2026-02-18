package com.refoodio.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun RefoodioNavHost(
    navController: NavHostController,
    navEntries: Set<FeatureNavEntry>,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.InventoryRoute,
        modifier = modifier
    ) {
        navEntries.forEach { entry ->
            entry.registerGraph(this, navController)
        }
    }
}