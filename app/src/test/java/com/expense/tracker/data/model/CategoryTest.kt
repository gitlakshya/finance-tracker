package com.expense.tracker.data.model

import org.junit.Assert.*
import org.junit.Test

class CategoryTest {

    @Test
    fun `category creation with all fields`() {
        val category = Category(
            id = 1,
            name = "Groceries",
            isDefault = true,
            color = "#FF5722"
        )

        assertEquals(1, category.id)
        assertEquals("Groceries", category.name)
        assertTrue(category.isDefault)
        assertEquals("#FF5722", category.color)
    }

    @Test
    fun `category with minimal fields`() {
        val category = Category(
            name = "Transport",
            color = "#2196F3"
        )

        assertEquals(0, category.id) // Default value
        assertEquals("Transport", category.name)
        assertFalse(category.isDefault) // Default value
        assertEquals("#2196F3", category.color)
    }

    @Test
    fun `category with default values`() {
        val category = Category(
            name = "Custom"
        )

        assertEquals(0, category.id)
        assertEquals("Custom", category.name)
        assertFalse(category.isDefault)
        assertEquals("#FF9C27B0", category.color) // Default purple color
    }

    @Test
    fun `default category has correct properties`() {
        val category = Category(
            name = "Food",
            isDefault = true
        )

        assertTrue(category.isDefault)
        assertEquals("#FF9C27B0", category.color)
    }

    @Test
    fun `category color validation`() {
        val validColor = "#FF5722"
        val category = Category(
            name = "Test",
            color = validColor
        )

        assertTrue(category.color.startsWith("#"))
        assertEquals(validColor, category.color)
    }

    @Test
    fun `category copy with changes`() {
        val original = Category(
            id = 1,
            name = "Original",
            isDefault = false,
            color = "#FF5722"
        )

        val modified = original.copy(
            name = "Modified",
            color = "#2196F3"
        )

        assertEquals(1, modified.id)
        assertEquals("Modified", modified.name)
        assertEquals("#2196F3", modified.color)
        assertFalse(modified.isDefault)
    }

    @Test
    fun `category equality check`() {
        val category1 = Category(
            id = 1,
            name = "Test",
            color = "#FF5722"
        )

        val category2 = Category(
            id = 1,
            name = "Test",
            color = "#FF5722"
        )

        assertEquals(category1, category2)
    }

    @Test
    fun `different categories are not equal`() {
        val category1 = Category(
            id = 1,
            name = "Test1"
        )

        val category2 = Category(
            id = 2,
            name = "Test2"
        )

        assertNotEquals(category1, category2)
    }

    @Test
    fun `category with special characters in name`() {
        val category = Category(
            name = "Coffee & Snacks",
            color = "#795548"
        )

        assertEquals("Coffee & Snacks", category.name)
        assertTrue(category.name.contains("&"))
    }

    @Test
    fun `category id auto generation`() {
        val category = Category(
            name = "Auto ID Test"
        )

        assertEquals(0, category.id) // Should be 0 before insert
    }
}
