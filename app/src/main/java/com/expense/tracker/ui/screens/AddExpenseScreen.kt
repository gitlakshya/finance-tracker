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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavHostController) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Groceries") }
    var description by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("Card") }
    var notes by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }

    val paymentModes = Constants.PaymentModes.ALL
    val scope = rememberCoroutineScope()

    // Load categories from database
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val cats = MainActivity.repository.getAllCategories().first()
                categories = cats.map { it.name }
                if (categories.isNotEmpty() && category !in categories) {
                    category = categories.first()
                }
            } catch (e: Exception) {
                categories = Constants.DefaultCategories.FALLBACK
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
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
            }
            Text(
                text = "Add Expense",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn {
            item {
                // Amount Input
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount *", style = MaterialTheme.typography.labelSmall) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    leadingIcon = { Text("₹", style = MaterialTheme.typography.bodyMedium) },
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                // Category Selection
                var expandedCategory by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
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
                    label = { Text("Description / Merchant", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    maxLines = 2,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                // Payment Mode Selection
                var expandedMode by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedMode,
                    onExpandedChange = { expandedMode = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    OutlinedTextField(
                        value = paymentMode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Mode", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMode,
                        onDismissRequest = { expandedMode = false }
                    ) {
                        paymentModes.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode, style = MaterialTheme.typography.bodySmall) },
                                onClick = {
                                    paymentMode = mode
                                    expandedMode = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                // Date and Time Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("Date: ${selectedDate}", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Text("Time: ${selectedTime}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            item {
                // Notes Input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    maxLines = 3,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                // Save Button
                Button(
                    onClick = {
                        if (amount.isNotEmpty() && amount.toDoubleOrNull() != null) {
                            isLoading = true
                            errorMessage = null
                            
                            scope.launch {
                                try {
                                    val amountValue = amount.toDouble()
                                    val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                                    
                                    val expense = Expense(
                                        amount = amountValue,
                                        category = category,
                                        description = description.ifEmpty { "Manual Entry" },
                                        paymentMode = paymentMode,
                                        date = dateTime,
                                        notes = notes,
                                        source = "Manual",
                                        smsId = "",
                                        merchant = description.ifEmpty { "" },
                                        bank = "",
                                        createdAt = LocalDateTime.now(),
                                        updatedAt = LocalDateTime.now()
                                    )
                                    
                                    MainActivity.repository.insertExpense(expense)
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    errorMessage = "Failed to save expense: ${e.message}"
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
                        Text("Save Expense", style = MaterialTheme.typography.labelMedium)
                    }
                }
                
                // Show error message if any
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
