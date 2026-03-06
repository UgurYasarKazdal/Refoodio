package com.refoodio.inventory.presentation.inventory_list

import com.refoodio.core.domain.model.inventory.FoodGroup
import com.refoodio.core.domain.model.inventory.FoodUnit
import com.refoodio.core.domain.model.inventory.InventoryItem
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.ui.util.UiText

interface InventoryListContract {
    data class State(
        val isLoading: Boolean = false,
        // 1. Kritik Bölüm: SKT'si yaklaşan ürünler (Duplicate gösterim için)
        val criticalItems: List<InventoryItemUiModel> = emptyList(),
        // 2. Ana Bölüm: FoodGroup başlıklarına göre gruplanmış envanter
        val sectionedItems: Map<FoodGroup, List<InventoryItemUiModel>> = emptyMap(),
        val expandedGroups: Set<FoodGroup> = FoodGroup.values().toSet(), // Başlangıçta hepsi açık
        // 3. Seçim Durumu: Sihirbaz Barı için seçilen ürünlerin ID set'i
        val selectedIds: Set<Int> = emptySet(),
        val errorMessage: String? = null
    )

    data class InventoryItemUiModel(
        val id: Int,
        val name: String,
        val quantityText: UiText,
        val formattedDate: UiText,
        val isCritical: Boolean,
        val unit: FoodUnit,
        val category: FoodCategory,
        val originalItem: InventoryItem
    )

    sealed interface Event {
        data object LoadInventories : Event

        // Silme işlemi için ID yeterli olacaktır
        data object DeleteInventory : Event

        // Kart seçimi/iptali için yeni event
        data class OnToggleSelect(val id: Int) : Event

        // Sihirbaz barındaki "Tarif Bul" butonu
        data object OnFindRecipesClick : Event

        // Tüm seçimleri temizle
        data object OnClearSelection : Event

        data object NavigateAddInventory : Event

        data class ToggleGroupExpansion(val foodGroup: FoodGroup) : Event
    }

    sealed interface SideEffect {
        data class ShowSnackbar(val message: UiText) : SideEffect

        // Seçilen ID'leri tarif ekranına aktarmak için string formatında (örn: "1,4,7")
        data class NavigateToRecipesWithFilters(val selectedIds: String) : SideEffect

        data object NavigateToAddInventory : SideEffect
    }
}