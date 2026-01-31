package com.expense.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false,
    val color: String = "#FF9C27B0" // Default purple color
)

// Predefined categories - 35+ categories for comprehensive expense tracking
val DEFAULT_CATEGORIES = listOf(
    // Food & Dining (3)
    Category(name = "Groceries", isDefault = true, color = "#FFFF5722"),
    Category(name = "Dining Out", isDefault = true, color = "#FFFF9800"),
    Category(name = "Coffee & Snacks", isDefault = true, color = "#FFC2185B"),
    
    // Transport (4)
    Category(name = "Fuel", isDefault = true, color = "#FF2196F3"),
    Category(name = "Public Transport", isDefault = true, color = "#FF1976D2"),
    Category(name = "Ride Sharing", isDefault = true, color = "#FF0D47A1"),
    Category(name = "Vehicle Maintenance", isDefault = true, color = "#FF0288D1"),
    
    // Housing & Utilities (5)
    Category(name = "Rent", isDefault = true, color = "#FF4CAF50"),
    Category(name = "Electricity", isDefault = true, color = "#FFFFC107"),
    Category(name = "Water", isDefault = true, color = "#FF00BCD4"),
    Category(name = "Internet", isDefault = true, color = "#FF3F51B5"),
    Category(name = "Home Maintenance", isDefault = true, color = "#FF558B2F"),
    
    // Shopping (5)
    Category(name = "Clothing", isDefault = true, color = "#FF9C27B0"),
    Category(name = "Electronics", isDefault = true, color = "#FF7B1FA2"),
    Category(name = "Home & Furniture", isDefault = true, color = "#FF5E35B1"),
    Category(name = "Books & Media", isDefault = true, color = "#FF512DA8"),
    Category(name = "Shopping", isDefault = true, color = "#FFA1006B"),
    
    // Entertainment (4)
    Category(name = "Movies & Streaming", isDefault = true, color = "#FF673AB7"),
    Category(name = "Gaming", isDefault = true, color = "#FF6A1B9A"),
    Category(name = "Sports & Hobbies", isDefault = true, color = "#FF8E24AA"),
    Category(name = "Subscriptions", isDefault = true, color = "#FFAD1457"),
    
    // Health & Medical (4)
    Category(name = "Medical", isDefault = true, color = "#FFF44336"),
    Category(name = "Pharmacy", isDefault = true, color = "#FFE53935"),
    Category(name = "Gym & Fitness", isDefault = true, color = "#FFCA1010"),
    Category(name = "Health Insurance", isDefault = true, color = "#FFB71C1C"),
    
    // Financial (4)
    Category(name = "Loan Payment", isDefault = true, color = "#FF880E4F"),
    Category(name = "Insurance", isDefault = true, color = "#FF6A0572"),
    Category(name = "Investment", isDefault = true, color = "#FF5A0A78"),
    Category(name = "Bank Charges", isDefault = true, color = "#FF3E1047"),
    
    // Education (3)
    Category(name = "Tuition & Courses", isDefault = true, color = "#FF1565C0"),
    Category(name = "School Supplies", isDefault = true, color = "#FF1976D2"),
    Category(name = "Student Loans", isDefault = true, color = "#FF1E88E5"),
    
    // Pets & Animals (2)
    Category(name = "Pet Food & Care", isDefault = true, color = "#FF6D4C41"),
    Category(name = "Veterinary", isDefault = true, color = "#FF795548"),
    
    // Travel & Vacation (2)
    Category(name = "Accommodation", isDefault = true, color = "#FF4DB6AC"),
    Category(name = "Travel & Flights", isDefault = true, color = "#FF26A69A"),
    
    // Miscellaneous (2)
    Category(name = "Gifts & Charity", isDefault = true, color = "#FFEC407A"),
    Category(name = "Others", isDefault = true, color = "#FF607D8B")
)
