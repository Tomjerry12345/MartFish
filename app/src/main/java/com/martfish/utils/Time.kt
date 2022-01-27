package com.martfish.utils

import java.text.SimpleDateFormat
import java.util.*

object Time {
    val format = SimpleDateFormat("HH", Locale.US)
    val hour: String = format.format(Date())

    val calendar = Calendar.getInstance()

    val hourOfDay = calendar[Calendar.HOUR_OF_DAY]
    val minuteOfDay = calendar[Calendar.MINUTE]
}