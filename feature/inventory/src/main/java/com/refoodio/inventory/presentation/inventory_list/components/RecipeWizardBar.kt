package com.refoodio.inventory.presentation.inventory_list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.refoodio.core.ui.theme.RefoodioTheme

@Composable
fun RecipeWizardBar(
    selectedCount: Int,
    onFindRecipesClick: () -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = selectedCount > 0,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(RefoodioTheme.spacing.medium),
            shape = RoundedCornerShape(RefoodioTheme.spacing.large),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(RefoodioTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "$selectedCount Malzeme Seçildi",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Elinizdekilerle en iyi tarifleri bulun",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(onClick = onFindRecipesClick) {
                    Text("Tarif Bul")
                    Icon(Icons.Default.AutoAwesome, contentDescription = null) // Sihirbaz ikonu
                }
            }
        }
    }
}