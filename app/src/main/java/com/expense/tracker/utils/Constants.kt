package com.expense.tracker.utils

/**
 * Application-wide constants
 * Centralized location for all hardcoded values
 */
object Constants {
    
    // Payment Modes
    object PaymentModes {
        const val CASH = "Cash"
        const val UPI = "UPI"
        const val CARD = "Card"
        
        val ALL = listOf(CASH, UPI, CARD)
    }
    
    // Expense Sources
    object ExpenseSources {
        const val SMS = "SMS"
        const val MANUAL = "Manual"
        const val IMPORT = "Import"
        
        val ALL = listOf(SMS, MANUAL, IMPORT)
    }
    
    // Default Categories (fallback when database is empty)
    object DefaultCategories {
        const val GROCERIES = "Groceries"
        const val OTHERS = "Others"
        
        val FALLBACK = listOf(GROCERIES, OTHERS)
    }
    
    // Database
    object Database {
        const val NAME = "expense_tracker_database"
        const val VERSION = 1
    }
    
    // Date Format Patterns
    object DateFormats {
        const val DISPLAY_DATE = "dd MMM yyyy"
        const val DISPLAY_TIME = "hh:mm a"
        const val DISPLAY_MONTH_YEAR = "MMMM yyyy"
        const val FULL_DATE_TIME = "dd MMM yyyy, hh:mm a"
    }
    
    // SMS Parser - Category Keywords
    object CategoryKeywords {
        val FOOD = listOf(
            "restaurant", "food", "cafe", "coffee", "pizza", "burger", 
            "hotel", "food delivery", "swiggy", "zomato", "uber eats", "dominos"
        )
        
        val TRANSPORT = listOf(
            "uber", "ola", "taxi", "petrol", "fuel", "gas station", 
            "parking", "ticket", "train", "flight", "bus"
        )
        
        val RENT = listOf(
            "rent", "landlord", "property", "mortgage"
        )
        
        val UTILITIES = listOf(
            "electricity", "water", "gas", "internet", "mobile", 
            "phone", "isp", "postpaid"
        )
        
        val SHOPPING = listOf(
            "amazon", "flipkart", "mall", "store", "shop", "retail", 
            "grocery", "supermarket", "walmart", "target"
        )
        
        val ENTERTAINMENT = listOf(
            "movie", "theater", "cinema", "game", "spotify", 
            "netflix", "youtube", "entertainment"
        )
        
        val MEDICAL = listOf(
            "hospital", "doctor", "medical", "pharmacy", "medicine", 
            "clinic", "health"
        )
        
        fun getCategoryKeywordsMap(): Map<String, List<String>> = mapOf(
            "Food" to FOOD,
            "Transport" to TRANSPORT,
            "Rent" to RENT,
            "Utilities" to UTILITIES,
            "Shopping" to SHOPPING,
            "Entertainment" to ENTERTAINMENT,
            "Medical" to MEDICAL
        )
    }
    
    // App Configuration
    object App {
        const val MIN_SDK = 29
        const val TARGET_SDK = 34
        const val COMPILE_SDK = 34
    }
    
    // UI Configuration
    object UI {
        const val TOP_MERCHANTS_LIMIT = 5
        const val EXPENSE_CARD_ELEVATION_DP = 1
        const val SUMMARY_CARD_ELEVATION_DP = 2
        const val BUTTON_HEIGHT_DP = 44
        const val ICON_BUTTON_SIZE_DP = 36
        const val ICON_SIZE_DP = 20
        const val DEFAULT_PADDING_DP = 12
    }
    
    // Colors (default category color)
    object Colors {
        const val DEFAULT_CATEGORY_COLOR = "#FF9C27B0"
    }
    
    // Permissions
    object Permissions {
        val SMS_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS
            )
        }
    }
}
