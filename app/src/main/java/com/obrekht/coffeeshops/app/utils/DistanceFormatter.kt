package com.obrekht.coffeeshops.app.utils

import android.content.Context
import com.obrekht.coffeeshops.R
import java.text.NumberFormat

fun Long.formatDistance(context: Context): String {
    val value = when {
        this > 1000 -> this / 1000.0
        else -> this
    }
    val unit = when {
        this > 1000 -> context.getString(R.string.unit_short_kilometers)
        else -> context.getString(R.string.unit_short_meters)
    }
    return NumberFormat.getNumberInstance().apply {
        maximumFractionDigits = 1
    }.format(value) + " $unit"
}
