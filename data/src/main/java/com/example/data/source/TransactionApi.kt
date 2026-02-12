package com.example.data.source

import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionType
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

interface TransactionApi {
    suspend fun fetchTransactions(): List<FinancialTransaction>
    suspend fun createTransaction(command: CreateTransactionCommand)
}

class FakeTransactionApi : TransactionApi {
    private val transactions = mutableListOf(
        FinancialTransaction(
            id = UUID.randomUUID().toString(),
            description = "Salary",
            amount = BigDecimal("5000.00"),
            type = TransactionType.INCOME,
            createdAt = Instant.now()
        ),
        FinancialTransaction(
            id = UUID.randomUUID().toString(),
            description = "Rent",
            amount = BigDecimal("1800.00"),
            type = TransactionType.EXPENSE,
            createdAt = Instant.now()
        )
    )

    override suspend fun fetchTransactions(): List<FinancialTransaction> {
        delay(400)
        return transactions.toList()
    }

    override suspend fun createTransaction(command: CreateTransactionCommand) {
        delay(250)
        transactions.add(
            FinancialTransaction(
                id = UUID.randomUUID().toString(),
                description = command.description,
                amount = command.amount,
                type = command.type,
                createdAt = Instant.now()
            )
        )
    }
}
