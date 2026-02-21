package com.refoodio.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.refoodio.core.ui.theme.RefoodioTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RefoodioDropdown(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemLabel: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val lineColor = MaterialTheme.colorScheme.outlineVariant

    ExposedDropdownMenuBox(
        expanded = isExpanded, onExpandedChange = { isExpanded = it }, modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .drawBehind {
                    val strokeWidthPx = 1.dp.toPx()
                    val verticalOffset = size.height - strokeWidthPx / 2
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, verticalOffset),
                        end = Offset(size.width, verticalOffset),
                        strokeWidth = strokeWidthPx
                    )
                }
                .padding(bottom = RefoodioTheme.spacing.small, end = RefoodioTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = itemLabel(selectedItem),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = RefoodioTheme.spacing.small)
            )
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
        }

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.widthIn(min = 150.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                    Text(
                        text = itemLabel(item), style = MaterialTheme.typography.titleMedium
                    )
                }, onClick = {
                    onItemSelected(item)
                    isExpanded = false
                }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}