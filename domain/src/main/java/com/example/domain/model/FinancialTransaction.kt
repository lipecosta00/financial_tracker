package com.example.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.time.YearMonth

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

data class UpdateTransactionCommand(
    val id: String,
    val description: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val createdAt: Instant
)

data class MonthlySummary(
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val balance: BigDecimal
)

data class TransactionFilters(
    val query: String = "",
    val type: TransactionType? = null,
    val month: YearMonth? = YearMonth.now()
)
