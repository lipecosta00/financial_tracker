package com.example.data.source

import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionType
import com.example.domain.model.UpdateTransactionCommand
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

interface TransactionApi {
    suspend fun fetchTransactions(): List<FinancialTransaction>
    suspend fun createTransaction(command: CreateTransactionCommand): FinancialTransaction
    suspend fun updateTransaction(command: UpdateTransactionCommand): FinancialTransaction
    suspend fun deleteTransaction(transactionId: String)
}

class FakeTransactionApi : TransactionApi {
    private val transactions = mutableListOf(
        FinancialTransaction(
            id = "seed-salary",
            description = "Salary",
            amount = BigDecimal("5000.00"),
            type = TransactionType.INCOME,
            createdAt = Instant.now()
        ),
        FinancialTransaction(
            id = "seed-rent",
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

    override suspend fun createTransaction(command: CreateTransactionCommand): FinancialTransaction {
        delay(250)
        val created = FinancialTransaction(
            id = UUID.randomUUID().toString(),
            description = command.description,
            amount = command.amount,
            type = command.type,
            createdAt = Instant.now()
        )
        transactions.add(created)
        return created
    }

    override suspend fun updateTransaction(command: UpdateTransactionCommand): FinancialTransaction {
        delay(200)
        val updated = FinancialTransaction(
            id = command.id,
            description = command.description,
            amount = command.amount,
            type = command.type,
            createdAt = command.createdAt
        )
        val index = transactions.indexOfFirst { it.id == command.id }
        if (index >= 0) {
            transactions[index] = updated
        } else {
            transactions.add(updated)
        }
        return updated
    }

    override suspend fun deleteTransaction(transactionId: String) {
        delay(150)
        transactions.removeAll { it.id == transactionId }
    }
}
