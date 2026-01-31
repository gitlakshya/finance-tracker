package com.expense.tracker.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.expense.tracker.data.model.Expense
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.time.LocalDateTime

/**
 * Instrumented tests for ExpenseDao
 */
@RunWith(AndroidJUnit4::class)
class ExpenseDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var expenseDao: ExpenseDao

    private val testExpense = Expense(
        id = 0L,
        amount = 500.0,
        category = "Food",
        description = "Lunch",
        paymentMode = "Card",
        date = LocalDateTime.now(),
        notes = "Test note",
        source = "Manual",
        smsId = "",
        merchant = "Restaurant",
        bank = "",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        expenseDao = database.expenseDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertExpenseAndGetById() = runTest {
        val id = expenseDao.insertExpense(testExpense)
        val expense = expenseDao.getExpenseById(id)
        
        assertNotNull(expense)
        assertEquals(testExpense.amount, expense?.amount)
        assertEquals(testExpense.category, expense?.category)
        assertEquals(testExpense.description, expense?.description)
    }

    @Test
    fun insertMultipleExpensesAndGetAll() = runTest {
        val expense1 = testExpense.copy(description = "Expense 1")
        val expense2 = testExpense.copy(description = "Expense 2")
        
        expenseDao.insertExpense(expense1)
        expenseDao.insertExpense(expense2)
        
        val expenses = expenseDao.getAllExpenses().first()
        
        assertTrue(expenses.size >= 2)
    }

    @Test
    fun updateExpense() = runTest {
        val id = expenseDao.insertExpense(testExpense)
        val expense = expenseDao.getExpenseById(id)!!
        
        val updatedExpense = expense.copy(amount = 1000.0, description = "Updated")
        expenseDao.updateExpense(updatedExpense)
        
        val result = expenseDao.getExpenseById(id)
        
        assertEquals(1000.0, result?.amount)
        assertEquals("Updated", result?.description)
    }

    @Test
    fun deleteExpense() = runTest {
        val id = expenseDao.insertExpense(testExpense)
        val expense = expenseDao.getExpenseById(id)!!
        
        expenseDao.deleteExpense(expense)
        
        val result = expenseDao.getExpenseById(id)
        assertNull(result)
    }

    @Test
    fun getExpensesByDateRange() = runTest {
        val now = LocalDateTime.now()
        val expense1 = testExpense.copy(date = now.minusDays(5))
        val expense2 = testExpense.copy(date = now.minusDays(2))
        val expense3 = testExpense.copy(date = now.minusDays(10))
        
        expenseDao.insertExpense(expense1)
        expenseDao.insertExpense(expense2)
        expenseDao.insertExpense(expense3)
        
        val startDate = now.minusDays(7)
        val endDate = now
        val expenses = expenseDao.getExpensesByDateRange(startDate, endDate)
        
        assertEquals(2, expenses.size)
    }

    @Test
    fun getExpensesByCategory() = runTest {
        val expense1 = testExpense.copy(category = "Food")
        val expense2 = testExpense.copy(category = "Food")
        val expense3 = testExpense.copy(category = "Transport")
        
        expenseDao.insertExpense(expense1)
        expenseDao.insertExpense(expense2)
        expenseDao.insertExpense(expense3)
        
        val foodExpenses = expenseDao.getExpensesByCategory("Food")
        
        assertEquals(2, foodExpenses.size)
        assertTrue(foodExpenses.all { it.category == "Food" })
    }

    @Test
    fun getExpensesByPaymentMode() = runTest {
        val expense1 = testExpense.copy(paymentMode = "Card")
        val expense2 = testExpense.copy(paymentMode = "Card")
        val expense3 = testExpense.copy(paymentMode = "UPI")
        
        expenseDao.insertExpense(expense1)
        expenseDao.insertExpense(expense2)
        expenseDao.insertExpense(expense3)
        
        val cardExpenses = expenseDao.getExpensesByPaymentMode("Card")
        
        assertEquals(2, cardExpenses.size)
        assertTrue(cardExpenses.all { it.paymentMode == "Card" })
    }

    @Test
    fun getTotalExpensesByDateRange() = runTest {
        val now = LocalDateTime.now()
        val expense1 = testExpense.copy(amount = 100.0, date = now.minusDays(1))
        val expense2 = testExpense.copy(amount = 200.0, date = now.minusDays(2))
        val expense3 = testExpense.copy(amount = 300.0, date = now.minusDays(10))
        
        expenseDao.insertExpense(expense1)
        expenseDao.insertExpense(expense2)
        expenseDao.insertExpense(expense3)
        
        val startDate = now.minusDays(7)
        val endDate = now
        val total = expenseDao.getTotalExpensesByDateRange(startDate, endDate)
        
        assertEquals(300.0, total ?: 0.0, 0.01)
    }

    @Test
    fun getCategoryBreakdown() = runTest {
        val now = LocalDateTime.now()
        val expense1 = testExpense.copy(category = "Food", amount = 100.0, date = now.minusDays(1))
        val expense2 = testExpense.copy(category = "Food", amount = 200.0, date = now.minusDays(2))
        val expense3 = testExpense.copy(category = "Transport", amount = 150.0, date = now.minusDays(3))
        
        expenseDao.insertExpense(expense1)
        expenseDao.insertExpense(expense2)
        expenseDao.insertExpense(expense3)
        
        val startDate = now.minusDays(7)
        val endDate = now
        val breakdown = expenseDao.getCategoryBreakdown(startDate, endDate)
        
        assertTrue(breakdown.isNotEmpty())
        val foodTotal = breakdown.find { it.category == "Food" }?.total
        assertEquals(300.0, foodTotal ?: 0.0, 0.01)
    }

    @Test
    fun getExpensesByAmountRange() = runTest {
        val expense1 = testExpense.copy(amount = 100.0)
        val expense2 = testExpense.copy(amount = 500.0)
        val expense3 = testExpense.copy(amount = 1000.0)
        
        expenseDao.insertExpense(expense1)
        expenseDao.insertExpense(expense2)
        expenseDao.insertExpense(expense3)
        
        val expenses = expenseDao.getExpensesByAmountRange(200.0, 800.0)
        
        assertEquals(1, expenses.size)
        assertEquals(500.0, expenses[0].amount, 0.01)
    }

    @Test
    fun getExpenseBySmsId() = runTest {
        val expense = testExpense.copy(smsId = "SMS123")
        expenseDao.insertExpense(expense)
        
        val result = expenseDao.getExpenseBySmsId("SMS123")
        
        assertNotNull(result)
        assertEquals("SMS123", result?.smsId)
    }

    @Test
    fun deleteBySmsId() = runTest {
        val expense = testExpense.copy(smsId = "SMS456")
        expenseDao.insertExpense(expense)
        
        expenseDao.deleteBySmsId("SMS456")
        
        val result = expenseDao.getExpenseBySmsId("SMS456")
        assertNull(result)
    }

    @Test
    fun deleteExpensesByIds() = runTest {
        val id1 = expenseDao.insertExpense(testExpense.copy(description = "Expense 1"))
        val id2 = expenseDao.insertExpense(testExpense.copy(description = "Expense 2"))
        val id3 = expenseDao.insertExpense(testExpense.copy(description = "Expense 3"))
        
        expenseDao.deleteExpensesByIds(listOf(id1, id2))
        
        assertNull(expenseDao.getExpenseById(id1))
        assertNull(expenseDao.getExpenseById(id2))
        assertNotNull(expenseDao.getExpenseById(id3))
    }
}
