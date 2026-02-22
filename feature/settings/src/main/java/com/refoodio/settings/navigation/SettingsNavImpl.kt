package com.refoodio.settings.navigation

import androidx.navigation.compose.composable
import com.refoodio.core.navigation.FeatureNavEntry
import com.refoodio.core.navigation.NavigationRoutes
import com.refoodio.settings.presentation.SettingsScreen
import javax.inject.Inject

internal class SettingsNavImpl @Inject constructor() : FeatureNavEntry {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.composable<NavigationRoutes.SettingsRoute> {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

