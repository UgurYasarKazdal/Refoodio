package com.refoodio.inventory.presentation.add_inventory.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    onBarcodeClick: () -> Unit,
    onReceiptClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        label = { Text(stringResource(R.string.add_inventory_search_label)) },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            Row {
                IconButton(onClick = onReceiptClick) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = stringResource(R.string.add_inventory_receipt_content_desc)
                    )
                }
                IconButton(onClick = onBarcodeClick) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = stringResource(R.string.add_inventory_barcode_content_desc)
                    )
                }
            }
        },
        singleLine = true
    )
}