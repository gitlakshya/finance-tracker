package com.expense.tracker.repository

import com.expense.tracker.data.database.CategoryDao
import com.expense.tracker.data.database.ExpenseDao
import com.expense.tracker.data.database.DailySpending
import com.expense.tracker.data.database.MerchantTotal
import com.expense.tracker.data.database.PaymentModeTotal
import com.expense.tracker.data.model.Category
import com.expense.tracker.data.model.Expense
import com.expense.tracker.data.model.DEFAULT_CATEGORIES
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) {
    // Expense operations
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    suspend fun deleteExpensesByIds(ids: List<Long>) = expenseDao.deleteExpensesByIds(ids)

    suspend fun getExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<Expense> =
        expenseDao.getExpensesByDateRange(startDate, endDate)

    suspend fun getExpensesByCategory(category: String): List<Expense> =
        expenseDao.getExpensesByCategory(category)

    suspend fun getExpensesByAmountRange(minAmount: Double, maxAmount: Double): List<Expense> =
        expenseDao.getExpensesByAmountRange(minAmount, maxAmount)

    suspend fun getExpensesByPaymentMode(paymentMode: String): List<Expense> =
        expenseDao.getExpensesByPaymentMode(paymentMode)

    suspend fun getTotalExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Double =
        expenseDao.getTotalExpensesByDateRange(startDate, endDate) ?: 0.0

    suspend fun getCategoryBreakdown(startDate: LocalDateTime, endDate: LocalDateTime) =
        expenseDao.getCategoryBreakdown(startDate, endDate)

    suspend fun getExpenseCountByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Int =
        expenseDao.getExpenseCountByDateRange(startDate, endDate)

    // Deduplication
    suspend fun getExpenseBySmsId(smsId: String): Expense? = expenseDao.getExpenseBySmsId(smsId)

    suspend fun deleteBySmsId(smsId: String) = expenseDao.deleteBySmsId(smsId)

    // Category operations
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun getCategoryByName(name: String): Category? = categoryDao.getCategoryByName(name)

    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    suspend fun deleteCustomCategory(name: String) = categoryDao.deleteCustomCategory(name)

    suspend fun initializeDefaultCategories() {
        val existingCategories = categoryDao.getDefaultCategories()
        if (existingCategories.isEmpty()) {
            DEFAULT_CATEGORIES.forEach { categoryDao.insertCategory(it) }
        }
    }

    // Daily aggregation queries
    suspend fun getExpensesByDay(startDate: LocalDateTime, endDate: LocalDateTime): List<Expense> =
        expenseDao.getExpensesByDay(startDate, endDate)

    suspend fun getTotalExpensesByDay(startDate: LocalDateTime, endDate: LocalDateTime): Double =
        expenseDao.getTotalExpensesByDay(startDate, endDate) ?: 0.0

    suspend fun getExpenseCountByDay(startDate: LocalDateTime, endDate: LocalDateTime): Int =
        expenseDao.getExpenseCountByDay(startDate, endDate)

    suspend fun getExpensesBySpecificDate(targetDate: LocalDateTime): List<Expense> =
        expenseDao.getExpensesBySpecificDate(targetDate)

    // Calendar view support
    fun getDaysWithExpenses(): Flow<List<String>> = expenseDao.getDaysWithExpenses()

    // Monthly statistics
    suspend fun getMonthlyTotal(startDate: LocalDateTime, endDate: LocalDateTime): Double =
        expenseDao.getMonthlyTotal(startDate, endDate) ?: 0.0

    suspend fun getMonthlyAverageAmount(startDate: LocalDateTime, endDate: LocalDateTime): Double =
        expenseDao.getMonthlyAverageAmount(startDate, endDate) ?: 0.0

    suspend fun getCategorySpendingTrend(startDate: LocalDateTime, endDate: LocalDateTime) =
        expenseDao.getCategorySpendingTrend(startDate, endDate)

    suspend fun getTopMerchants(startDate: LocalDateTime, endDate: LocalDateTime, limit: Int = 10): List<MerchantTotal> =
        expenseDao.getTopMerchants(startDate, endDate, limit)

    suspend fun getPaymentModeBreakdown(startDate: LocalDateTime, endDate: LocalDateTime): List<PaymentModeTotal> =
        expenseDao.getPaymentModeBreakdown(startDate, endDate)

    suspend fun getDailySpendingTrend(startDate: LocalDateTime, endDate: LocalDateTime): List<DailySpending> =
        expenseDao.getDailySpendingTrend(startDate, endDate)

    suspend fun getHighValueExpenses(threshold: Double, startDate: LocalDateTime, endDate: LocalDateTime): List<Expense> =
        expenseDao.getHighValueExpenses(threshold, startDate, endDate)
}
