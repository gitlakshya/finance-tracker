package com.expense.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.expense.tracker.MainActivity
import com.expense.tracker.data.model.Expense
import com.expense.tracker.utils.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(navController: NavHostController, expenseId: Long) {
    var expense by remember { mutableStateOf<Expense?>(null) }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Groceries") }
    var description by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("Card") }
    var notes by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }

    val paymentModes = Constants.PaymentModes.ALL
    val scope = rememberCoroutineScope()

    // Load expense and categories
    LaunchedEffect(expenseId) {
        scope.launch {
            try {
                // Load expense
                MainActivity.repository.getExpenseById(expenseId)?.let { exp ->
                    expense = exp
                    amount = exp.amount.toString()
                    category = exp.category
                    description = exp.description
                    paymentMode = exp.paymentMode
                    notes = exp.notes
                    selectedDate = exp.date.toLocalDate()
                    selectedTime = exp.date.toLocalTime()
                }

                // Load categories
                categories = MainActivity.repository.getAllCategories().first().map { it.name }
            } catch (e: Exception) {
                errorMessage = "Failed to load expense: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Edit Expense",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }

        if (expense == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    // Amount Input
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount *", style = MaterialTheme.typography.labelSmall) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Text("₹", style = MaterialTheme.typography.bodySmall) },
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }

                item {
                    // Category Selection
                    var expandedCategory by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category *", style = MaterialTheme.typography.labelSmall) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, style = MaterialTheme.typography.bodySmall) },
                                    onClick = {
                                        category = cat
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Description Input
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }

                item {
                    // Payment Mode Selection
                    var expandedPayment by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedPayment,
                        onExpandedChange = { expandedPayment = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = paymentMode,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Payment Mode", style = MaterialTheme.typography.labelSmall) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPayment) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPayment,
                            onDismissRequest = { expandedPayment = false }
                        ) {
                            paymentModes.forEach { mode ->
                                DropdownMenuItem(
                                    text = { Text(mode, style = MaterialTheme.typography.bodySmall) },
                                    onClick = {
                                        paymentMode = mode
                                        expandedPayment = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Notes Input
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }

                item {
                    Text(
                        "Date: ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Error Message
                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Update Button
                item {
                    Button(
                        onClick = {
                            if (amount.isNotBlank()) {
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        val amountValue = amount.toDouble()
                                        val dateTime = LocalDateTime.of(selectedDate, selectedTime)

                                        val updatedExpense = expense!!.copy(
                                            amount = amountValue,
                                            category = category,
                                            description = description.ifEmpty { "Manual Entry" },
                                            paymentMode = paymentMode,
                                            notes = notes,
                                            date = dateTime,
                                            updatedAt = LocalDateTime.now()
                                        )

                                        MainActivity.repository.updateExpense(updatedExpense)
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to update expense: ${e.message}"
                                        isLoading = false
                                    }
                                }
                            } else {
                                errorMessage = "Please enter a valid amount"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Update Expense", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
