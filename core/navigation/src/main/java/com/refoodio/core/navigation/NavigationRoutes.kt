package com.refoodio.core.navigation

import kotlinx.serialization.Serializable

object NavigationRoutes {
    @Serializable
    object InventoryRoute

    @Serializable
    object InventoryAddRoute

    @Serializable
    data class RecipeRoute(val selectedIds: String? = null)
    @Serializable
    object ReceiptScanRoute

    @Serializable
    object SettingsRoute

    @Serializable
    object HomeRoute
}

