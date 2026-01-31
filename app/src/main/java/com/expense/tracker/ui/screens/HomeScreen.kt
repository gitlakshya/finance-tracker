package com.expense.tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.expense.tracker.MainActivity
import com.expense.tracker.data.model.Expense
import com.expense.tracker.utils.DateUtils
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth

@Composable
fun HomeScreen(navController: NavHostController) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var expenses by remember { mutableStateOf(emptyList<Expense>()) }
    var totalExpenses by remember { mutableStateOf(0.0) }
    var categoryBreakdown by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentMonth) {
        isLoading = true
        try {
            val startDate = DateUtils.getStartOfMonth(currentMonth)
            val endDate = DateUtils.getEndOfMonth(currentMonth)
            
            expenses = MainActivity.repository.getExpensesByDateRange(startDate, endDate)
            totalExpenses = MainActivity.repository.getTotalExpensesByDateRange(startDate, endDate)
            categoryBreakdown = MainActivity.repository.getCategoryBreakdown(startDate, endDate)
                .associate { (category, amount) -> category to amount }
        } catch (e: Exception) {
            // Handle error
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Month Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { currentMonth = currentMonth.minusMonths(1) }, modifier = Modifier.height(40.dp)) {
                Text("< Previous", style = MaterialTheme.typography.labelMedium)
            }
            Text(
                text = DateUtils.formatMonthYear(currentMonth),
                style = MaterialTheme.typography.titleMedium
            )
            Button(onClick = { currentMonth = currentMonth.plusMonths(1) }, modifier = Modifier.height(40.dp)) {
                Text("Next >", style = MaterialTheme.typography.labelMedium)
            }
        }

        // Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "Total Expenses",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "₹${String.format("%.2f", totalExpenses)}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${expenses.size} transactions",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // Category Breakdown
        if (categoryBreakdown.isNotEmpty()) {
            Text(
                text = "Category Breakdown",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            categoryBreakdown.forEach { (category, amount) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = category, style = MaterialTheme.typography.bodySmall)
                    Text(text = "₹${String.format("%.2f", amount)}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Recent Expenses
        Text(
            text = "Recent Expenses",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No expenses found",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        } else {
            LazyColumn {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseItemCard(
                        expense = expense,
                        onDelete = {
                            scope.launch {
                                try {
                                    MainActivity.repository.deleteExpense(expense)
                                    // Reload expenses
                                    val startDate = DateUtils.getStartOfMonth(currentMonth)
                                    val endDate = DateUtils.getEndOfMonth(currentMonth)
                                    expenses = MainActivity.repository.getExpensesByDateRange(startDate, endDate)
                                    totalExpenses = MainActivity.repository.getTotalExpensesByDateRange(startDate, endDate)
                                    categoryBreakdown = MainActivity.repository.getCategoryBreakdown(startDate, endDate)
                                        .associate { (category, amount) -> category to amount }
                                } catch (e: Exception) {
                                    // Handle error
                                }
                            }
                        },
                        onEdit = {
                            navController.navigate("edit_expense/${expense.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseItemCard(
    expense: Expense,
    onDelete: () -> Unit,
    onEdit: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "${expense.category} • ${DateUtils.formatDate(expense.date)}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = expense.paymentMode,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "-₹${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
