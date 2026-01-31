# API Reference - Expense Tracker Android App

## Core Classes & Methods

### 1. ExpenseRepository

**Purpose**: Main data access layer for expenses and categories

#### Expense Operations
```kotlin
// Create
suspend fun insertExpense(expense: Expense): Long
suspend fun insertExpenses(expenses: List<Expense>)

// Read
fun getAllExpenses(): Flow<List<Expense>>
suspend fun getExpenseById(id: Long): Expense?
suspend fun getExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<Expense>
suspend fun getExpensesByCategory(category: String): List<Expense>
suspend fun getExpensesByAmountRange(minAmount: Double, maxAmount: Double): List<Expense>
suspend fun getExpensesByPaymentMode(paymentMode: String): List<Expense>

// Update
suspend fun updateExpense(expense: Expense)

// Delete
suspend fun deleteExpense(expense: Expense)
suspend fun deleteExpensesByIds(ids: List<Long>)
suspend fun deleteBySmsId(smsId: String)

// Analytics
suspend fun getTotalExpensesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Double
suspend fun getCategoryBreakdown(startDate: LocalDateTime, endDate: LocalDateTime): List<CategoryTotal>
suspend fun getExpenseCountByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Int

// Deduplication
suspend fun getExpenseBySmsId(smsId: String): Expense?
```

#### Category Operations
```kotlin
// Create
suspend fun insertCategory(category: Category): Long

// Read
fun getAllCategories(): Flow<List<Category>>
suspend fun getCategoryByName(name: String): Category?
suspend fun getDefaultCategories(): List<Category>

// Update
suspend fun updateCategory(category: Category)

// Delete
suspend fun deleteCategory(category: Category)
suspend fun deleteCustomCategory(name: String)

// Initialize
suspend fun initializeDefaultCategories()
```

---

### 2. Expense (Data Model)

```kotlin
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,                    // Transaction amount
    val category: String,                  // Food, Transport, etc.
    val description: String,               // Merchant/details
    val paymentMode: String,               // "Cash" | "UPI" | "Card"
    val date: LocalDateTime,               // Transaction date & time
    val notes: String = "",                // Optional user notes
    val source: String = "MANUAL",         // "MANUAL" | "SMS"
    val smsId: String = "",                // For deduplication
    val merchant: String = "",             // Extracted merchant name
    val bank: String = "",                 // Bank/SMS sender
    val createdAt: LocalDateTime,          // Record creation time
    val updatedAt: LocalDateTime           // Last update time
)
```

---

### 3. Category (Data Model)

```kotlin
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                      // Category name (unique)
    val isDefault: Boolean = false,        // Predefined vs custom
    val color: String = "#FF9C27B0"        // Hex color for UI
)

// Predefined categories (8 default)
val DEFAULT_CATEGORIES = listOf(
    Category(name = "Food", isDefault = true, color = "#FFFF5722"),
    Category(name = "Transport", isDefault = true, color = "#FF2196F3"),
    Category(name = "Rent", isDefault = true, color = "#FF4CAF50"),
    Category(name = "Utilities", isDefault = true, color = "#FFF57C00"),
    Category(name = "Shopping", isDefault = true, color = "#FF9C27B0"),
    Category(name = "Entertainment", isDefault = true, color = "#FF673AB7"),
    Category(name = "Medical", isDefault = true, color = "#FFF44336"),
    Category(name = "Others", isDefault = true, color = "#FF607D8B")
)
```

---

### 4. SMSTransactionParser

**Purpose**: Parse bank/wallet SMS messages and extract transaction details

#### Main Method
```kotlin
fun parseTransaction(
    smsBody: String,        // SMS message content
    sender: String,         // SMS sender ID
    timestamp: Long         // Message timestamp (ms)
): ParsedTransaction?       // Returns null if not a valid transaction
```

