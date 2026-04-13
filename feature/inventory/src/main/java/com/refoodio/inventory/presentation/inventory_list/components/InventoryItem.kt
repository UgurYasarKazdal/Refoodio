package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refoodio.inventory.presentation.inventory_list.InventoryListContract

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryItem(
    item: InventoryListContract.InventoryItemUiModel,
    // Tezgah seçimi
    isSelected: Boolean,
    onToggleSelect: (Int) -> Unit,
    // Toplu silme modu
    isInBulkDeleteMode: Boolean,
    isDeleteSelected: Boolean,
    onToggleDeleteSelect: (Int) -> Unit,
    onEnterBulkDeleteMode: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val urgencyColor = when (item.urgencyLevel) {
        InventoryListContract.UrgencyLevel.CRITICAL -> MaterialTheme.colorScheme.error
        InventoryListContract.UrgencyLevel.WARNING  -> MaterialTheme.colorScheme.tertiary
        InventoryListContract.UrgencyLevel.NORMAL   -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    }
    val urgencyLabel = when (item.urgencyLevel) {
        InventoryListContract.UrgencyLevel.CRITICAL -> "Hemen Tüket!"
        InventoryListContract.UrgencyLevel.WARNING  -> "Bu hafta tüket"
        InventoryListContract.UrgencyLevel.NORMAL   -> item.formattedDate.asString()
    }
    val urgencyLabelColor = when (item.urgencyLevel) {
        InventoryListContract.UrgencyLevel.CRITICAL -> MaterialTheme.colorScheme.error
        InventoryListContract.UrgencyLevel.WARNING  -> MaterialTheme.colorScheme.tertiary
        InventoryListContract.UrgencyLevel.NORMAL   -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val cardColor by animateColorAsState(
        targetValue = when {
            isInBulkDeleteMode && isDeleteSelected -> MaterialTheme.colorScheme.errorContainer
            isInBulkDeleteMode -> MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        label = "card_color"
    )

    Card(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isInBulkDeleteMode) onToggleDeleteSelect(item.id)
                    else onToggleSelect(item.id)
                },
                onLongClick = {
                    if (isInBulkDeleteMode) onToggleDeleteSelect(item.id)
                    else onEnterBulkDeleteMode(item.id)
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box {
            Row(modifier = Modifier.fillMaxWidth()) {
                // ── Sol urgency şeridi ─────────────────────────────────
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(urgencyColor)
                )

                // ── Kart içeriği ───────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Kategori ikon + ürün adı
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = item.groupIcon),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Miktar
                    Text(
                        text = "${item.quantityText.asString()} ${stringResource(item.unit.shortNameResId)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Urgency badge veya tarih
                    if (item.urgencyLevel != InventoryListContract.UrgencyLevel.NORMAL) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = urgencyColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = urgencyLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = urgencyLabelColor,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Text(
                            text = urgencyLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // ── Sağ üst köşe badge ─────────────────────────────────────
            val showBadge = isSelected || (isInBulkDeleteMode && isDeleteSelected)
            if (showBadge) {
                val badgeColor = if (isInBulkDeleteMode)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary

                val badgeBgColor = if (isInBulkDeleteMode)
                    MaterialTheme.colorScheme.errorContainer
                else
                    MaterialTheme.colorScheme.primaryContainer

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(20.dp),
                    shape = CircleShape,
                    color = badgeBgColor,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isInBulkDeleteMode) Icons.Default.Close else Icons.Default.Check,
                            contentDescription = if (isInBulkDeleteMode) "Silinecek" else "Seçili",
                            modifier = Modifier.size(12.dp),
                            tint = badgeColor
                        )
                    }
                }
            }
        }
    }
}
