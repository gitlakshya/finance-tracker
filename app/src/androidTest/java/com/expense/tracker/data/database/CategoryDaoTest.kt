package com.expense.tracker.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.expense.tracker.data.model.Category
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith

/**
 * Instrumented tests for CategoryDao
 */
@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var categoryDao: CategoryDao

    private val testCategory = Category(
        id = 0L,
        name = "Test Category",
        icon = "🧪",
        color = "#FF5722",
        isDefault = false
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        categoryDao = database.categoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCategoryAndGetById() = runTest {
        val id = categoryDao.insertCategory(testCategory)
        val category = categoryDao.getCategoryById(id)
        
        assertNotNull(category)
        assertEquals(testCategory.name, category?.name)
        assertEquals(testCategory.icon, category?.icon)
        assertEquals(testCategory.color, category?.color)
    }

    @Test
    fun insertMultipleCategoriesAndGetAll() = runTest {
        val category1 = testCategory.copy(name = "Category 1")
        val category2 = testCategory.copy(name = "Category 2")
        
        categoryDao.insertCategory(category1)
        categoryDao.insertCategory(category2)
        
        val categories = categoryDao.getAllCategories().first()
        
        assertTrue(categories.size >= 2)
    }

    @Test
    fun updateCategory() = runTest {
        val id = categoryDao.insertCategory(testCategory)
        val category = categoryDao.getCategoryById(id)!!
        
        val updatedCategory = category.copy(name = "Updated Category", color = "#00FF00")
        categoryDao.updateCategory(updatedCategory)
        
        val result = categoryDao.getCategoryById(id)
        
        assertEquals("Updated Category", result?.name)
        assertEquals("#00FF00", result?.color)
    }

    @Test
    fun deleteCategory() = runTest {
        val id = categoryDao.insertCategory(testCategory)
        val category = categoryDao.getCategoryById(id)!!
        
        categoryDao.deleteCategory(category)
        
        val result = categoryDao.getCategoryById(id)
        assertNull(result)
    }

    @Test
    fun getCategoryByName() = runTest {
        categoryDao.insertCategory(testCategory.copy(name = "Food"))
        
        val category = categoryDao.getCategoryByName("Food")
        
        assertNotNull(category)
        assertEquals("Food", category?.name)
    }

    @Test
    fun getCategoryByNameNotFound() = runTest {
        val category = categoryDao.getCategoryByName("NonExistent")
        
        assertNull(category)
    }

    @Test
    fun getDefaultCategories() = runTest {
        val defaultCategory1 = testCategory.copy(name = "Default1", isDefault = true)
        val defaultCategory2 = testCategory.copy(name = "Default2", isDefault = true)
        val customCategory = testCategory.copy(name = "Custom", isDefault = false)
        
        categoryDao.insertCategory(defaultCategory1)
        categoryDao.insertCategory(defaultCategory2)
        categoryDao.insertCategory(customCategory)
        
        val defaultCategories = categoryDao.getDefaultCategories()
        
        assertEquals(2, defaultCategories.size)
        assertTrue(defaultCategories.all { it.isDefault })
    }

    @Test
    fun deleteCustomCategory() = runTest {
        categoryDao.insertCategory(testCategory.copy(name = "Custom", isDefault = false))
        
        categoryDao.deleteCustomCategory("Custom")
        
        val category = categoryDao.getCategoryByName("Custom")
        assertNull(category)
    }

    @Test
    fun deleteCustomCategoryDoesNotDeleteDefault() = runTest {
        categoryDao.insertCategory(testCategory.copy(name = "Default", isDefault = true))
        
        categoryDao.deleteCustomCategory("Default")
        
        val category = categoryDao.getCategoryByName("Default")
        assertNotNull(category) // Should still exist because it's default
    }

    @Test
    fun getAllCategoriesSortedByName() = runTest {
        categoryDao.insertCategory(testCategory.copy(name = "Zebra"))
        categoryDao.insertCategory(testCategory.copy(name = "Apple"))
        categoryDao.insertCategory(testCategory.copy(name = "Mango"))
        
        val categories = categoryDao.getAllCategories().first()
        
        // Should be sorted alphabetically
        for (i in 0 until categories.size - 1) {
            assertTrue(categories[i].name <= categories[i + 1].name)
        }
    }

    @Test
    fun replaceCategoryOnConflict() = runTest {
        val category1 = testCategory.copy(name = "Unique", color = "#FF0000")
        val id = categoryDao.insertCategory(category1)
        
        val category2 = category1.copy(id = id, color = "#00FF00")
        categoryDao.insertCategory(category2)
        
        val result = categoryDao.getCategoryById(id)
        
        assertEquals("#00FF00", result?.color)
    }
}