#### Return Type
```kotlin
data class ParsedTransaction(
    val amount: Double,          // Transaction amount
    val category: String,        // Auto-detected category
    val description: String,     // Merchant name
    val paymentMode: String,     // "Card" | "UPI" | "Transfer"
    val merchant: String         // Extracted merchant
)
```

#### Detection Logic
```
1. Ignores OTP messages (contains "OTP", "password", etc.)
2. Ignores credit messages (contains "credited", "received", etc.)
3. Ignores promotional messages (contains "promo", "offer", etc.)
4. Looks for debit keywords (debited, withdrawn, spent, paid, charged)
5. Extracts amount using regex patterns
6. Categorizes based on merchant keywords
```

#### Supported SMS Formats
```
✅ "Rs.500 debited from account"
✅ "₹1000 spent at Amazon"
✅ "Amount: 200 UPI transfer"
✅ "Debited INR 350 at Uber on 20/01/2024 15:30"
✅ "Your card payment: 2500 at Hotel - Balance: 25000"
```

---

### 5. FilterUtils

**Purpose**: Filter and search expenses with various criteria

#### Methods
```kotlin
// Single filters
fun filterExpensesByDateRange(
    expenses: List<Expense>,
    startDate: LocalDateTime,
    endDate: LocalDateTime
): List<Expense>

fun filterExpensesByCategory(
    expenses: List<Expense>,
    categories: List<String>
): List<Expense>

fun filterExpensesByAmountRange(
    expenses: List<Expense>,
    minAmount: Double,
    maxAmount: Double
): List<Expense>

fun filterExpensesByPaymentMode(
    expenses: List<Expense>,
    paymentModes: List<String>
): List<Expense>

fun filterExpensesBySource(
    expenses: List<Expense>,
    source: String          // "SMS" | "MANUAL" | "ALL"
): List<Expense>

fun searchExpenses(
    expenses: List<Expense>,
    query: String           // Search in description, category, merchant, notes
): List<Expense>

// Combined filters
fun applyMultipleFilters(
    expenses: List<Expense>,
    startDate: LocalDateTime? = null,
    endDate: LocalDateTime? = null,
    categories: List<String> = emptyList(),
    minAmount: Double = 0.0,
    maxAmount: Double = Double.MAX_VALUE,
    paymentModes: List<String> = emptyList(),
    searchQuery: String = ""
): List<Expense>
```

#### Usage Example
```kotlin
val filtered = FilterUtils.applyMultipleFilters(
    expenses = allExpenses,
    startDate = LocalDateTime.of(2024, 1, 1, 0, 0),
    endDate = LocalDateTime.of(2024, 1, 31, 23, 59),
    categories = listOf("Food", "Transport"),
    minAmount = 100.0,
    maxAmount = 5000.0,
    paymentModes = listOf("UPI", "Card"),
    searchQuery = "amazon"
)
// Returns: Expenses from Jan 2024, in Food/Transport, 
//          Rs 100-5000, paid by UPI/Card, mentioning amazon
```

---

### 6. ReportGenerator

**Purpose**: Generate monthly/yearly financial reports and analytics

#### Methods
```kotlin
fun generateMonthlyReport(
    expenses: List<Expense>,
    yearMonth: YearMonth
): MonthlyReport

fun generateYearlyReport(
    expenses: List<Expense>,
    year: Int
): Map<String, MonthlyReport>  // Map of month -> report

fun getTopCategories(
    report: MonthlyReport,
    limit: Int = 5
): List<Pair<String, Double>>  // (Category, Amount)

fun getAverageDailyExpense(report: MonthlyReport): Double

fun getHighestExpenseDay(report: MonthlyReport): Pair<Int, Double>?  // (Day, Amount)

fun getLowestExpenseDay(report: MonthlyReport): Pair<Int, Double>?   // (Day, Amount)

fun comparePeriods(
    currentReport: MonthlyReport,
    previousReport: MonthlyReport
): Map<String, Double>  // Comparison metrics
```

