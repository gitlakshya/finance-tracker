package com.expense.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val description: String,
    val paymentMode: String, // "Cash", "UPI", "Card"
    val date: LocalDateTime,
    val notes: String = "",
    val source: String = "MANUAL", // "MANUAL" or "SMS"
    val smsId: String = "", // For deduplication
    val merchant: String = "",
    val bank: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
