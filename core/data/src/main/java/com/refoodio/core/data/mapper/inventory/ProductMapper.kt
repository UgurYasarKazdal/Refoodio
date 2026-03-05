package com.refoodio.core.data.mapper.inventory

import com.refoodio.core.network.dto.inventory.BarcodeInventoryDto
import com.refoodio.core.domain.model.catalog.FoodItem
import com.refoodio.core.domain.model.recipe.FoodCategory

fun BarcodeInventoryDto.toDomainModel(): FoodItem {
    return FoodItem(
        id = 0, // Veritabanına kaydedilirken otomatik artan ID alacaktır
        name = this.product_name ?: "Bilinmeyen Ürün",
        // OFF'ta 'brands' ve 'product_name_tr' gibi alanları alternatif isimlere ekleyebilirsin
        alternativeNames = listOfNotNull(this.brands, this.product_name_tr).filter { it.isNotBlank() },

        // OFF'tan gelen kategoriler genelde çok uzundur (en:beverages, tr:içecekler vb.)
        // İlk anlamlı kategoriyi bulmaya çalışalım
        category = this.categories
            ?.split(",")
            ?.map { it.trim() }
            ?.firstOrNull { !it.startsWith("en:") } // Tercihen Türkçe olanı al
            ?: "Genel",

        // Aşağıdaki alanlar OFF API'sinde standart olarak bulunmaz,
        // varsayılan değerlerle başlatıyoruz. Kullanıcı daha sonra düzenleyebilir.
        defaultShelfLife = 7, // Örn: Varsayılan 1 hafta
        isFreezable = false,
        isEssential = false,
        isLiquid = false,
        unit = "Adet",
        categoryId = FoodCategory.OTHER.id
    )
}