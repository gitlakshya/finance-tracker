package com.expense.tracker.utils

import java.util.regex.Pattern

data class ParsedTransaction(
    val amount: Double,
    val category: String,
    val description: String,
    val paymentMode: String,
    val merchant: String
)

object SMSTransactionParser {
    private val AMOUNT_PATTERN = Regex("""(?:Rs\.?|₹|INR|Amount)\s*(?:of\s+)?[:\s]*([0-9,]+(?:\.[0-9]{2})?)""")
    private val DEBIT_PATTERN = Regex("""(?:debited|withdrawn|transferred|spent|paid|charged)\s*(?:of|by|to)?\s*[:\s]*(?:Rs\.?|₹)?""", RegexOption.IGNORE_CASE)
    private val CREDIT_PATTERN = Regex("""(?:credited|received|deposited|added)\s*""", RegexOption.IGNORE_CASE)

    fun parseTransaction(smsBody: String, sender: String): ParsedTransaction? {
        // Check if it's a debit transaction (skip if credit)
        if (CREDIT_PATTERN.containsMatchIn(smsBody)) {
            return null
        }

        if (!DEBIT_PATTERN.containsMatchIn(smsBody) && !AMOUNT_PATTERN.containsMatchIn(smsBody)) {
            return null
        }

        // Extract amount
        val amountMatch = AMOUNT_PATTERN.find(smsBody)
        val amountStr = amountMatch?.groupValues?.get(1)?.replace(",", "") ?: return null
        val amount = amountStr.toDoubleOrNull() ?: return null

        if (amount <= 0) return null

        // Extract merchant/description
        val merchant = extractMerchant(smsBody)
        val paymentMode = extractPaymentMode(smsBody)
        val category = categorizeExpense(merchant, smsBody)

        return ParsedTransaction(
            amount = amount,
            category = category,
            description = merchant,
            paymentMode = paymentMode,
            merchant = merchant
        )
    }

    private fun extractMerchant(smsBody: String): String {
        // Look for common patterns
        val patterns = listOf(
            Regex("""at\s+([A-Za-z0-9\s]+?)(?:\.|,|on\s|${'$'}|\s-\s)"""),
            Regex("""to\s+([A-Za-z0-9\s]+?)(?:\.|,|on\s|${'$'}|\s-\s)"""),
            Regex("""(?:for|toward|towards)\s+([A-Za-z0-9\s]+?)(?:\.|,|on\s|${'$'}|\s-\s)"""),
            Regex("""merchant\s*[:=]\s*([A-Za-z0-9\s]+?)(?:\.|,|\s-\s)""")
        )

        for (pattern in patterns) {
            val match = pattern.find(smsBody)
            if (match != null) {
                return match.groupValues[1].trim()
            }
        }

        // Fallback: extract first capitalized word or phrase
        val words = smsBody.split(Regex("\\s+"))
        for (i in words.indices) {
            if (words[i].first().isUpperCase() && words[i].length > 2) {
                return words.subList(i, minOf(i + 3, words.size)).joinToString(" ")
            }
        }

        return "Transaction"
    }

    private fun extractPaymentMode(smsBody: String): String {
        return when {
            smsBody.contains("atm", ignoreCase = true) -> "Card"
            smsBody.contains("card", ignoreCase = true) -> "Card"
            smsBody.contains("upi", ignoreCase = true) -> "UPI"
            smsBody.contains("neft", ignoreCase = true) -> "Transfer"
            smsBody.contains("rtgs", ignoreCase = true) -> "Transfer"
            smsBody.contains("imps", ignoreCase = true) -> "Transfer"
            else -> "Card"
        }
    }

    private fun categorizeExpense(merchant: String, smsBody: String): String {
        val merchantLower = merchant.lowercase()
        val smsBodYLower = smsBody.lowercase()

        // Category mapping based on keywords
        val categoryKeywords = Constants.CategoryKeywords.getCategoryKeywordsMap().toMutableMap()
        categoryKeywords["Others"] = emptyList()

        for ((category, keywords) in categoryKeywords) {
            for (keyword in keywords) {
                if (merchantLower.contains(keyword) || smsBodYLower.contains(keyword)) {
                    return category
                }
            }
        }

        return "Others"
    }
}
