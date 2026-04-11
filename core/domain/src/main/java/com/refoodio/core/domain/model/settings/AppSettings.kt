package com.refoodio.core.domain.model.settings

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val expiryNotificationsEnabled: Boolean = true,
    val notifyDaysBefore: Int = 3,
    val weeklySummaryEnabled: Boolean = false,
    val defaultCookingMethod: String = "Tencere",
    val defaultMaxTime: Int = 30,
    val defaultDoneness: String = "Normal",
    val defaultDietOptions: Set<String> = emptySet(),
    val defaultGourmetMode: Boolean = false
)

enum class ThemeMode(val labelTr: String) {
    SYSTEM("Sistem"),
    LIGHT("Açık"),
    DARK("Koyu")
}
