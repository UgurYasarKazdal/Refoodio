package com.refoodio.core.domain.util

import com.refoodio.core.domain.util.TimeConstants.ONE_DAY_IN_MILLIS
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

val Int.daysToMillis: Long get() = this * ONE_DAY_IN_MILLIS

fun Long.toReadableDate(): String {
    val instant = Instant.ofEpochMilli(this)

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    return formatter.format(instant)
}