package com.refoodio.home.domain

import com.refoodio.core.domain.model.home.ExpiryBuckets
import com.refoodio.core.domain.model.home.HomeStats
import com.refoodio.core.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetHomeStatsUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<HomeStats> = repository.getAllInventories().map { items ->
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        val threeDaysMs = 3 * oneDayMs
        val sevenDaysMs = 7 * oneDayMs
        val thirtyDaysMs = 30 * oneDayMs

        val expiringSoon = items.filter { it.expiryDate in now..(now + threeDaysMs) }
        val expiringToday = items.filter { it.expiryDate in now..(now + oneDayMs) }

        val totalValue = items.sumOf { it.price ?: 0.0 }
        val atRiskValue = expiringSoon.sumOf { it.price ?: 0.0 }

        val wasteRiskPercent = if (items.isEmpty()) 0f
        else expiringSoon.size.toFloat() / items.size

        val categoryDistribution = items
            .groupBy { it.category }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(6)

        val storeBreakdown = items
            .filter { it.storeName != null && it.price != null }
            .groupBy { it.storeName!! }
            .mapValues { entry -> entry.value.sumOf { it.price!! } }
            .toList()
            .sortedByDescending { it.second }
            .take(5)

        val expiryBuckets = ExpiryBuckets(
            today = items.count { it.expiryDate in now..(now + oneDayMs) },
            thisWeek = items.count { it.expiryDate in (now + oneDayMs + 1)..(now + sevenDaysMs) },
            thisMonth = items.count { it.expiryDate in (now + sevenDaysMs + 1)..(now + thirtyDaysMs) },
            later = items.count { it.expiryDate > now + thirtyDaysMs }
        )

        HomeStats(
            totalCount = items.size,
            expiringSoonCount = expiringSoon.size,
            expiringTodayCount = expiringToday.size,
            totalValue = totalValue,
            atRiskValue = atRiskValue,
            wasteRiskPercent = wasteRiskPercent,
            categoryDistribution = categoryDistribution,
            storeBreakdown = storeBreakdown,
            expiryBuckets = expiryBuckets
        )
    }
}
