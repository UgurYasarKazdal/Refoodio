package com.refoodio.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.refoodio.core.domain.model.settings.AppSettings
import com.refoodio.core.domain.model.settings.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        // Mevcut
        val FOOD_CATALOG_LOADED = booleanPreferencesKey("food_catalog_loaded")
        val LAST_DEVICE_LANG = stringPreferencesKey("last_Device_lang")

        // Ayarlar
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val EXPIRY_NOTIFICATIONS = booleanPreferencesKey("expiry_notifications")
        val NOTIFY_DAYS_BEFORE = intPreferencesKey("notify_days_before")
        val WEEKLY_SUMMARY = booleanPreferencesKey("weekly_summary")
        val DEFAULT_COOKING_METHOD = stringPreferencesKey("default_cooking_method")
        val DEFAULT_DIET_OPTIONS = stringPreferencesKey("default_diet_options")
        val DEFAULT_GOURMET_MODE = booleanPreferencesKey("default_gourmet_mode")
    }

    // ── Mevcut ────────────────────────────────────────────────────────────

    val isFoodCatalogLoaded: Flow<Boolean> =
        dataStore.data.map { it[Keys.FOOD_CATALOG_LOADED] ?: false }

    val lastDeviceLang: Flow<String> =
        dataStore.data.map { it[Keys.LAST_DEVICE_LANG] ?: "" }

    suspend fun setFoodCatalogLoaded(isLoaded: Boolean) {
        dataStore.edit { it[Keys.FOOD_CATALOG_LOADED] = isLoaded }
    }

    suspend fun setLastDeviceLang(lang: String) {
        dataStore.edit { it[Keys.LAST_DEVICE_LANG] = lang }
    }

    // ── AppSettings ───────────────────────────────────────────────────────

    val appSettings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME_MODE]
                ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            expiryNotificationsEnabled = prefs[Keys.EXPIRY_NOTIFICATIONS] ?: true,
            notifyDaysBefore = prefs[Keys.NOTIFY_DAYS_BEFORE] ?: 3,
            weeklySummaryEnabled = prefs[Keys.WEEKLY_SUMMARY] ?: false,
            defaultCookingMethod = prefs[Keys.DEFAULT_COOKING_METHOD] ?: "Tencere",
            defaultDietOptions = prefs[Keys.DEFAULT_DIET_OPTIONS]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toSet()
                ?: emptySet(),
            defaultGourmetMode = prefs[Keys.DEFAULT_GOURMET_MODE] ?: false
        )
    }

    suspend fun saveAppSettings(settings: AppSettings) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = settings.themeMode.name
            prefs[Keys.EXPIRY_NOTIFICATIONS] = settings.expiryNotificationsEnabled
            prefs[Keys.NOTIFY_DAYS_BEFORE] = settings.notifyDaysBefore
            prefs[Keys.WEEKLY_SUMMARY] = settings.weeklySummaryEnabled
            prefs[Keys.DEFAULT_COOKING_METHOD] = settings.defaultCookingMethod
            prefs[Keys.DEFAULT_DIET_OPTIONS] = settings.defaultDietOptions.joinToString(",")
            prefs[Keys.DEFAULT_GOURMET_MODE] = settings.defaultGourmetMode
        }
    }
}
