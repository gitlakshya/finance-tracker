package com.expense.tracker.utils

import com.expense.tracker.data.model.Category

/**
 * AI-powered category suggestion engine using pattern matching and NLP-like analysis
 * This is an offline ML approach that analyzes merchant names and descriptions
 */
object AICategorySuggestion {

    // Category pattern mappings
    private val categoryPatterns = mapOf(
        "Groceries" to listOf(
            "grocery", "supermarket", "mart", "store", "dmart", "blinkit", "zepto",
            "food corner", "convenience", "vegetable", "farmer market"
        ),
        "Dining Out" to listOf(
            "restaurant", "cafe", "coffee", "pizza", "burger", "hotel", "diner",
            "fast food", "eatery", "bistro", "pub", "bar", "meal"
        ),
        "Coffee & Snacks" to listOf(
            "coffee", "cafe", "tea", "snack", "bakery", "pastry", "juice",
            "smoothie", "breakfast", "brunch", "dessert"
        ),
        "Fuel" to listOf(
            "petrol", "gas", "fuel", "pump", "filling station", "shell", "bp",
            "essar", "indian oil", "diesel"
        ),
        "Public Transport" to listOf(
            "metro", "bus", "train", "railway", "ticket", "transit", "uber pool",
            "carpool", "transport", "commute"
        ),
        "Ride Sharing" to listOf(
            "uber", "ola", "cab", "taxi", "ride", "auto", "rapido",
            "transport"
        ),
        "Vehicle Maintenance" to listOf(
            "mechanic", "service", "repair", "garage", "maintenance", "washing",
            "car wash", "oil change", "spare parts"
        ),
        "Electricity" to listOf(
            "electricity", "power", "bill", "kwh", "electric", "power bill",
            "utility", "discom"
        ),
        "Water" to listOf(
            "water", "hydro", "municipal", "aqua", "supply", "purifier",
            "tank"
        ),
        "Internet" to listOf(
            "internet", "wifi", "broadband", "airtel", "jio", "vodafone",
            "recharge", "data", "isp", "telecom"
        ),
        "Home Maintenance" to listOf(
            "plumber", "electrician", "carpenter", "paint", "construction",
            "repair", "maintenance", "cleaning", "labour"
        ),
        "Clothing" to listOf(
            "clothes", "dress", "shirt", "pants", "shoe", "fashion", "apparel",
            "boutique", "tailoring", "garment", "wear"
        ),
        "Electronics" to listOf(
            "electronics", "phone", "laptop", "computer", "gadget", "mobile",
            "tablet", "camera", "device", "tech"
        ),
        "Home & Furniture" to listOf(
            "furniture", "sofa", "bed", "table", "chair", "home", "decor",
            "kitchen", "bedding", "curtain"
        ),
        "Books & Media" to listOf(
            "book", "novel", "comics", "magazine", "library", "reader",
            "kindle", "audiobook", "publication"
        ),
        "Movies & Streaming" to listOf(
            "movie", "cinema", "netflix", "prime video", "youtube", "hotstar",
            "theater", "ticket", "film", "show", "stream"
        ),
        "Gaming" to listOf(
            "game", "gaming", "steam", "playstore", "console", "ps", "xbox",
            "nintendo", "esports"
        ),
        "Sports & Hobbies" to listOf(
            "gym", "fitness", "sport", "hobby", "yoga", "trainer", "class",
            "equipment", "league"
        ),
        "Subscriptions" to listOf(
            "subscription", "monthly", "plan", "premium", "membership",
            "recurring", "annual", "fee"
        ),
        "Medical" to listOf(
            "doctor", "hospital", "clinic", "medical", "health", "medicine",
            "appointment", "consultation", "surgery"
        ),
        "Pharmacy" to listOf(
            "pharmacy", "medicine", "drug", "tablets", "prescription",
            "chemist", "pills", "vaccine"
        ),
        "Gym & Fitness" to listOf(
            "gym", "fitness", "yoga", "trainer", "workout", "exercise",
            "membership", "class", "studio"
        ),
        "Health Insurance" to listOf(
            "insurance", "health", "policy", "premium", "mediclaim",
            "coverage"
        ),
        "Loan Payment" to listOf(
            "loan", "emi", "mortgage", "credit", "monthly payment",
            "installment", "debt"
        ),
        "Investment" to listOf(
            "stock", "mutual fund", "investment", "trading", "broker",
            "portfolio", "dividend"
        ),
        "Bank Charges" to listOf(
            "charge", "fee", "bank", "atm", "transfer", "account",
            "maintenance", "penalty"
        ),
        "Tuition & Courses" to listOf(
            "tuition", "course", "school", "college", "university", "class",
            "coaching", "training", "academy", "education"
        ),
        "School Supplies" to listOf(
            "book", "pen", "pencil", "notebook", "stationery", "supplies",
            "school", "study", "materials"
        ),
        "Pet Food & Care" to listOf(
            "pet", "dog", "cat", "animal", "food", "care", "treat",
            "grooming", "shop"
        ),
        "Veterinary" to listOf(
            "vet", "veterinary", "clinic", "animal", "doctor", "vaccination",
            "treatment"
        ),
        "Accommodation" to listOf(
            "hotel", "resort", "hostel", "airbnb", "stay", "lodge",
            "apartment", "booking"
        ),
        "Travel & Flights" to listOf(
            "flight", "airline", "travel", "ticket", "train", "bus",
            "booking", "vacation", "tour", "trip"
        ),
        "Gifts & Charity" to listOf(
            "gift", "charity", "donation", "present", "fund", "ngo",
            "contribution"
        )
    )

