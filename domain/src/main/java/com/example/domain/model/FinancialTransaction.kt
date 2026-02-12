package com.example.domain.model

import java.math.BigDecimal
import java.time.Instant

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class FinancialTransaction(
    val id: String,
    val description: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val createdAt: Instant
)

data class CreateTransactionCommand(
    val description: String,
    val amount: BigDecimal,
    val type: TransactionType
)

data class MonthlySummary(
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val balance: BigDecimal
)
