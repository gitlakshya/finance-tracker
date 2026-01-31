package com.expense.tracker.data.database

import androidx.room.*
import com.expense.tracker.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE id IN (:ids)")
    suspend fun deleteExpensesByIds(ids: List<Long>)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<Expense>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    suspend fun getExpensesByCategory(category: String): List<Expense>

    @Query("SELECT * FROM expenses WHERE amount >= :minAmount AND amount <= :maxAmount ORDER BY date DESC")
    suspend fun getExpensesByAmountRange(minAmount: Double, maxAmount: Double): List<Expense>

    @Query("SELECT * FROM expenses WHERE paymentMode = :paymentMode ORDER BY date DESC")
    suspend fun getExpensesByPaymentMode(paymentMode: String): List<Expense>

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTotalExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Double?

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE date >= :startDate AND date <= :endDate GROUP BY category")
    suspend fun getCategoryBreakdown(startDate: LocalDateTime, endDate: LocalDateTime): List<CategoryTotal>

    @Query("DELETE FROM expenses WHERE smsId = :smsId")
    suspend fun deleteBySmsId(smsId: String)

    @Query("SELECT * FROM expenses WHERE smsId = :smsId LIMIT 1")
    suspend fun getExpenseBySmsId(smsId: String): Expense?

    @Query("SELECT COUNT(*) FROM expenses WHERE date >= :startDate AND date <= :endDate")
    suspend fun getExpenseCountByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Int

    // Daily aggregation queries
    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date < :endDate ORDER BY date DESC")
    suspend fun getExpensesByDay(startDate: LocalDateTime, endDate: LocalDateTime): List<Expense>

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startDate AND date < :endDate")
    suspend fun getTotalExpensesByDay(startDate: LocalDateTime, endDate: LocalDateTime): Double?

    @Query("SELECT COUNT(*) FROM expenses WHERE date >= :startDate AND date < :endDate")
    suspend fun getExpenseCountByDay(startDate: LocalDateTime, endDate: LocalDateTime): Int

    // Daily expenses for calendar view - shows if there are expenses on a date
    @Query("SELECT DISTINCT CAST(date AS TEXT) FROM expenses ORDER BY date DESC")
    fun getDaysWithExpenses(): Flow<List<String>>

    // Monthly statistics
    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startDate AND date <= :endDate")
    suspend fun getMonthlyTotal(startDate: LocalDateTime, endDate: LocalDateTime): Double?

    @Query("SELECT AVG(amount) FROM expenses WHERE date >= :startDate AND date <= :endDate")
    suspend fun getMonthlyAverageAmount(startDate: LocalDateTime, endDate: LocalDateTime): Double?

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE date >= :startDate AND date <= :endDate GROUP BY category ORDER BY total DESC")
    suspend fun getCategorySpendingTrend(startDate: LocalDateTime, endDate: LocalDateTime): List<CategoryTotal>

    // Top merchants query
    @Query("SELECT merchant, SUM(amount) as total FROM expenses WHERE date >= :startDate AND date <= :endDate AND merchant != '' GROUP BY merchant ORDER BY total DESC LIMIT :limit")
    suspend fun getTopMerchants(startDate: LocalDateTime, endDate: LocalDateTime, limit: Int): List<MerchantTotal>

    // Payment mode breakdown
    @Query("SELECT paymentMode, SUM(amount) as total FROM expenses WHERE date >= :startDate AND date <= :endDate GROUP BY paymentMode ORDER BY total DESC")
    suspend fun getPaymentModeBreakdown(startDate: LocalDateTime, endDate: LocalDateTime): List<PaymentModeTotal>

    // Expense trend - daily spending
    @Query("""
        SELECT CAST(date AS TEXT) as day, SUM(amount) as total, COUNT(*) as count 
        FROM expenses 
        WHERE date >= :startDate AND date <= :endDate 
        GROUP BY CAST(date AS TEXT) 
        ORDER BY date DESC
    """)
    suspend fun getDailySpendingTrend(startDate: LocalDateTime, endDate: LocalDateTime): List<DailySpending>

    // Get expenses for a specific date
    @Query("SELECT * FROM expenses WHERE DATE(date) = DATE(:targetDate) ORDER BY date DESC")
    suspend fun getExpensesBySpecificDate(targetDate: LocalDateTime): List<Expense>

    // High spending detection - expenses above certain threshold
    @Query("SELECT * FROM expenses WHERE amount > :threshold AND date >= :startDate AND date <= :endDate ORDER BY amount DESC")
    suspend fun getHighValueExpenses(threshold: Double, startDate: LocalDateTime, endDate: LocalDateTime): List<Expense>
}

data class CategoryTotal(
    val category: String,
    val total: Double
)

data class MerchantTotal(
    val merchant: String,
    val total: Double
)

data class PaymentModeTotal(
    val paymentMode: String,
    val total: Double
)

data class DailySpending(
    val day: String,
    val total: Double,
    val count: Int
)
