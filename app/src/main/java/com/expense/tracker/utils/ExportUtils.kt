package com.expense.tracker.utils

import android.os.Environment
import com.expense.tracker.data.model.Expense
import java.io.File

object ExportUtils {
    /**
     * Export expenses to CSV format
     */
    fun exportToCsv(expenses: List<Expense>, fileName: String): File? {
        val csvHeader = "Date,Time,Description,Category,Amount,Payment Mode,Notes,Source\n"
        val csvRows = expenses.map { expense ->
            val dateStr = DateUtils.formatDate(expense.date)
            val timeStr = DateUtils.formatTime(expense.date)
            "\"$dateStr\",\"$timeStr\",\"${expense.description}\",\"${expense.category}\",${String.format("%.2f", expense.amount)},\"${expense.paymentMode}\",\"${expense.notes}\",\"${expense.source}\""
        }.joinToString("\n")

        return try {
            val filePath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            filePath.writeText(csvHeader + csvRows)
            filePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate a CSV report for the month
     */
    fun generateMonthlyReportCsv(
        monthName: String,
        expenses: List<Expense>
    ): String {
        val header = "Monthly Report for $monthName\n\n"
        val summary = "Total Transactions: ${expenses.size}\n"
        val total = "Total Spent: ₹${String.format("%.2f", expenses.sumOf { it.amount })}\n\n"
        
        val expenseDetails = "Date,Time,Description,Category,Amount,Payment Mode\n" +
                expenses.map { expense ->
                    val dateStr = DateUtils.formatDate(expense.date)
                    val timeStr = DateUtils.formatTime(expense.date)
                    "\"$dateStr\",\"$timeStr\",\"${expense.description}\",\"${expense.category}\",${String.format("%.2f", expense.amount)},\"${expense.paymentMode}\""
                }.joinToString("\n")
        
        return header + summary + total + expenseDetails
    }
}
