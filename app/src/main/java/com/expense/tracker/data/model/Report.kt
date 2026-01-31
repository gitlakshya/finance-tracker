package com.expense.tracker.data.model

data class MonthlyReport(
    val month: String, // "2024-01"
    val totalExpenses: Double,
    val categoryBreakdown: Map<String, Double>,
    val dailySpending: Map<Int, Double> // day -> amount
)

data class ExpenseWithCategory(
    val id: Long,
    val amount: Double,
    val category: String,
    val description: String,
    val paymentMode: String,
    val date: String,
    val notes: String
)
