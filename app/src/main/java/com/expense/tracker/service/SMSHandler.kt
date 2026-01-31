package com.expense.tracker.service

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.provider.Telephony
import com.expense.tracker.data.model.Expense
import com.expense.tracker.utils.SMSTransactionParser
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object SMSReader {
    @SuppressLint("MissingPermission")
    fun readAllSMS(contentResolver: ContentResolver): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val cursor: Cursor? = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE
            ),
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val smsId = it.getString(it.getColumnIndexOrThrow(Telephony.Sms._ID))
                val address = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val date = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))

                val parsedTransaction = SMSTransactionParser.parseTransaction(body, address)
                if (parsedTransaction != null) {
                    val dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(date),
                        ZoneId.systemDefault()
                    )
                    val expenseWithSmsId = Expense(
                        amount = parsedTransaction.amount,
                        category = parsedTransaction.category,
                        description = parsedTransaction.description,
                        paymentMode = parsedTransaction.paymentMode,
                        date = dateTime,
                        notes = "",
                        source = "SMS",
                        smsId = "SMS_${address}_${date}",
                        merchant = parsedTransaction.merchant,
                        bank = address
                    )
                    expenses.add(expenseWithSmsId)
                }
            }
        }

        return expenses
    }

    @SuppressLint("MissingPermission")
    fun readSMSSince(contentResolver: ContentResolver, since: Long): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val cursor: Cursor? = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE
            ),
            "${Telephony.Sms.DATE} > ?",
            arrayOf(since.toString()),
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val smsId = it.getString(it.getColumnIndexOrThrow(Telephony.Sms._ID))
                val address = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val date = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))

                val parsedTransaction = SMSTransactionParser.parseTransaction(body, address)
                if (parsedTransaction != null) {
                    val dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(date),
                        ZoneId.systemDefault()
                    )
                    val expenseWithSmsId = Expense(
                        amount = parsedTransaction.amount,
                        category = parsedTransaction.category,
                        description = parsedTransaction.description,
                        paymentMode = parsedTransaction.paymentMode,
                        date = dateTime,
                        notes = "",
                        source = "SMS",
                        smsId = "SMS_${address}_${date}",
                        merchant = parsedTransaction.merchant,
                        bank = address
                    )
                    expenses.add(expenseWithSmsId)
                }
            }
        }

        return expenses
    }
}
