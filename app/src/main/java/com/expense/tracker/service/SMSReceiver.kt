package com.expense.tracker.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.expense.tracker.MainActivity
import com.expense.tracker.data.model.Expense
import com.expense.tracker.utils.SMSTransactionParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    for (message in messages) {
                        val smsBody = message.displayMessageBody
                        val sender = message.displayOriginatingAddress
                        val timestamp = message.timestampMillis
                        
                        Log.d("SMSReceiver", "SMS from $sender: $smsBody")
                        
                        // Parse and process SMS
                        val parsedTransaction = SMSTransactionParser.parseTransaction(smsBody, sender)
                        if (parsedTransaction != null) {
                            Log.d("SMSReceiver", "Parsed transaction: $parsedTransaction")
                            
                            // Check for duplicates using SMS ID
                            val smsId = "SMS_${sender}_${timestamp}"
                            val existing = MainActivity.repository.getExpenseBySmsId(smsId)
                            
                            if (existing == null) {
                                // Save to database with SMS ID
                                val dateTime = LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(timestamp),
                                    ZoneId.systemDefault()
                                )
                                val expense = Expense(
                                    amount = parsedTransaction.amount,
                                    category = parsedTransaction.category,
                                    description = parsedTransaction.description,
                                    paymentMode = parsedTransaction.paymentMode,
                                    date = dateTime,
                                    notes = "",
                                    source = "SMS",
                                    smsId = smsId,
                                    merchant = parsedTransaction.merchant,
                                    bank = sender
                                )
                                MainActivity.repository.insertExpense(expense)
                                Log.d("SMSReceiver", "Expense saved: ${expense.description}")
                            } else {
                                Log.d("SMSReceiver", "Duplicate SMS detected, skipping")
                            }
                        } else {
                            Log.d("SMSReceiver", "Not a transaction SMS")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SMSReceiver", "Error processing SMS", e)
                }
            }
        }
    }
}
