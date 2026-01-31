package com.expense.tracker.utils

import com.expense.tracker.data.model.Category
import org.junit.Assert.*
import org.junit.Test

class AICategorySuggestionTest {

    private val testCategories = listOf(
        Category(name = "Groceries", color = "#FF5722"),
        Category(name = "Dining Out", color = "#FF9800"),
        Category(name = "Fuel", color = "#795548"),
        Category(name = "Public Transport", color = "#2196F3"),
        Category(name = "Electricity", color = "#FFC107"),
        Category(name = "Internet", color = "#9C27B0"),
        Category(name = "Mobile", color = "#00BCD4"),
        Category(name = "Entertainment", color = "#E91E63"),
        Category(name = "Others", color = "#9E9E9E")
    )

    @Test
    fun `suggest category for grocery merchant`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "Monthly groceries",
            merchant = "DMart Supermarket",
            availableCategories = testCategories
        )

        assertEquals("Groceries", result)
    }

    @Test
    fun `suggest category for restaurant`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "Dinner",
            merchant = "Pizza Hut Restaurant",
            availableCategories = testCategories
        )

        assertEquals("Dining Out", result)
    }

    @Test
    fun `suggest category for fuel purchase`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "Petrol",
            merchant = "Indian Oil Pump",
            availableCategories = testCategories
        )

        assertEquals("Fuel", result)
    }

    @Test
    fun `suggest category for utility bill`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "Monthly bill",
            merchant = "Electricity Board",
            availableCategories = testCategories
        )

        assertEquals("Electricity", result)
    }

    @Test
    fun `suggest category for transport`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "Daily commute",
            merchant = "Metro Card Recharge",
            availableCategories = testCategories
        )

        assertEquals("Public Transport", result)
    }

    @Test
    fun `suggest category returns valid string`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "Food shopping",
            merchant = "Grocery Store",
            availableCategories = testCategories
        )

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `suggest category with no match returns Others`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "Random expense",
            merchant = "Unknown Merchant",
            availableCategories = testCategories
        )

        assertEquals("Others", result)
    }

    @Test
    fun `suggest category is case insensitive`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "GROCERIES",
            merchant = "DMART",
            availableCategories = testCategories
        )

        assertEquals("Groceries", result)
    }

    @Test
    fun `suggest category with partial keyword match`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "Internet service",
            merchant = "Broadband Provider",
            availableCategories = testCategories
        )

        assertEquals("Internet", result)
    }

    @Test
    fun `suggest category with empty inputs returns Others`() {
        val result = AICategorySuggestion.suggestCategory(
            description = "",
            merchant = "",
            availableCategories = testCategories
        )

        assertEquals("Others", result)
    }
}
