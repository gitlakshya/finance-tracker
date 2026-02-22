package com.expense.tracker.utils

import android.content.Context
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier

data class ParsedTransaction(
    val amount: Double,
    val category: String,
    val description: String,
    val paymentMode: String,
    val merchant: String
)

object SMSTransactionParser {
    // We keep the amount pattern to extract the exact numbers
    private val AMOUNT_PATTERN = Regex("""(?:Rs\.?|₹|INR|Amount)\s*(?:of\s+)?[:\s]*([0-9,]+(?:\.[0-9]{2})?)""")

    private var bertClassifier: BertNLClassifier? = null

    // Initialize the Transformer model (Call this from MainActivity or Application class)
    fun initializeML(context: Context) {
        if (bertClassifier == null) {
            val options = BertNLClassifier.BertNLClassifierOptions.builder().build()
            bertClassifier = BertNLClassifier.createFromFileAndOptions(
                context,
                "model.tflite",
                options
            )
        }
    }

    fun parseTransaction(smsBody: String, sender: String): ParsedTransaction? {
        val classifier = bertClassifier ?: return null // Ensure model is loaded

        // 1. Transformer Intent Classification
        // The model returns a list of categories (e.g., "0" for Not Debit, "1" for Debit) with confidence scores
        val results = classifier.classify(smsBody)

        // Find the "1" (Is_Debit) category score
        val isDebitScore = results.find { it.label == "1" }?.score ?: 0f

        // If the Transformer is less than 70% confident it's a debit, ignore it
        if (isDebitScore < 0.70f) {
            return null
        }

        // 2. Extract Amount using existing Regex
        val amountMatch = AMOUNT_PATTERN.find(smsBody)
        val amountStr = amountMatch?.groupValues?.get(1)?.replace(",", "") ?: return null
        val amount = amountStr.toDoubleOrNull() ?: return null

        if (amount <= 0) return null

        // 3. Extract Meta-data using your existing logic
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
