package com.refoodio.core.domain.util

import com.refoodio.core.domain.util.TimeConstants.ONE_DAY_IN_MILLIS

val Int.daysToMillis: Long get() = this * ONE_DAY_IN_MILLIS