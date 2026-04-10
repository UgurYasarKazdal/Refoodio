package com.refoodio.home.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refoodio.core.domain.model.home.ExpiryBuckets
import com.refoodio.core.domain.model.home.HomeStats
import com.refoodio.core.domain.model.recipe.FoodCategory
import com.refoodio.core.ui.theme.RefoodioTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    HomeContent(stats = state.stats)
}

@Composable
private fun HomeContent(stats: HomeStats) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = RefoodioTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.large)
    ) {
        item { Spacer(Modifier.height(RefoodioTheme.spacing.medium)) }

        // Başlık
        item {
            Column {
                Text(
                    text = "Merhaba 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (stats.totalCount == 0) "Henüz ürün eklemedin."
                    else "Envanterinde ${stats.totalCount} ürün var.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bugün bozulacak uyarısı
        if (stats.expiringTodayCount > 0) {
            item { ExpiringTodayBanner(count = stats.expiringTodayCount) }
        }

        // 4 özet kart
        item { SummaryCards(stats = stats) }

        // İsraf risk göstergesi
        if (stats.totalCount > 0) {
            item { WasteRiskCard(percent = stats.wasteRiskPercent, count = stats.expiringSoonCount) }
        }

        // Raf ömrü dağılımı
        if (stats.totalCount > 0) {
            item { ExpiryBucketsCard(buckets = stats.expiryBuckets) }
        }

        // Kategori dağılımı
        if (stats.categoryDistribution.isNotEmpty()) {
            item { CategoryDistributionCard(distribution = stats.categoryDistribution) }
        }

        // Market harcaması
        if (stats.storeBreakdown.isNotEmpty()) {
            item { StoreBreakdownCard(stores = stats.storeBreakdown) }
        }

        item { Spacer(Modifier.height(RefoodioTheme.spacing.large)) }
    }
}

// ── Bugün bozulacak banner ──────────────────────────────────────────────────

@Composable
private fun ExpiringTodayBanner(count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier.padding(RefoodioTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.medium)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "Bugün $count ürün bozulacak!",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Hemen kullan veya tüket.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

// ── 4 özet kart ────────────────────────────────────────────────────────────

@Composable
private fun SummaryCards(stats: HomeStats) {
    val currencyFmt = remember { NumberFormat.getNumberInstance(Locale("tr", "TR")).apply { maximumFractionDigits = 0 } }

    Column(verticalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)) {
        Row(horizontalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)) {
            StatCard(
                icon = Icons.Default.Inventory2,
                title = "Toplam Stok",
                value = stats.totalCount.toString(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.Warning,
                title = "Yakında Bozulacak",
                value = stats.expiringSoonCount.toString(),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.weight(1f)
            )
        }
        if (stats.totalValue > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)) {
                StatCard(
                    icon = Icons.Default.AttachMoney,
                    title = "Stok Değeri",
                    value = "₺${currencyFmt.format(stats.totalValue)}",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Warning,
                    title = "Risk'teki Değer",
                    value = "₺${currencyFmt.format(stats.atRiskValue)}",
                    containerColor = Color(0xFFFFE0B2),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(RefoodioTheme.spacing.medium)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall)
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// ── İsraf risk göstergesi ──────────────────────────────────────────────────

@Composable
private fun WasteRiskCard(percent: Float, count: Int) {
    val riskColor = when {
        percent >= 0.5f -> MaterialTheme.colorScheme.error
        percent >= 0.25f -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }
    val riskLabel = when {
        percent >= 0.5f -> "Yüksek Risk"
        percent >= 0.25f -> "Orta Risk"
        else -> "Düşük Risk"
    }

    var animatedPercent by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(percent) { animatedPercent = percent }
    val animProgress by animateFloatAsState(
        targetValue = animatedPercent,
        animationSpec = tween(durationMillis = 800),
        label = "wasteRisk"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(RefoodioTheme.spacing.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("İsraf Riski", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = riskLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = riskColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(RefoodioTheme.spacing.small))
            LinearProgressIndicator(
                progress = { animProgress },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                color = riskColor,
                trackColor = MaterialTheme.colorScheme.surface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${(percent * 100).toInt()}% · $count ürün 3 gün içinde bozulacak",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Raf ömrü dağılımı ──────────────────────────────────────────────────────

@Composable
private fun ExpiryBucketsCard(buckets: ExpiryBuckets) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(RefoodioTheme.spacing.medium)) {
            Text("Raf Ömrü Dağılımı", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(RefoodioTheme.spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(RefoodioTheme.spacing.small)
            ) {
                BucketItem("Bugün", buckets.today, MaterialTheme.colorScheme.error, Modifier.weight(1f))
                BucketItem("Bu Hafta", buckets.thisWeek, Color(0xFFFF9800), Modifier.weight(1f))
                BucketItem("Bu Ay", buckets.thisMonth, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                BucketItem(">30 Gün", buckets.later, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BucketItem(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Kategori dağılımı ──────────────────────────────────────────────────────

@Composable
private fun CategoryDistributionCard(distribution: List<Pair<FoodCategory, Int>>) {
    val maxCount = distribution.maxOfOrNull { it.second }?.toFloat() ?: 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(RefoodioTheme.spacing.medium)) {
            Text("Kategori Dağılımı", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(RefoodioTheme.spacing.medium))
            distribution.forEach { (category, count) ->
                val progress = count / maxCount
                var animated by remember { mutableFloatStateOf(0f) }
                LaunchedEffect(progress) { animated = progress }
                val animProgress by animateFloatAsState(
                    targetValue = animated,
                    animationSpec = tween(600),
                    label = "cat_${category.name}"
                )
                Column(modifier = Modifier.padding(bottom = RefoodioTheme.spacing.small)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(category.nameResId), style = MaterialTheme.typography.bodySmall)
                        Text("$count ürün", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    LinearProgressIndicator(
                        progress = { animProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

// ── Market harcaması ───────────────────────────────────────────────────────

@Composable
private fun StoreBreakdownCard(stores: List<Pair<String, Double>>) {
    val currencyFmt = remember { NumberFormat.getNumberInstance(Locale("tr", "TR")).apply { maximumFractionDigits = 2 } }
    val maxValue = stores.maxOfOrNull { it.second }?.toFloat() ?: 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(RefoodioTheme.spacing.medium)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Market Harcaması", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(RefoodioTheme.spacing.medium))
            stores.forEach { (store, total) ->
                val progress = total.toFloat() / maxValue
                var animated by remember { mutableFloatStateOf(0f) }
                LaunchedEffect(progress) { animated = progress }
                val animProgress by animateFloatAsState(
                    targetValue = animated,
                    animationSpec = tween(600),
                    label = "store_$store"
                )
                Column(modifier = Modifier.padding(bottom = RefoodioTheme.spacing.small)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(store, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        Text("₺${currencyFmt.format(total)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    LinearProgressIndicator(
                        progress = { animProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}
