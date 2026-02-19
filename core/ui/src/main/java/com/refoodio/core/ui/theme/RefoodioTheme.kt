package com.refoodio.core.ui.theme

import androidx.compose.runtime.Composable

object RefoodioTheme {
    val spacing: RefoodioSpacing
        @Composable get() = LocalSpacing.current

    val dimens: RefoodioDimens
        @Composable get() = LocalDimens.current

    val stroke: RefoodioStroke
        @Composable get() = LocalStroke.current
}