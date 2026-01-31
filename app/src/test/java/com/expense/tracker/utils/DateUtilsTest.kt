package com.expense.tracker.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.time.YearMonth

class DateUtilsTest {

    @Test
    fun `formatDate returns correct date format`() {
        val dateTime = LocalDateTime.of(2024, 1, 15, 14, 30)
        val result = DateUtils.formatDate(dateTime)
        
        assertEquals("15 Jan 2024", result)
    }

    @Test
    fun `formatTime returns correct time format`() {
        val dateTime = LocalDateTime.of(2024, 1, 15, 14, 30)
        val result = DateUtils.formatTime(dateTime)
        
        assertEquals("02:30 PM", result)
    }

    @Test
    fun `formatMonthYear returns correct format`() {
        val yearMonth = YearMonth.of(2024, 1)
        val result = DateUtils.formatMonthYear(yearMonth)
        
        assertEquals("January 2024", result)
    }

    @Test
    fun `formatDateDisplay returns correct format`() {
        val dateTime = LocalDateTime.of(2024, 1, 15, 14, 30)
        val result = DateUtils.formatDateDisplay(dateTime)
        
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `getStartOfMonth returns first day at midnight`() {
        val yearMonth = YearMonth.of(2024, 1)
        val result = DateUtils.getStartOfMonth(yearMonth)
        
        assertEquals(1, result.dayOfMonth)
        assertEquals(0, result.hour)
        assertEquals(0, result.minute)
        assertEquals(0, result.second)
    }

    @Test
    fun `getEndOfMonth returns last day at 23-59`() {
        val yearMonth = YearMonth.of(2024, 1)
        val result = DateUtils.getEndOfMonth(yearMonth)
        
        assertEquals(31, result.dayOfMonth)
        assertEquals(23, result.hour)
        assertEquals(59, result.minute)
    }

    @Test
    fun `getStartOfMonth handles February correctly`() {
        val yearMonth = YearMonth.of(2024, 2) // 2024 is leap year
        val result = DateUtils.getEndOfMonth(yearMonth)
        
        assertEquals(29, result.dayOfMonth) // Leap year
    }

    @Test
    fun `formatTime handles AM correctly`() {
        val dateTime = LocalDateTime.of(2024, 1, 15, 9, 15)
        val result = DateUtils.formatTime(dateTime)
        
        assertTrue(result.contains("AM"))
    }

    @Test
    fun `formatTime handles PM correctly`() {
        val dateTime = LocalDateTime.of(2024, 1, 15, 18, 45)
        val result = DateUtils.formatTime(dateTime)
        
        assertTrue(result.contains("PM"))
    }

    @Test
    fun `formatDate handles different months`() {
        val dates = listOf(
            LocalDateTime.of(2024, 1, 1, 0, 0) to "01 Jan 2024",
            LocalDateTime.of(2024, 6, 15, 0, 0) to "15 Jun 2024",
            LocalDateTime.of(2024, 12, 31, 0, 0) to "31 Dec 2024"
        )
        
        dates.forEach { (dateTime, expected) ->
            assertEquals(expected, DateUtils.formatDate(dateTime))
        }
    }
}
