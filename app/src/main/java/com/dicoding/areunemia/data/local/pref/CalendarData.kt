package com.dicoding.areunemia.data.local.pref

import java.util.Date

data class CalendarData(
    val date: Date,
    val calendarDate: String,
    val calendarDay: String,
    var isSelected: Boolean = false,
    val hasEvent: Boolean = false
)
