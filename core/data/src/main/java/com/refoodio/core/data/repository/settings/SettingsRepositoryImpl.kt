package com.refoodio.core.data.repository.settings

import com.refoodio.core.data.local.UserPreferencesDataSource
import com.refoodio.core.database.dao.inventory.InventoryDao
import com.refoodio.core.domain.model.settings.AppSettings
import com.refoodio.core.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val prefsDataSource: UserPreferencesDataSource,
    private val inventoryDao: InventoryDao
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> = prefsDataSource.appSettings

    override suspend fun saveSettings(settings: AppSettings) {
        prefsDataSource.saveAppSettings(settings)
    }

    override suspend fun clearAllInventory() {
        withContext(Dispatchers.IO) {
            inventoryDao.deleteAllInventory()
        }
    }

    override fun exportInventoryCsv(): Flow<String> = flow {
        val items = withContext(Dispatchers.IO) {
            inventoryDao.getAllInventoriesOnce()
        }
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val sb = StringBuilder()
        sb.appendLine("Ad,Kategori,Miktar,Birim,Son Kullanma Tarihi")
        items.forEach { item ->
            val date = dateFormatter.format(Date(item.expiryDate))
            val qty = if (item.quantity % 1.0 == 0.0)
                item.quantity.toInt().toString()
            else
                item.quantity.toString()
            sb.appendLine("${item.name},${item.category.name},$qty,${item.unit.name},$date")
        }
        emit(sb.toString())
    }.flowOn(Dispatchers.IO)
}
