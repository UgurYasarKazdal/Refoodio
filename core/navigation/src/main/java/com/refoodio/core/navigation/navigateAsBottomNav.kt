package com.refoodio.core.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

fun NavHostController.navigateAsBottomNav(route: Any) {
    this.navigate(route) {
        // Grafiğin başlangıç noktasına kadar temizle (genelde Inventory/Home)
        popUpTo(this@navigateAsBottomNav.graph.findStartDestination().id) {
            saveState = true
        }
        // Aynı hedefe üst üste basıldığında yeniden oluşturma
        launchSingleTop = true
        // Önceki kaydedilmiş durumu geri getir
        restoreState = true
    }
}