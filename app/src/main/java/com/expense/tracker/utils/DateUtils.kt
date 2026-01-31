package com.expense.tracker.utils

import android.icu.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.*

object DateUtils {
    fun getStartOfMonth(yearMonth: YearMonth): LocalDateTime {
        return yearMonth.atDay(1).atStartOfDay()
    }

    fun getEndOfMonth(yearMonth: YearMonth): LocalDateTime {
        return yearMonth.atEndOfMonth().atTime(23, 59, 59)
    }

    fun formatDate(dateTime: LocalDateTime): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))
    }

    fun formatTime(dateTime: LocalDateTime): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))
    }

    fun formatMonthYear(yearMonth: YearMonth): String {
        val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.set(yearMonth.year, yearMonth.monthValue - 1, 1)
        return formatter.format(cal.time)
    }

    fun getDayOfMonth(dateTime: LocalDateTime): Int {
        return dateTime.dayOfMonth
    }

    fun formatDateDisplay(dateTime: LocalDateTime): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()))
    }
}
