package com.expense.tracker.utils

import com.expense.tracker.data.model.Expense
import java.time.LocalDateTime

object FilterUtils {
    fun filterExpensesByDateRange(
        expenses: List<Expense>,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Expense> {
        return expenses.filter { expense ->
            expense.date >= startDate && expense.date <= endDate
        }
    }

    fun filterExpensesByCategory(
        expenses: List<Expense>,
        categories: List<String>
    ): List<Expense> {
        return if (categories.isEmpty()) {
            expenses
        } else {
            expenses.filter { it.category in categories }
        }
    }

    fun filterExpensesByAmountRange(
        expenses: List<Expense>,
        minAmount: Double,
        maxAmount: Double
    ): List<Expense> {
        return expenses.filter { expense ->
            expense.amount >= minAmount && expense.amount <= maxAmount
        }
    }

    fun filterExpensesByPaymentMode(
        expenses: List<Expense>,
        paymentModes: List<String>
    ): List<Expense> {
        return if (paymentModes.isEmpty()) {
            expenses
        } else {
            expenses.filter { it.paymentMode in paymentModes }
        }
    }

    fun filterExpensesBySource(
        expenses: List<Expense>,
        source: String // "SMS", "MANUAL", or "ALL"
    ): List<Expense> {
        return if (source == "ALL") {
            expenses
        } else {
            expenses.filter { it.source == source }
        }
    }

    fun searchExpenses(
        expenses: List<Expense>,
        query: String
    ): List<Expense> {
        val lowerQuery = query.lowercase()
        return expenses.filter { expense ->
            expense.description.lowercase().contains(lowerQuery) ||
            expense.category.lowercase().contains(lowerQuery) ||
            expense.merchant.lowercase().contains(lowerQuery) ||
            expense.notes.lowercase().contains(lowerQuery)
        }
    }

    fun applyMultipleFilters(
        expenses: List<Expense>,
        startDate: LocalDateTime? = null,
        endDate: LocalDateTime? = null,
        categories: List<String> = emptyList(),
        minAmount: Double = 0.0,
        maxAmount: Double = Double.MAX_VALUE,
        paymentModes: List<String> = emptyList(),
        searchQuery: String = ""
    ): List<Expense> {
        var filtered = expenses

        if (startDate != null && endDate != null) {
            filtered = filterExpensesByDateRange(filtered, startDate, endDate)
        }

        if (categories.isNotEmpty()) {
            filtered = filterExpensesByCategory(filtered, categories)
        }

        filtered = filterExpensesByAmountRange(filtered, minAmount, maxAmount)

        if (paymentModes.isNotEmpty()) {
            filtered = filterExpensesByPaymentMode(filtered, paymentModes)
        }

        if (searchQuery.isNotEmpty()) {
            filtered = searchExpenses(filtered, searchQuery)
        }

        return filtered.sortedByDescending { it.date }
    }
}
