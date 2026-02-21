package com.refoodio.core.ui.util

import java.text.DecimalFormat

fun Double.formatQuantity(): String {
    val df = DecimalFormat("#.##")
    return df.format(this)
}