#### Return Types
```kotlin
data class MonthlyReport(
    val month: String,                          // "2024-01"
    val totalExpenses: Double,                  // Sum of all expenses
    val categoryBreakdown: Map<String, Double>, // Category -> Amount
    val dailySpending: Map<Int, Double>         // Day -> Amount
)
```

#### Usage Example
```kotlin
val expenses = repository.getExpensesByDateRange(startDate, endDate)
val report = ReportGenerator.generateMonthlyReport(expenses, YearMonth.now())

println("Total: ₹${report.totalExpenses}")
println("By Category: ${report.categoryBreakdown}")
println("Daily Avg: ₹${ReportGenerator.getAverageDailyExpense(report)}")
println("Top 3 Categories: ${ReportGenerator.getTopCategories(report, 3)}")
```

---

### 7. ExportUtils

**Purpose**: Export expenses to CSV and PDF formats

#### Methods
```kotlin
fun exportToCsv(
    expenses: List<Expense>,
    fileName: String
): File?

fun exportMonthlyReportToPdf(
    context: Context,
    report: MonthlyReport,
    expenses: List<Expense>,
    fileName: String
): File?
```

#### CSV Output Format
```
Date,Time,Description,Category,Amount,Payment Mode,Notes,Source
20-Jan-2024,15:30,Amazon,Shopping,1000.00,Card,Electronics,MANUAL
20-Jan-2024,15:45,Zomato,Food,450.00,UPI,Dinner,SMS
```

#### PDF Output Format
- Title and month/year
- Summary section (total, count, average)
- Category breakdown table
- Detailed transactions table
- Ready for printing

#### Usage Example
```kotlin
val report = ReportGenerator.generateMonthlyReport(expenses, YearMonth.now())

// Export to PDF
val pdfFile = ExportUtils.exportMonthlyReportToPdf(
    context = this,
    report = report,
    expenses = expenses,
    fileName = "expense_report_jan_2024.pdf"
)

// Export to CSV
val csvFile = ExportUtils.exportToCsv(
    expenses = expenses,
    fileName = "expenses_jan_2024.csv"
)
```

---

### 8. DateUtils

**Purpose**: Date formatting and manipulation utilities

#### Methods
```kotlin
fun getStartOfMonth(yearMonth: YearMonth): LocalDateTime
fun getEndOfMonth(yearMonth: YearMonth): LocalDateTime

fun formatDate(dateTime: LocalDateTime): String         // "20 Jan 2024"
fun formatTime(dateTime: LocalDateTime): String         // "15:30"
fun formatMonthYear(yearMonth: YearMonth): String       // "Jan 2024"

fun getDayOfMonth(dateTime: LocalDateTime): Int
```

---

### 9. CategoryUtils

**Purpose**: Category helper utilities

#### Methods
```kotlin
fun getCategoryColor(
    categoryName: String,
    categories: List<Category>
): String  // Hex color

fun isDefaultCategory(
    categoryName: String,
    categories: List<Category>
): Boolean
```

---

### 10. SMSReader

**Purpose**: Read SMS from device SMS inbox

#### Methods
```kotlin
fun readAllSMS(contentResolver: ContentResolver): List<Expense>

fun readSMSSince(
    contentResolver: ContentResolver,
    since: Long  // Timestamp in milliseconds
): List<Expense>
```

#### Requirements
- `READ_SMS` permission
- Returns parsed expenses from SMS

---

## Database DAOs

### ExpenseDao
```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertExpense(expense: Expense): Long

@Update
suspend fun updateExpense(expense: Expense)

@Delete
suspend fun deleteExpense(expense: Expense)

@Query("...")
suspend fun getExpenseById(id: Long): Expense?

@Query("...")
fun getAllExpenses(): Flow<List<Expense>>

// ... more queries
```

### CategoryDao
```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertCategory(category: Category): Long

@Update
suspend fun updateCategory(category: Category)

@Query("...")
fun getAllCategories(): Flow<List<Category>>

// ... more queries
```