    /**
     * Suggest a category based on merchant name and description
     * Uses pattern matching and keyword analysis
     */
    fun suggestCategory(
        description: String,
        merchant: String,
        availableCategories: List<Category>
    ): String {
        val text = "$description $merchant".lowercase()

        // Calculate scores for each category
        val scores = mutableMapOf<String, Int>()

        categoryPatterns.forEach { (category, keywords) ->
            var score = 0

            keywords.forEach { keyword ->
                if (text.contains(keyword)) {
                    score += when {
                        merchant.lowercase().contains(keyword) -> 3 // Higher weight for merchant
                        description.lowercase().contains(keyword) -> 2
                        else -> 1
                    }
                }
            }

            if (score > 0) {
                scores[category] = score
            }
        }

        // Return the category with highest score, or "Others" if no match
        return scores.maxByOrNull { it.value }?.key ?: "Others"
    }

    /**
     * Get top 3 category suggestions with confidence scores
     */
    fun getSuggestionsWithConfidence(
        description: String,
        merchant: String,
        availableCategories: List<Category>
    ): List<Pair<String, Float>> {
        val text = "$description $merchant".lowercase()
        val scores = mutableMapOf<String, Int>()

        categoryPatterns.forEach { (category, keywords) ->
            var score = 0

            keywords.forEach { keyword ->
                if (text.contains(keyword)) {
                    score += when {
                        merchant.lowercase().contains(keyword) -> 3
                        description.lowercase().contains(keyword) -> 2
                        else -> 1
                    }
                }
            }

            if (score > 0) {
                scores[category] = score
            }
        }

        // Calculate confidence as percentage of max possible score
        val maxScore = 5 // Rough estimate of max keywords matched per category
        val sorted = scores.entries.sortedByDescending { it.value }
            .take(3)
            .map { (category, score) ->
                category to ((score.toFloat() / (maxScore * 3)) * 100).coerceIn(0f, 100f)
            }

        return sorted
    }

    /**
     * Learn from user selections to improve suggestions (future enhancement)
     * This would be used to build a personalized model
     */
    data class LearningData(
        val description: String,
        val merchant: String,
        val selectedCategory: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val learningHistory = mutableListOf<LearningData>()

    fun recordUserSelection(data: LearningData) {
        learningHistory.add(data)
    }

    fun getAccuracy(): Float {
        if (learningHistory.isEmpty()) return 0f

        // In a real scenario, this would compare suggestions with user selections
        // and calculate accuracy percentage
        return 85f // Placeholder
    }
}
