# Quick Reference Guide - Expense Tracker Android App

## 🚀 Quick Start (2 Minutes)

### 1. Build APK
```bash
cd /workspaces/finance-tracker
./gradlew assembleDebug
```

### 2. Install
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. Run
Tap "Expense Tracker" icon on device/emulator

---

## 📂 Key Files Explained

### Data Layer
| File | Purpose |
|------|---------|
| `ExpenseDatabase.kt` | Room database setup |
| `ExpenseDao.kt` | Expense queries |
| `CategoryDao.kt` | Category queries |
| `ExpenseRepository.kt` | Data access wrapper |

### SMS Processing
| File | Purpose |
|------|---------|
| `SMSReceiver.kt` | Receives incoming SMS |
| `SMSTransactionParser.kt` | Parses SMS content |
| `SMSHandler.kt` | SMS processing logic |

### UI Screens
| File | Screen |
|------|--------|
| `HomeScreen.kt` | Dashboard |
| `AddExpenseScreen.kt` | Add expense form |
| `ReportsScreen.kt` | Monthly reports |
| `SettingsScreen.kt` | App settings |

### Utilities
| File | Function |
|------|----------|
| `FilterUtils.kt` | Filter expenses |
| `ReportGenerator.kt` | Generate reports |
| `ExportUtils.kt` | PDF/CSV export |
| `DateUtils.kt` | Date formatting |

---

## 💾 Database Schema

### Expenses Table
```sql
CREATE TABLE expenses (
    id INTEGER PRIMARY KEY,
    amount REAL,
    category TEXT,
    description TEXT,
    paymentMode TEXT,
    date TEXT,
    notes TEXT,
    source TEXT,
    smsId TEXT,
    merchant TEXT,
    bank TEXT,
    createdAt TEXT,
    updatedAt TEXT
)
```

### Categories Table
```sql
CREATE TABLE categories (
    id INTEGER PRIMARY KEY,
    name TEXT UNIQUE,
    isDefault INTEGER,
    color TEXT
)
```

---

## 🔧 Common Tasks

### Add Expense Programmatically
```kotlin
val expense = Expense(
    amount = 500.0,
    category = "Food",
    description = "Lunch",
    paymentMode = "Cash",
    date = LocalDateTime.now(),
    notes = "Restaurant"
)
repository.insertExpense(expense)
```

### Generate Monthly Report
```kotlin
val expenses = repository.getExpensesByDateRange(startDate, endDate)
val report = ReportGenerator.generateMonthlyReport(expenses, YearMonth.now())
println("Total: ${report.totalExpenses}")
println("By Category: ${report.categoryBreakdown}")
```

### Export to PDF
```kotlin
val file = ExportUtils.exportMonthlyReportToPdf(
    context,
    report,
    expenses,
    "report.pdf"
)
```

### Filter Expenses
```kotlin
val filtered = FilterUtils.applyMultipleFilters(
    expenses = allExpenses,
    startDate = startOfMonth,
    endDate = endOfMonth,
    categories = listOf("Food", "Transport"),
    searchQuery = "amazon"
)
```

---

## 📊 SMS Parsing Examples

### Supported Formats
```
✅ "Rs.500 debited from account"
✅ "₹1000 spent at Amazon"
✅ "Amount: 200 UPI transfer"
✅ "Debited INR 350 at Uber"

❌ "Salary of Rs.5000 credited"      (Credit - ignored)
❌ "OTP: 123456"                      (OTP - ignored)
❌ "Special offer - 50% off"          (Promo - ignored)
```

### Auto-Categorization
```
"Zomato" / "McDonald's"     → Food
"Uber" / "Ola"              → Transport
"Amazon" / "Flipkart"       → Shopping
"Electricity bill"          → Utilities
"Hospital" / "Pharmacy"     → Medical
```

---

## 🎯 Permission Requests

### Required Permissions
```xml
READ_SMS                    - Read transaction SMS
RECEIVE_SMS                 - Receive SMS notifications
POST_NOTIFICATIONS          - Show app notifications
READ_EXTERNAL_STORAGE       - Read exported files
WRITE_EXTERNAL_STORAGE      - Write reports
INTERNET                    - Future cloud backup
```

### User Can Skip
- SMS integration (use manual mode only)
- Cloud backup
- Notifications

---

## 🧪 Test Commands

### Build & Test
```bash
./gradlew build              # Build project
./gradlew assembleDebug      # Build debug APK
./gradlew assembleRelease    # Build release APK
./gradlew test               # Run unit tests
./gradlew lint               # Check code quality
```

### Install & Run
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.expense.tracker/.MainActivity
```

---

## 📱 UI Navigation

```
Home Screen
  ├─ Show monthly summary
  ├─ List recent expenses
  ├─ Delete expense
  └─ Filter by month

