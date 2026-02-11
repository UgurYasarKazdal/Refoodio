package com.refoodio.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface FeatureNavEntry {
    fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}