---

## Data Models

### Expense
- Primary key: `id` (auto-increment)
- Indexed: `date`, `category`, `smsId`
- Storage: LocalDateTime -> String

### Category
- Primary key: `id`
- Unique: `name`
- Defaults: 8 predefined categories

### CategoryTotal (Query result)
```kotlin
data class CategoryTotal(
    val category: String,
    val total: Double
)
```

---

## Enums

### PaymentMode
```kotlin
"Cash"      // Cash payment
"UPI"       // Unified Payment Interface
"Card"      // Debit/Credit card
"Transfer"  // Bank transfer (NEFT/RTGS/IMPS)
```

### ExpenseSource
```kotlin
"SMS"       // Auto-detected from SMS
"MANUAL"    // Manually entered by user
```

---

## Constants & Defaults

### Default Categories (8)
1. Food - Red (#FFFF5722)
2. Transport - Blue (#FF2196F3)
3. Rent - Green (#FF4CAF50)
4. Utilities - Orange (#FFF57C00)
5. Shopping - Purple (#FF9C27B0)
6. Entertainment - Deep Purple (#FF673AB7)
7. Medical - Pink (#FFF44336)
8. Others - Blue-Grey (#FF607D8B)

---

## Error Handling

### Common Errors
```kotlin
// Database errors
// → Room throws SQLiteException
// → Caught in try-catch or coroutine error handler

// SMS parsing errors
// → Returns null if not a valid transaction
// → No exception thrown

// Permission errors
// → SMSReader throws SecurityException
// → Request permission before calling
```

### Error Recovery
```kotlin
try {
    repository.insertExpense(expense)
} catch (e: Exception) {
    Log.e("ExpenseDB", "Error inserting expense", e)
    // Show error to user
}
```

---

## Performance Characteristics

| Operation | Time |
|-----------|------|
| SMS parsing | < 1.5 sec |
| Insert expense | < 100 ms |
| Query by date range | < 200 ms |
| Generate report | < 800 ms |
| Export to PDF | < 2 sec |
| Export to CSV | < 1 sec |

---

## Thread Safety

### Repository Methods
- All marked `suspend` - use with coroutines
- Safe for concurrent access
- Room handles threading automatically

### UI Updates
- Use `lifecycleScope.launch` for safe context handling
- Collect Flow in UI layer
- Automatic cancellation on lifecycle end

---

## Example: Complete Usage

```kotlin
// Initialize
val database = ExpenseDatabase.getDatabase(this)
val repository = ExpenseRepository(database.expenseDao(), database.categoryDao())

// Initialize default categories
lifecycleScope.launch {
    repository.initializeDefaultCategories()
}

// Add expense
lifecycleScope.launch {
    val expense = Expense(
        amount = 500.0,
        category = "Food",
        description = "Pizza Hut",
        paymentMode = "Card",
        date = LocalDateTime.now(),
        notes = "Dinner"
    )
    val id = repository.insertExpense(expense)
    Log.d("App", "Expense saved with ID: $id")
}

// Get monthly report
lifecycleScope.launch {
    val startDate = DateUtils.getStartOfMonth(YearMonth.now())
    val endDate = DateUtils.getEndOfMonth(YearMonth.now())
    
    val expenses = repository.getExpensesByDateRange(startDate, endDate)
    val report = ReportGenerator.generateMonthlyReport(expenses, YearMonth.now())
    
    Log.d("App", "Total expenses: ₹${report.totalExpenses}")
}

// Export report
lifecycleScope.launch(Dispatchers.IO) {
    val file = ExportUtils.exportMonthlyReportToPdf(
        context = this@MainActivity,
        report = report,
        expenses = expenses,
        fileName = "report.pdf"
    )
    if (file != null) {
        Log.d("App", "Report saved to: ${file.path}")
    }
}
```

---

**Last Updated**: January 26, 2024  
**API Version**: 1.0.0