↓ FAB (Add Expense)

Add Expense Screen
  ├─ Enter amount
  ├─ Select category
  ├─ Enter description
  ├─ Choose payment mode
  ├─ Pick date/time
  └─ Save

Bottom Navigation
  ├─ Home
  ├─ Reports
  └─ Settings

Reports Screen
  ├─ View monthly totals
  ├─ See category breakdown
  ├─ Export PDF
  └─ Export CSV

Settings Screen
  ├─ Enable SMS tracking
  ├─ Cloud backup toggle
  ├─ Notification settings
  ├─ Manage categories
  └─ About app
```

---

## 🐛 Debugging

### View Logs
```bash
adb logcat | grep ExpenseTracker
```

### Check Database
```bash
adb shell
cd /data/data/com.expense.tracker/databases/
sqlite3 expense_database
SELECT * FROM expenses;
SELECT * FROM categories;
```

### Clear App Data
```bash
adb shell pm clear com.expense.tracker
```

---

## 📋 Project Dependencies

### Core Libraries
- androidx.core:core-ktx:1.12.0
- androidx.appcompat:appcompat:1.6.1
- androidx.compose.*:2023.10.00
- androidx.room:room-ktx:2.6.1
- org.jetbrains.kotlinx:kotlinx-coroutines:1.7.3

### UI Libraries
- androidx.navigation:navigation-compose:2.7.4
- androidx.lifecycle:lifecycle-runtime-compose:2.6.1

### Export/PDF
- com.itextpdf:itextpdf:5.5.13.3

### Charts
- com.github.PhilJay:MPAndroidChart:v3.1.0

### Permissions
- com.google.accompanist:accompanist-permissions:0.33.1-alpha

---

## 🔄 Development Workflow

### 1. Feature Development
- Create feature branch
- Implement code
- Add tests
- Run tests locally

### 2. Code Review
- Check architecture
- Verify error handling
- Test edge cases

### 3. Build & Package
```bash
./gradlew clean build
./gradlew assembleRelease
# Get APK from: app/build/outputs/apk/release/
```

### 4. Testing
- Manual testing on device
- Check logs for errors
- Verify database integrity

### 5. Deployment
- Version bump
- Update changelog
- Sign APK (for production)
- Upload to Play Store

---

## 📚 Documentation Map

```
PROJECT_SUMMARY.md           ← Start here
├── README.md                ← Features & overview
├── BUILD_GUIDE.md           ← Build & deploy
├── IMPLEMENTATION_GUIDE.md  ← Code integration
└── CHANGELOG.md             ← Version history
```

---

## 🎓 Key Concepts

### Room Database
- ORM for SQLite
- Type-safe database access
- Automatic migration support
- Flow for reactive updates

### Jetpack Compose
- Declarative UI framework
- Composable functions
- State management with remember/mutableState
- Preview in Android Studio

### Coroutines
- Async operations without blocking
- Structured concurrency
- lifecycleScope for proper cleanup
- Dispatchers for threading

### Flow
- Reactive data streams
- Emit values asynchronously
- Collect in UI layer
- Automatic cancellation

---

## ⚡ Performance Tips

### Database
- Use indexed queries
- Batch operations when possible
- Close cursors properly
- Use Flow for reactive updates

### SMS Parsing
- Cache compiled regex patterns
- Limit SMS scanning to recent messages
- Use coroutines for background processing

### UI
- Use lazy loading for lists
- Implement pagination for large datasets
- Avoid recomposition with keys
- Use remember wisely

---

## 🔐 Security Checklist

- [ ] No hardcoded secrets
- [ ] Validate all inputs
- [ ] Use proper permissions
- [ ] Encrypt sensitive data at rest
- [ ] Clear sensitive data on logout
- [ ] Use HTTPS for network calls
- [ ] Implement ProGuard/R8 minification

---

## 📞 Quick Help

| Issue | Solution |
|-------|----------|
| SMS not detected | Grant READ_SMS permission, restart app |
| Database error | Clear app data, reinstall |
| UI not updating | Check Flow.collect, verify repository connection |
| No expenses shown | Check date range, ensure expenses exist |
| Export failed | Check storage permission, available space |
| Build error | Run `./gradlew clean build` |

---

## 📈 Scalability Notes

### Current Design Supports
- 10K+ expenses without performance issues
- 100+ categories
- 5+ years of data

### For Million+ Expenses
- Implement pagination
- Archive old data
- Partition database
- Add indexes on frequently queried columns

---

**Last Updated**: January 26, 2024  
**App Version**: 1.0.0  
**Status**: ✅ Ready for Use
