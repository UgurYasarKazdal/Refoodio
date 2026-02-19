package com.refoodio.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalSpacing = staticCompositionLocalOf { RefoodioSpacing() }

data class RefoodioSpacing(
    val default: Dp = 0.dp,
    val small: Dp = 4.dp,
    val medium: Dp = 8.dp,
    val mediumLarge: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 32.dp
)