package com.refoodio.inventory.presentation.add_inventory.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.refoodio.core.domain.util.toReadableDate
import com.refoodio.core.ui.theme.RefoodioTheme
import com.refoodio.inventory.R

@Composable
fun ExpiryDateSection(
    expiryDate: Long?, onDateClick: () -> Unit, modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable { onDateClick() }
        .padding(vertical = RefoodioTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(RefoodioTheme.spacing.mediumLarge))
        Column {
            Text(
                text = stringResource(R.string.add_inventory_expiry_date_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = expiryDate?.toReadableDate()
                    ?: stringResource(R.string.add_inventory_no_date_selected),
                style = MaterialTheme.typography.bodyLarge,
                color = if (expiryDate == null) MaterialTheme.colorScheme.outline
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}