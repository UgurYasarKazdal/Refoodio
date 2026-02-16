package com.refoodio.core.domain.util

import com.refoodio.core.domain.util.TimeConstants.ONE_DAY_IN_MILLIS

val Int.daysToMillis: Long get() = this * ONE_DAY_IN_MILLIS

// core/domain/util/DateExt.kt
fun Long.toReadableDate(): String {
    val date = java.util.Date(this)
    val format = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
    return format.format(date)
}