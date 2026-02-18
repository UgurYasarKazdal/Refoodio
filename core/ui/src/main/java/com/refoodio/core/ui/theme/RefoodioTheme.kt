package com.refoodio.core.ui.theme

import androidx.compose.runtime.Composable

object RefoodioTheme {
    val spacing: RefoodioSpacing
        @Composable
        get() = LocalSpacing.current
}