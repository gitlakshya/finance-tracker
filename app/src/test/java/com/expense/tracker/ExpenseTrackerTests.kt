package com.expense.tracker.utils

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

/**
 * Unit tests for SMS parsing and expense categorization
 */
class SMSTransactionParserTest {

    @Test
    fun testParseHDFCBankTransaction() {
        val sms = "Dear customer, Rs.500 debited from your account on 20/01/2024 at 15:30 for UPI transaction at Amazon. Balance: 5000"
        val parsed = SMSTransactionParser.parseTransaction(sms, "HDFC_BANK")
        
        assertNotNull(parsed)
        assertEquals(500.0, parsed?.amount)
        assertEquals("Shopping", parsed?.category)
        assertEquals("UPI", parsed?.paymentMode)
    }

    @Test
    fun testParseZomatoTransaction() {
        val sms = "You spent ₹450 at Zomato Food on 20/01/2024 15:30. Remaining balance: ₹10000"
        val parsed = SMSTransactionParser.parseTransaction(sms, "ZOMATO")
        
        assertNotNull(parsed)
        assertEquals(450.0, parsed?.amount)
        assertEquals("Food", parsed?.category)
    }

    @Test
    fun testParseUberTransaction() {
        val sms = "Your Uber trip cost ₹350. Transaction ID: 123456. Date: 20 Jan 2024 16:45"
        val parsed = SMSTransactionParser.parseTransaction(sms, "UBER")
        
        assertNotNull(parsed)
        assertEquals(350.0, parsed?.amount)
        assertEquals("Transport", parsed?.category)
    }

    @Test
    fun testIgnoreCreditMessages() {
        val sms = "Salary of Rs.50000 credited to your account on 01/01/2024"
        val parsed = SMSTransactionParser.parseTransaction(sms, "EMPLOYER")
        
        assertNull(parsed)  // Should be ignored
    }

    @Test
    fun testIgnoreOTPMessages() {
        val sms = "Your OTP is 123456. Valid for 10 minutes."
        val parsed = SMSTransactionParser.parseTransaction(sms, "BANK")
        
        assertNull(parsed)  // Should be ignored
    }

    @Test
    fun testIgnorePromotionalMessages() {
        val sms = "Special offer: Get 50% discount on your next purchase. Use code SPECIAL50"
        val parsed = SMSTransactionParser.parseTransaction(sms, "MERCHANT")
        
        assertNull(parsed)  // Should be ignored
    }

    @Test
    fun testParseAmountVariations() {
        val amounts = listOf(
            "Rs.100",
            "₹200.50",
            "Amount: 300",
            "INR 400",
            "Rs 500.75"
        )
        
        amounts.forEach { amount ->
            val sms = "$amount debited from your account"
            val parsed = SMSTransactionParser.parseTransaction(sms, "BANK")
            assertNotNull("Should parse $amount", parsed)
        }
    }

    @Test
    fun testCategoryAutodetection() {
        val testCases = mapOf(
            "Debited Rs.500 at McDonald's" to "Food",
            "Payment of Rs.1000 to OLA Cabs" to "Transport",
            "Amazon purchase Rs.2000" to "Shopping",
            "Electricity bill Rs.1500" to "Utilities",
            "Hospital bill Rs.5000" to "Medical"
        )
        
        testCases.forEach { (sms, expectedCategory) ->
            val parsed = SMSTransactionParser.parseTransaction(sms, "BANK")
            assertEquals("For SMS: $sms", expectedCategory, parsed?.category)
        }
    }
}

/**
 * Unit tests for filtering logic
 */
class FilterUtilsTest {

    @Test
    fun testFilterByDateRange() {
        val today = LocalDateTime.now()
        val tomorrow = today.plusDays(1)
        val nextWeek = today.plusDays(7)

        val expenses = listOf(
            Expense(amount = 100.0, category = "Food", description = "Lunch", 
                   paymentMode = "Cash", date = today),
            Expense(amount = 200.0, category = "Transport", description = "Uber", 
                   paymentMode = "UPI", date = tomorrow),
            Expense(amount = 300.0, category = "Shopping", description = "Amazon", 
                   paymentMode = "Card", date = nextWeek)
        )

        val filtered = FilterUtils.filterExpensesByDateRange(
            expenses,
            today,
            tomorrow
        )
        
        assertEquals(2, filtered.size)
    }

