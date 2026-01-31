package com.expense.tracker.utils

import com.expense.tracker.data.model.Category

object CategoryUtils {
    fun getCategoryColor(categoryName: String, categories: List<Category>): String {
        val category = categories.find { it.name == categoryName }
        return category?.color ?: "#FF607D8B"
    }

    fun isDefaultCategory(categoryName: String, categories: List<Category>): Boolean {
        val category = categories.find { it.name == categoryName }
        return category?.isDefault ?: false
    }
}
