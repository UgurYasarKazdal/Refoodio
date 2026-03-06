package com.refoodio.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem<T : Any>(
    val route: T, @StringRes val labelRes: Int, val icon: ImageVector
) {
    object Home : BottomNavItem<NavigationRoutes.HomeRoute>(
        route = NavigationRoutes.HomeRoute, labelRes = R.string.nav_home, icon = Icons.Default.Home
    )

    object Inventory : BottomNavItem<NavigationRoutes.InventoryRoute>(
        route = NavigationRoutes.InventoryRoute, R.string.nav_inventory, icon = Icons.Default.List
    )

    object AddItem : BottomNavItem<NavigationRoutes.InventoryAddRoute>(
        route = NavigationRoutes.InventoryAddRoute,
        labelRes = R.string.nav_add_item,
        icon = Icons.Default.AddCircle
    )

    object Recipe : BottomNavItem<NavigationRoutes.RecipeRoute>(
        route = NavigationRoutes.RecipeRoute(selectedIds = null),
        labelRes = R.string.nav_recipe,
        icon = Icons.Default.SoupKitchen
    )

    object Settings : BottomNavItem<NavigationRoutes.SettingsRoute>(
        route = NavigationRoutes.SettingsRoute,
        labelRes = R.string.nav_settings,
        icon = Icons.Default.Settings
    )

}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Inventory,
    BottomNavItem.AddItem,
    BottomNavItem.Recipe,
    BottomNavItem.Settings
)