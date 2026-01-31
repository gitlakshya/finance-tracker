package com.expense.tracker.data.model

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class ExpenseTest {

    @Test
    fun `expense creation with all fields`() {
        val now = LocalDateTime.now()
        val expense = Expense(
            id = 1,
            amount = 500.0,
            category = "Groceries",
            description = "Weekly shopping",
            paymentMode = "Card",
            date = now,
            notes = "Test notes",
            source = "Manual",
            smsId = "SMS123",
            merchant = "DMart",
            bank = "HDFC",
            createdAt = now,
            updatedAt = now
        )

        assertEquals(1, expense.id)
        assertEquals(500.0, expense.amount, 0.01)
        assertEquals("Groceries", expense.category)
        assertEquals("Weekly shopping", expense.description)
        assertEquals("Card", expense.paymentMode)
        assertEquals(now, expense.date)
        assertEquals("Test notes", expense.notes)
        assertEquals("Manual", expense.source)
        assertEquals("SMS123", expense.smsId)
        assertEquals("DMart", expense.merchant)
        assertEquals("HDFC", expense.bank)
    }

    @Test
    fun `expense with minimal fields`() {
        val now = LocalDateTime.now()
        val expense = Expense(
            amount = 100.0,
            category = "Food",
            description = "Lunch",
            paymentMode = "Cash",
            date = now,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(0, expense.id)
        assertEquals(100.0, expense.amount, 0.01)
        assertEquals("Food", expense.category)
    }

    @Test
    fun `expense copy with updated amount`() {
        val now = LocalDateTime.now()
        val original = Expense(
            id = 1,
            amount = 100.0,
            category = "Food",
            description = "Lunch",
            paymentMode = "Cash",
            date = now,
            createdAt = now,
            updatedAt = now
        )

        val updated = original.copy(amount = 150.0, updatedAt = now.plusMinutes(1))

        assertEquals(150.0, updated.amount, 0.01)
        assertEquals(1, updated.id)
        assertEquals("Food", updated.category)
        assertTrue(updated.updatedAt.isAfter(original.updatedAt))
    }

    @Test
    fun `expense equality check`() {
        val now = LocalDateTime.now()
        val expense1 = Expense(
            id = 1,
            amount = 100.0,
            category = "Food",
            description = "Test",
            paymentMode = "Cash",
            date = now,
            createdAt = now,
            updatedAt = now
        )

        val expense2 = expense1.copy()

        assertEquals(expense1, expense2)
    }

    @Test
    fun `expense with different amounts are not equal`() {
        val now = LocalDateTime.now()
        val expense1 = Expense(
            id = 1,
            amount = 100.0,
            category = "Food",
            description = "Test",
            paymentMode = "Cash",
            date = now,
            createdAt = now,
            updatedAt = now
        )

        val expense2 = expense1.copy(amount = 200.0)

        assertNotEquals(expense1, expense2)
    }

    @Test
    fun `expense amount is positive`() {
        val now = LocalDateTime.now()
        val expense = Expense(
            amount = 500.0,
            category = "Food",
            description = "Test",
            paymentMode = "Cash",
            date = now,
            createdAt = now,
            updatedAt = now
        )

        assertTrue(expense.amount > 0)
    }

    @Test
    fun `expense with empty optional fields`() {
        val now = LocalDateTime.now()
        val expense = Expense(
            amount = 100.0,
            category = "Test",
            description = "Test expense",
            paymentMode = "Cash",
            date = now,
            notes = "",
            source = "",
            smsId = "",
            merchant = "",
            bank = "",
            createdAt = now,
            updatedAt = now
        )

        assertTrue(expense.notes.isEmpty())
        assertTrue(expense.merchant.isEmpty())
    }

    @Test
    fun `expense payment modes validation`() {
        val validModes = listOf("Cash", "Card", "UPI")
        val now = LocalDateTime.now()

        validModes.forEach { mode ->
            val expense = Expense(
                amount = 100.0,
                category = "Test",
                description = "Test",
                paymentMode = mode,
                date = now,
                createdAt = now,
                updatedAt = now
            )
            assertTrue(validModes.contains(expense.paymentMode))
        }
    }

    @Test
    fun `expense source types`() {
        val sources = listOf("SMS", "Manual", "Import")
        val now = LocalDateTime.now()

        sources.forEach { source ->
            val expense = Expense(
                amount = 100.0,
                category = "Test",
                description = "Test",
                paymentMode = "Cash",
                date = now,
                source = source,
                createdAt = now,
                updatedAt = now
            )
            assertEquals(source, expense.source)
        }
    }
}
