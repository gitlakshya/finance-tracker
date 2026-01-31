package com.expense.tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.expense.tracker.MainActivity
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarViewScreen(navController: NavHostController) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var daysWithExpenses by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }
    var dailyExpenses by remember { mutableStateOf<Map<LocalDate, Double>>(emptyMap()) }
    val scope = rememberCoroutineScope()

    // Load calendar data  
    LaunchedEffect(Unit) {
        try {
            // Simplified - just load once
            val today = LocalDate.now()
            val start = today.minusMonths(3).atStartOfDay()
            val end = today.plusMonths(3).atStartOfDay()
            // Note: Will need better implementation for production
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    // Load daily expenses for selected day
    LaunchedEffect(selectedDate) {
        try {
            val startOfDay = selectedDate.atStartOfDay()
            val endOfDay = selectedDate.plusDays(1).atStartOfDay()
            val total = MainActivity.repository.getTotalExpensesByDay(startOfDay, endOfDay)
            dailyExpenses = dailyExpenses + (selectedDate to total)
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val calendarState = rememberCalendarState(
        startMonth = YearMonth.now().minusMonths(12),
        endMonth = YearMonth.now().plusMonths(12),
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = firstDayOfWeekFromLocale()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Calendar View",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold
            )
        }

        // Calendar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            HorizontalCalendar(
                state = calendarState,
                modifier = Modifier.padding(16.dp),
                dayContent = { day ->
                    CalendarDay(
                        day = day.date,
                        isSelected = day.date == selectedDate,
                        hasExpenses = day.date in daysWithExpenses,
                        onClick = { selectedDate = day.date }
                    )
                }
            )
        }

        // Selected day information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val dayTotal = dailyExpenses[selectedDate] ?: 0.0
                Spacer(modifier = Modifier.height(8.dp))

                if (dayTotal > 0) {
                    Text(
                        text = "Total Spent: ₹ %.2f".format(dayTotal),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val dateString = selectedDate.toString()
                            navController.navigate("daily_expense/$dateString")
                        },
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("View Details", style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    Text(
                        text = "No expenses on this day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // Legend
        Text(
            text = "Legend",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraSmall
                    )
            )
            Text(
                text = "Selected date",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    )
            )
            Text(
                text = "Date with expenses",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CalendarDay(
    day: LocalDate,
    isSelected: Boolean,
    hasExpenses: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    hasExpenses -> MaterialTheme.colorScheme.errorContainer
                    else -> Color.Transparent
                },
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                hasExpenses -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
