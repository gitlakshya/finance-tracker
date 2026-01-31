package com.expense.tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.expense.tracker.MainActivity
import com.expense.tracker.utils.DateUtils
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavHostController) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var monthlyTotal by remember { mutableStateOf(0.0) }
    var averageExpense by remember { mutableStateOf(0.0) }
    var transactionCount by remember { mutableStateOf(0) }
    var categoryBreakdown by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    var topMerchants by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    var paymentModes by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Load data for the selected month
    LaunchedEffect(currentMonth) {
        isLoading = true
        scope.launch {
            try {
                val startOfMonth = currentMonth.atDay(1).atStartOfDay()
                val endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59)

                monthlyTotal = MainActivity.repository.getMonthlyTotal(startOfMonth, endOfMonth)
                averageExpense = MainActivity.repository.getMonthlyAverageAmount(startOfMonth, endOfMonth)
                transactionCount = MainActivity.repository.getExpenseCountByDateRange(startOfMonth, endOfMonth)

                categoryBreakdown = MainActivity.repository.getCategorySpendingTrend(startOfMonth, endOfMonth)
                    .map { it.category to it.total }
                    .sortedByDescending { it.second }

                topMerchants = MainActivity.repository.getTopMerchants(startOfMonth, endOfMonth, 5)
                    .map { it.merchant to it.total }

                paymentModes = MainActivity.repository.getPaymentModeBreakdown(startOfMonth, endOfMonth)
                    .map { it.paymentMode to it.total }

                isLoading = false
            } catch (e: Exception) {
                isLoading = false
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Reports & Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Month selector
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { currentMonth = currentMonth.minusMonths(1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Previous month", modifier = Modifier.size(18.dp))
                            }

                            Text(
                                text = currentMonth.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy")).uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { currentMonth = currentMonth.plusMonths(1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Next month", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Summary cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryCard(
                            title = "Total Spent",
                            value = "₹ %.0f".format(monthlyTotal),
                            modifier = Modifier.weight(1f)
                        )

                        SummaryCard(
                            title = "Average",
                            value = "₹ %.0f".format(averageExpense),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryCard(
                            title = "Transactions",
                            value = transactionCount.toString(),
                            modifier = Modifier.weight(1f)
                        )

                        SummaryCard(
                            title = "Highest Day",
                            value = "View Calendar",
                            isButton = true,
                            onButtonClick = { navController.navigate("calendar_view") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Category Breakdown
                if (categoryBreakdown.isNotEmpty()) {
                    item {
                        Text(
                            text = "Category Breakdown",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categoryBreakdown.forEach { (category, amount) ->
                                    CategoryBreakdownItem(
                                        category = category,
                                        amount = amount,
                                        total = monthlyTotal
                                    )
                                }
                            }
                        }
                    }
                }

                // Payment Mode Distribution
                if (paymentModes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Payment Method Distribution",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(paymentModes) { (mode, amount) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mode,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹ %.2f".format(amount),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "%.1f%%".format((amount / monthlyTotal) * 100),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }

                // Top Merchants
                if (topMerchants.isNotEmpty()) {
                    item {
                        Text(
                            text = "Top Merchants",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(topMerchants) { (merchant, amount) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = merchant.take(30),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "₹ %.2f".format(amount),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Action buttons
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("calendar_view") },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.padding(end = 6.dp).size(16.dp))
                            Text("Calendar", style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = { /* Export PDF */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Export", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    isButton: Boolean = false,
    onButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (isButton) {
        OutlinedButton(
            onClick = onButtonClick,
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        Card(
            modifier = modifier.height(64.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownItem(
    category: String,
    amount: Double,
    total: Double
) {
    val percentage = if (total > 0) (amount / total) * 100 else 0.0

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹ %.2f".format(amount),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "%.1f%%".format(percentage),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Progress bar
        LinearProgressIndicator(
            progress = ((percentage / 100).toFloat()).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
