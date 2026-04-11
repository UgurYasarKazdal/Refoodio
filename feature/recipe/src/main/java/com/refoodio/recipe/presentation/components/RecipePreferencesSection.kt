package com.refoodio.recipe.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class CookingMethod(val label: String, val emoji: String)

val cookingMethods = listOf(
    CookingMethod("Tencere", "🍲"),
    CookingMethod("Fırın", "🔥"),
    CookingMethod("Airfryer", "💨"),
    CookingMethod("Izgara", "🥩"),
    CookingMethod("Tavada", "🍳"),
    CookingMethod("Buharda", "♨️"),
    CookingMethod("Barbekü", "🪵")
)

val timeOptions = listOf(15, 30, 45, 60, 90)
val donenessOptions = listOf("Az Pişmiş", "Normal", "İyice Pişmiş")
val dietOptions = listOf(
    "Düşük Kalori",
    "Yüksek Protein",
    "Düşük Karbonhidrat",
    "Vejetaryen",
    "Vegan",
    "Glutensiz"
)

/** Full preferences content — intended to be shown inside a ModalBottomSheet. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipePreferencesContent(
    selectedMethod: String,
    selectedTime: Int,
    isGourmetMode: Boolean,
    selectedDoneness: String,
    selectedDietOptions: Set<String>,
    onMethodChange: (String) -> Unit,
    onTimeChange: (Int) -> Unit,
    onGourmetToggle: () -> Unit,
    onDonenessChange: (String) -> Unit,
    onDietToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Tarif Tercihleri",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // ── Pişirme Yöntemi ──────────────────────────────────────────────
        PreferenceGroup("Pişirme Yöntemi") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                cookingMethods.forEach { method ->
                    FilterChip(
                        selected = selectedMethod == method.label,
                        onClick = { onMethodChange(method.label) },
                        label = { Text("${method.emoji} ${method.label}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ── Maksimum Süre ────────────────────────────────────────────────
        PreferenceGroup("Maksimum Süre") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                timeOptions.forEach { time ->
                    FilterChip(
                        selected = selectedTime == time,
                        onClick = { onTimeChange(time) },
                        label = { Text("$time dk") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ── Pişme Derecesi ───────────────────────────────────────────────
        PreferenceGroup("Pişme Derecesi") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                donenessOptions.forEach { option ->
                    FilterChip(
                        selected = selectedDoneness == option,
                        onClick = { onDonenessChange(option) },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ── Diyet Tercihleri ─────────────────────────────────────────────
        PreferenceGroup("Diyet Tercihleri") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                dietOptions.forEach { option ->
                    FilterChip(
                        selected = option in selectedDietOptions,
                        onClick = { onDietToggle(option) },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ── Tarif Stili ──────────────────────────────────────────────────
        PreferenceGroup("Tarif Stili") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StyleCard(
                    emoji = "♻️",
                    title = "Ekonomik",
                    description = "Elindeki tüm malzemeleri değerlendir, israfı önle",
                    selected = !isGourmetMode,
                    onClick = { if (isGourmetMode) onGourmetToggle() },
                    modifier = Modifier.weight(1f)
                )
                StyleCard(
                    emoji = "✨",
                    title = "Gurme",
                    description = "En uyumlu malzeme kombinasyonunu seç, sunuma önem ver",
                    selected = isGourmetMode,
                    onClick = { if (!isGourmetMode) onGourmetToggle() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StyleCard(
    emoji: String,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface
    val borderColor = if (selected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outlineVariant

    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PreferenceGroup(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}
