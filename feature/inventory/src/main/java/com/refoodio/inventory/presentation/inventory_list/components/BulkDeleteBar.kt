package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.refoodio.core.ui.theme.RefoodioTheme

@Composable
fun BulkDeleteBar(
    selectedCount: Int,
    isVisible: Boolean,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(RefoodioTheme.spacing.medium),
            shape = RoundedCornerShape(RefoodioTheme.spacing.large),
            color = MaterialTheme.colorScheme.errorContainer,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = RefoodioTheme.spacing.medium,
                    vertical = RefoodioTheme.spacing.small
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Çıkış
                IconButton(
                    onClick = onExit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "İptal",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }

                // Seçili sayısı
                Text(
                    text = if (selectedCount > 0) "$selectedCount seçildi" else "Seç",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )

                // Tümünü seç
                IconButton(
                    onClick = onSelectAll,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SelectAll,
                        contentDescription = "Tümünü Seç",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Sil butonu
                Button(
                    onClick = onDelete,
                    enabled = selectedCount > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sil")
                }
            }
        }
    }
}
