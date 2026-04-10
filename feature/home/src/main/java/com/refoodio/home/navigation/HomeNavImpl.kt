package com.refoodio.home.navigation

import androidx.navigation.compose.composable
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.core.navigation.NavigationRoutes
import com.refoodio.home.presentation.HomeScreen
import javax.inject.Inject

internal class HomeNavImpl @Inject constructor() : FeatureNavEntry {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.composable<NavigationRoutes.HomeRoute> {
            HomeScreen()
        }
    }
}
