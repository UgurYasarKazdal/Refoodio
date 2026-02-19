package com.refoodio.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


val LocalDimens = staticCompositionLocalOf { RefoodioDimens() }

data class RefoodioDimens(
    val iconSizeSmall: Dp = 16.dp,
    val iconSizeMedium: Dp = 24.dp,
    val loadingIndicatorSmall: Dp = 20.dp,
    val suggestionListMaxHeight: Dp = 200.dp,
    val minClickTarget: Dp = 48.dp
)