package com.refoodio.inventory.presentation.add_inventory.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.refoodio.inventory.R

@Composable
fun SearchAndBarcodeField(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        label = { Text(stringResource(R.string.add_inventory_search_label)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}
