# Expense Tracker Implementation Guide

This document provides detailed instructions for implementing additional features and connecting the UI to the database.

## Quick Start

### 1. Setup Database Connection

The database is already configured in [ExpenseDatabase.kt](app/src/main/java/com/expense/tracker/data/database/ExpenseDatabase.kt). To use it in your activities:

```kotlin
// In MainActivity or any Activity
val database = ExpenseDatabase.getDatabase(this)
val expenseDao = database.expenseDao()
val categoryDao = database.categoryDao()
val repository = ExpenseRepository(expenseDao, categoryDao)
```

### 2. Initialize Default Categories

Call this once on app startup:

```kotlin
lifecycleScope.launch {
    repository.initializeDefaultCategories()
}
```

### 3. Connect HomeScreen to Database

Update [HomeScreen.kt](app/src/main/java/com/expense/tracker/ui/screens/HomeScreen.kt):

```kotlin
@Composable
fun HomeScreen(
    navController: NavHostController,
    repository: ExpenseRepository = LocalRepository.current // Add dependency
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var expenses by remember { mutableStateOf(emptyList<Expense>()) }
    var totalExpenses by remember { mutableStateOf(0.0) }
    var categoryBreakdown by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }

    LaunchedEffect(currentMonth) {
        val startDate = DateUtils.getStartOfMonth(currentMonth)
        val endDate = DateUtils.getEndOfMonth(currentMonth)
        
        expenses = repository.getExpensesByDateRange(startDate, endDate)
        totalExpenses = repository.getTotalExpensesByDateRange(startDate, endDate)
        
        val breakdown = repository.getCategoryBreakdown(startDate, endDate)
        categoryBreakdown = breakdown.associate { it.category to it.total }
    }
    
    // ... rest of the UI
}
```

### 4. Connect AddExpenseScreen to Database

Update [AddExpenseScreen.kt](app/src/main/java/com/expense/tracker/ui/screens/AddExpenseScreen.kt):

```kotlin
Button(
    onClick = {
        if (amount.isNotEmpty() && amount.toDoubleOrNull() != null) {
            isLoading = true
            lifecycleScope.launch {
                val expense = Expense(
                    amount = amount.toDouble(),
                    category = category,
                    description = description,
                    paymentMode = paymentMode,
                    date = LocalDateTime.of(selectedDate, selectedTime),
                    notes = notes,
                    source = "MANUAL"
                )
                repository.insertExpense(expense)
                isLoading = false
                navController.popBackStack()
            }
        }
    },
    // ... button styling
)
```

### 5. Request SMS Permissions

Add to MainActivity:

```kotlin
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavigation() {
    val smsPermissionState = rememberPermissionState(Manifest.permission.READ_SMS)
    val receivePermissionState = rememberPermissionState(Manifest.permission.RECEIVE_SMS)
    
    LaunchedEffect(Unit) {
        if (!smsPermissionState.hasPermission) {
            smsPermissionState.launchPermissionRequest()
        }
        if (!receivePermissionState.hasPermission) {
            receivePermissionState.launchPermissionRequest()
        }
    }
    
    // ... navigation setup
}
```

## Advanced Features

### Enable SMS Reading on App Startup

Add to MainActivity:

```kotlin
private suspend fun readExistingSMS(context: Context, repository: ExpenseRepository) {
    lifecycleScope.launch(Dispatchers.IO) {
        try {
            val allExpenses = SMSReader.readAllSMS(context.contentResolver)
            
            for (expense in allExpenses) {
                // Check for duplicates
                val existing = repository.getExpenseBySmsId(expense.smsId)
                if (existing == null) {
                    repository.insertExpense(expense)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```

### Generate Reports with Charts

Add MPAndroidChart implementation to ReportsScreen:

```kotlin
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun MonthlyChartCard(report: MonthlyReport) {
    Card(modifier = Modifier.fillMaxWidth()) {
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    val entries = report.categoryBreakdown.map { (category, amount) ->
                        PieEntry(amount.toFloat(), category)
                    }
                    val dataSet = PieDataSet(entries, "Expenses")
                    val data = PieData(dataSet)
                    setData(data)
                    invalidate()
                }
            }
        )
    }
}
```

### Export Reports

