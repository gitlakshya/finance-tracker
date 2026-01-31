package com.expense.tracker.utils

import com.expense.tracker.data.model.Expense
import com.expense.tracker.data.model.MonthlyReport
import java.time.LocalDateTime
import java.time.YearMonth

object ReportGenerator {
    fun generateMonthlyReport(
        expenses: List<Expense>,
        yearMonth: YearMonth
    ): MonthlyReport {
        val filtered = expenses.filter {
            val itemMonth = YearMonth.from(it.date)
            itemMonth == yearMonth
        }

        val totalExpenses = filtered.sumOf { it.amount }

        val categoryBreakdown = filtered
            .groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.amount } }

        val dailySpending = mutableMapOf<Int, Double>()
        filtered.forEach { expense ->
            val day = expense.date.dayOfMonth
            dailySpending[day] = (dailySpending[day] ?: 0.0) + expense.amount
        }

        return MonthlyReport(
            month = yearMonth.toString(),
            totalExpenses = totalExpenses,
            categoryBreakdown = categoryBreakdown,
            dailySpending = dailySpending.toSortedMap()
        )
    }

    fun generateYearlyReport(expenses: List<Expense>, year: Int): Map<String, MonthlyReport> {
        val reports = mutableMapOf<String, MonthlyReport>()

        (1..12).forEach { month ->
            val yearMonth = YearMonth.of(year, month)
            reports[yearMonth.toString()] = generateMonthlyReport(expenses, yearMonth)
        }

        return reports
    }

    fun getTopCategories(report: MonthlyReport, limit: Int = 5): List<Pair<String, Double>> {
        return report.categoryBreakdown
            .toList()
            .sortedByDescending { it.second }
            .take(limit)
    }

    fun getAverageDailyExpense(report: MonthlyReport): Double {
        return if (report.dailySpending.isEmpty()) {
            0.0
        } else {
            report.totalExpenses / report.dailySpending.size
        }
    }

    fun getHighestExpenseDay(report: MonthlyReport): Pair<Int, Double>? {
        return report.dailySpending.maxByOrNull { it.value }?.toPair()
    }

    fun getLowestExpenseDay(report: MonthlyReport): Pair<Int, Double>? {
        return report.dailySpending.minByOrNull { it.value }?.toPair()
    }

    fun comparePeriods(
        currentReport: MonthlyReport,
        previousReport: MonthlyReport
    ): Map<String, Double> {
        val totalDiff = currentReport.totalExpenses - previousReport.totalExpenses
        val totalPercentChange = if (previousReport.totalExpenses > 0) {
            (totalDiff / previousReport.totalExpenses) * 100
        } else {
            0.0
        }

        return mapOf(
            "total_difference" to totalDiff,
            "total_percent_change" to totalPercentChange,
            "current_total" to currentReport.totalExpenses,
            "previous_total" to previousReport.totalExpenses
        )
    }
}
