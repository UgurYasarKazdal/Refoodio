package com.refoodio.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.refoodio.core.ui.theme.RefoodioTheme


@Composable
fun RefoodioPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isEnabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = isEnabled && !isLoading,
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(RefoodioTheme.dimens.loadingIndicatorSmall), // Veya RefoodioTheme.dimens.loadingIndicatorSmall
                strokeWidth = RefoodioTheme.stroke.standard,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = text, style = MaterialTheme.typography.labelLarge
            )
        }
    }
}