```kotlin
Button(onClick = {
    lifecycleScope.launch(Dispatchers.IO) {
        val startDate = DateUtils.getStartOfMonth(currentMonth)
        val endDate = DateUtils.getEndOfMonth(currentMonth)
        val expenses = repository.getExpensesByDateRange(startDate, endDate)
        
        val report = ReportGenerator.generateMonthlyReport(
            expenses,
            YearMonth.parse(currentMonth.toString())
        )
        
        val pdfFile = ExportUtils.exportMonthlyReportToPdf(
            context,
            report,
            expenses,
            "expense_report_${currentMonth}.pdf"
        )
        
        if (pdfFile != null) {
            // Share or save the file
            Toast.makeText(context, "Report saved to ${pdfFile.path}", Toast.LENGTH_LONG).show()
        }
    }
}) {
    Text("Export PDF")
}
```

## SMS Parsing Examples

### Example Bank SMS Messages

The parser handles formats like:

1. **HDFC Bank**: `Dear customer, Rs.500 debited from your account ending xxx on 20/01/2024 at 15:30 for UPI transaction at Amazon. Balance: 5000`

2. **ICICI**: `Your account is debited by INR 1200 on 20-Jan-24 15:45 for debit card transaction at Zomato Food`

3. **Google Pay**: `You sent ₹250 to John via UPI on 20/01/24`

The parser extracts:
- **Amount**: 500/1200/250
- **Category**: Shopping (Amazon), Food (Zomato)
- **Merchant**: Amazon, Zomato
- **Payment Mode**: Card/UPI
- **Date/Time**: Automatically parsed

### Test the Parser

```kotlin
val testSMS = "Dear customer, Rs.500 debited from your account on 20/01/2024 at 15:30 for UPI transaction at Amazon. Balance: 5000"
val parsed = SMSTransactionParser.parseTransaction(testSMS, "HDFC_BANK")

println("Amount: ${parsed?.amount}")          // 500.0
println("Category: ${parsed?.category}")      // Shopping
println("Merchant: ${parsed?.merchant}")      // Amazon
println("Payment Mode: ${parsed?.paymentMode}")  // UPI
```

## Dependency Injection Setup (Optional)

For better architecture, implement Hilt:

```kotlin
// In MainActivity
@HiltAndroidApp
class ExpenseTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}

// Create a module
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideExpenseDatabase(context: Context): ExpenseDatabase {
        return ExpenseDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideExpenseRepository(
        database: ExpenseDatabase
    ): ExpenseRepository {
        return ExpenseRepository(database.expenseDao(), database.categoryDao())
    }
}

// Use in Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val repository: ExpenseRepository by hiltViewModel()
}
```

## Testing

### Test SMS Parser

```kotlin
@Test
fun testSMSParsing() {
    val sms = "Your account is debited by Rs.1000 at Amazon. Balance: 5000"
    val parsed = SMSTransactionParser.parseTransaction(sms, "HDFC")
    
    assertEquals(1000.0, parsed?.amount)
    assertEquals("Shopping", parsed?.category)
}

@Test
fun testFilter() {
    val expenses = listOf(
        Expense(amount = 100.0, category = "Food", ...),
        Expense(amount = 500.0, category = "Transport", ...),
        Expense(amount = 200.0, category = "Food", ...)
    )
    
    val filtered = FilterUtils.filterExpensesByCategory(expenses, listOf("Food"))
    assertEquals(2, filtered.size)
}

@Test
fun testReportGeneration() {
    val expenses = listOf(...)
    val report = ReportGenerator.generateMonthlyReport(expenses, YearMonth.now())
    
    assertTrue(report.totalExpenses > 0)
    assertTrue(report.categoryBreakdown.isNotEmpty())
}
```

## Debugging

### Check Database Contents

```kotlin
lifecycleScope.launch {
    repository.getAllExpenses().collect { expenses ->
        expenses.forEach { expense ->
            Log.d("ExpenseDB", "ID: ${expense.id}, Amount: ${expense.amount}, Category: ${expense.category}")
        }
    }
}
```

### Monitor SMS Receiver

Add to SMSReceiver:

```kotlin
Log.d("SMSReceiver", "SMS Received from: $sender")
Log.d("SMSReceiver", "Body: $smsBody")
Log.d("SMSReceiver", "Parsed: $parsedTransaction")
```

## Next Steps

1. **Connect Repository to UI** - Implement data binding between screens and database
2. **Add More Tests** - Unit and integration tests
3. **Implement Notifications** - Show alerts when expenses are added
4. **Cloud Backup** - Add Google Drive integration
5. **Budget Alerts** - Notify when budget limit is exceeded
6. **Receipt Images** - Store expense receipts

## Troubleshooting

- **Database not initializing**: Ensure Room dependency is correct
- **SMS not detected**: Check manifest permissions and receiver registration
- **UI not updating**: Verify Flow.collect is being used correctly
- **Memory leaks**: Use lifecycleScope instead of GlobalScope

For more information, refer to the main [README.md](../README.md).
