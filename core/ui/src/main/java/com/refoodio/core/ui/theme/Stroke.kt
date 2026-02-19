package com.refoodio.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalStroke = staticCompositionLocalOf { RefoodioStroke() }

data class RefoodioStroke(
    val thin: Dp = 1.dp,
    val standard: Dp = 2.dp
)