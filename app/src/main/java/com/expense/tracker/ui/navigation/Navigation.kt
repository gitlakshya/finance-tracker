package com.expense.tracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.expense.tracker.ui.screens.HomeScreen
import com.expense.tracker.ui.screens.AddExpenseScreen
import com.expense.tracker.ui.screens.ReportsScreen
import com.expense.tracker.ui.screens.SettingsScreen
import com.expense.tracker.ui.screens.ManageCategoriesScreen
import com.expense.tracker.ui.screens.DailyExpenseScreen
import com.expense.tracker.ui.screens.CalendarViewScreen
import com.expense.tracker.ui.screens.EditExpenseScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.NavType
import java.time.LocalDate
import java.time.LocalDateTime

sealed class Screen(val route: String, val label: String) {
    object Home : Screen("home", "Home")
    object AddExpense : Screen("add_expense", "Add")
    object Reports : Screen("reports", "Reports")
    object Settings : Screen("settings", "Settings")
    object ManageCategories : Screen("manage_categories", "Manage Categories")
    object CalendarView : Screen("calendar_view", "Calendar")
    object DailyExpense : Screen("daily_expense/{date}", "Daily Expense")
    object EditExpense : Screen("edit_expense/{id}", "Edit Expense")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddExpense.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.AddExpense.route) {
                AddExpenseScreen(navController)
            }
            composable(Screen.Reports.route) {
                ReportsScreen(navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }
            composable(Screen.ManageCategories.route) {
                ManageCategoriesScreen(navController)
            }
            composable(Screen.CalendarView.route) {
                CalendarViewScreen(navController)
            }
            composable(
                route = "daily_expense/{date}",
                arguments = listOf(
                    navArgument("date") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val dateString = backStackEntry.arguments?.getString("date")
                val selectedDate = if (dateString != null) {
                    try {
                        // Parse ISO date string (yyyy-MM-dd) to LocalDateTime
                        LocalDate.parse(dateString).atStartOfDay()
                    } catch (e: Exception) {
                        LocalDateTime.now()
                    }
                } else {
                    LocalDateTime.now()
                }
                DailyExpenseScreen(navController, selectedDate)
            }
            composable(
                route = "edit_expense/{id}",
                arguments = listOf(
                    navArgument("id") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getLong("id") ?: 0L
                EditExpenseScreen(navController, expenseId)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Reports,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Home -> Icon(Icons.Default.Home, contentDescription = screen.label)
                        Screen.Reports -> Icon(Icons.Default.MoreVert, contentDescription = screen.label)
                        Screen.Settings -> Icon(Icons.Default.Settings, contentDescription = screen.label)
                        else -> Icon(Icons.Default.Home, contentDescription = screen.label)
                    }
                },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