    @Test
    fun testFilterByCategory() {
        val expenses = listOf(
            Expense(amount = 100.0, category = "Food", description = "Lunch", 
                   paymentMode = "Cash", date = LocalDateTime.now()),
            Expense(amount = 200.0, category = "Food", description = "Dinner", 
                   paymentMode = "UPI", date = LocalDateTime.now()),
            Expense(amount = 300.0, category = "Transport", description = "Uber", 
                   paymentMode = "Card", date = LocalDateTime.now())
        )

        val filtered = FilterUtils.filterExpensesByCategory(expenses, listOf("Food"))
        
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.category == "Food" })
    }

    @Test
    fun testFilterByAmountRange() {
        val expenses = listOf(
            Expense(amount = 100.0, category = "Food", description = "Lunch", 
                   paymentMode = "Cash", date = LocalDateTime.now()),
            Expense(amount = 500.0, category = "Transport", description = "Uber", 
                   paymentMode = "UPI", date = LocalDateTime.now()),
            Expense(amount = 1000.0, category = "Shopping", description = "Amazon", 
                   paymentMode = "Card", date = LocalDateTime.now())
        )

        val filtered = FilterUtils.filterExpensesByAmountRange(expenses, 200.0, 800.0)
        
        assertEquals(1, filtered.size)
        assertEquals(500.0, filtered[0].amount)
    }

    @Test
    fun testSearchExpenses() {
        val expenses = listOf(
            Expense(amount = 100.0, category = "Food", description = "Pizza Hut", 
                   paymentMode = "Cash", date = LocalDateTime.now()),
            Expense(amount = 200.0, category = "Transport", description = "Uber", 
                   paymentMode = "UPI", date = LocalDateTime.now()),
            Expense(amount = 300.0, category = "Shopping", description = "Amazon Books", 
                   paymentMode = "Card", date = LocalDateTime.now())
        )

        val filtered = FilterUtils.searchExpenses(expenses, "amazon")
        
        assertEquals(1, filtered.size)
        assertTrue(filtered[0].description.contains("Amazon", ignoreCase = true))
    }
}

/**
 * Unit tests for report generation
 */
class ReportGeneratorTest {

    @Test
    fun testMonthlyReportGeneration() {
        val today = LocalDateTime.now()
        val expenses = listOf(
            Expense(amount = 100.0, category = "Food", description = "Lunch", 
                   paymentMode = "Cash", date = today),
            Expense(amount = 200.0, category = "Transport", description = "Uber", 
                   paymentMode = "UPI", date = today),
            Expense(amount = 150.0, category = "Food", description = "Dinner", 
                   paymentMode = "Card", date = today.plusDays(1))
        )

        val report = ReportGenerator.generateMonthlyReport(
            expenses,
            YearMonth.from(today)
        )

        assertEquals(450.0, report.totalExpenses, 0.01)
        assertEquals(2, report.categoryBreakdown.size)
        assertEquals(250.0, report.categoryBreakdown["Food"], 0.01)
        assertEquals(200.0, report.categoryBreakdown["Transport"], 0.01)
    }

    @Test
    fun testAverageDailyExpense() {
        val today = LocalDateTime.now()
        val expenses = listOf(
            Expense(amount = 100.0, category = "Food", description = "Lunch", 
                   paymentMode = "Cash", date = today),
            Expense(amount = 200.0, category = "Transport", description = "Uber", 
                   paymentMode = "UPI", date = today),
            Expense(amount = 150.0, category = "Food", description = "Dinner", 
                   paymentMode = "Card", date = today.plusDays(1))
        )

        val report = ReportGenerator.generateMonthlyReport(
            expenses,
            YearMonth.from(today)
        )

        val avg = ReportGenerator.getAverageDailyExpense(report)
        assertEquals(225.0, avg, 0.01)  // (100+200+150) / 2 days
    }

    @Test
    fun testTopCategories() {
        val today = LocalDateTime.now()
        val expenses = listOf(
            Expense(amount = 500.0, category = "Food", description = "Restaurant", 
                   paymentMode = "Card", date = today),
            Expense(amount = 100.0, category = "Food", description = "Snacks", 
                   paymentMode = "Cash", date = today),
            Expense(amount = 1000.0, category = "Rent", description = "Monthly Rent", 
                   paymentMode = "Transfer", date = today),
            Expense(amount = 200.0, category = "Transport", description = "Uber", 
                   paymentMode = "UPI", date = today)
        )

        val report = ReportGenerator.generateMonthlyReport(
            expenses,
            YearMonth.from(today)
        )

        val topCategories = ReportGenerator.getTopCategories(report, 2)
        
        assertEquals(2, topCategories.size)
        assertEquals("Rent", topCategories[0].first)
        assertEquals(1000.0, topCategories[0].second, 0.01)
    }
}

/**
 * Integration tests (would require Android context)
 */
class ExpenseRepositoryTest {
    // Tests would require actual Room database
    // Use robolectric or other instrumented testing frameworks
    
    // Example:
    // @Test
    // fun testInsertAndRetrieveExpense() {
    //     val expense = Expense(...)
    //     repository.insertExpense(expense)
    //     val retrieved = repository.getExpenseById(expense.id)
    //     assertEquals(expense.amount, retrieved?.amount)
    // }
}
