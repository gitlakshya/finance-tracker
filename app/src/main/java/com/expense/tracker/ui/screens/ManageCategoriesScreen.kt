package com.expense.tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.expense.tracker.MainActivity
import com.expense.tracker.data.model.Category
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(navController: NavHostController) {
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var newCategoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF9C27B0") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Load categories on screen enter
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                categories = MainActivity.repository.getAllCategories().first()
            } catch (e: Exception) {
                errorMessage = "Failed to load categories: ${e.message}"
            }
        }
    }

    // Color palette for quick selection
    val colorOptions = listOf(
        "#FFFF5722", // Red
        "#FF2196F3", // Blue
        "#FF4CAF50", // Green
        "#FFF57C00", // Orange
        "#FF9C27B0", // Purple
        "#FF00BCD4", // Cyan
        "#FF795548", // Brown
        "#FF607D8B"  // Blue Grey
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Top bar
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
                text = "Manage Categories",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )
        }

        // Add new category section
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
                    text = "Add New Category",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Category name input
                TextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    label = { Text("Category Name", style = MaterialTheme.typography.labelSmall) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                // Color picker
                Text(
                    text = "Select Color",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    colorOptions.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color(android.graphics.Color.parseColor(color)),
                                    shape = MaterialTheme.shapes.small
                                )
                                .then(
                                    if (selectedColor == color) {
                                        Modifier.padding(2.dp)
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                        Button(
                            onClick = { selectedColor = color },
                            modifier = Modifier.size(32.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors()
                        ) {}
                    }
                }

                // Add button
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                try {
                                    val newCategory = Category(
                                        name = newCategoryName,
                                        color = selectedColor
                                    )
                                    MainActivity.repository.insertCategory(newCategory)
                                    categories = MainActivity.repository.getAllCategories().first()
                                    newCategoryName = ""
                                    selectedColor = "#FF9C27B0"
                                    isLoading = false
                                } catch (e: Exception) {
                                    errorMessage = "Failed to add category: ${e.message}"
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.padding(end = 6.dp).size(18.dp))
                    Text("Add Category", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Categories list
        Text(
            text = "Existing Categories (${categories.size})",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        if (categories.isEmpty()) {
            Text(
                text = "No categories yet. Add one above!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = Color(android.graphics.Color.parseColor(category.color)),
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                )
                                Column {
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (category.isDefault) {
                                        Text(
                                            text = "Default",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            // Delete button (only for non-default categories)
                            if (!category.isDefault) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                MainActivity.repository.deleteCategory(category)
                                                categories = MainActivity.repository.getAllCategories().first()
                                            } catch (e: Exception) {
                                                errorMessage = "Failed to delete category: ${e.message}"
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
