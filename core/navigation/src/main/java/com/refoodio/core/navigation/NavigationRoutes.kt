package com.refoodio.core.navigation

import kotlinx.serialization.Serializable

object NavigationRoutes {
    @Serializable
    object InventoryRoute

    @Serializable
    object InventoryAddRoute

    @Serializable
    object RecipeRoute

    @Serializable
    object SettingsRoute

    @Serializable
    object HomeRoute
}

