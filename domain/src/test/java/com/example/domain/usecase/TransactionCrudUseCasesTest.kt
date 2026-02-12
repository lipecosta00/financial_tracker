package com.example.domain.usecase

import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionFilters
import com.example.domain.model.TransactionType
import com.example.domain.model.UpdateTransactionCommand
import com.example.domain.repository.TransactionRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class TransactionCrudUseCasesTest {

    @Test
    fun `update use case should forward command to repository`() = runBlocking {
        val fakeRepository = FakeTransactionRepository()
        val useCase = UpdateTransactionUseCase(fakeRepository)
        val command = UpdateTransactionCommand(
            id = "tx-1",
            description = "Updated rent",
            amount = BigDecimal("1500.00"),
            type = TransactionType.EXPENSE,
            createdAt = Instant.parse("2026-02-01T10:00:00Z")
        )

        useCase(command)

        assertThat(fakeRepository.lastUpdatedCommand).isEqualTo(command)
    }

    @Test
    fun `delete use case should forward id to repository`() = runBlocking {
        val fakeRepository = FakeTransactionRepository()
        val useCase = DeleteTransactionUseCase(fakeRepository)
        val transactionId = "tx-1"
        useCase(transactionId)

        assertThat(fakeRepository.lastDeletedId).isEqualTo(transactionId)
        assertThat(fakeRepository.deleteCalls).isEqualTo(1)
        assertThat(fakeRepository.updateCalls).isEqualTo(0)
        assertThat(fakeRepository.lastUpdatedCommand).isNull()
    }
}

private class FakeTransactionRepository : TransactionRepository {
    var lastUpdatedCommand: UpdateTransactionCommand? = null
    var lastDeletedId: String? = null

    var updateCalls = 0
    var deleteCalls = 0

    override fun observeTransactions(filters: TransactionFilters): Flow<List<FinancialTransaction>> = emptyFlow()

    override suspend fun refresh() = Unit

    override suspend fun addTransaction(command: CreateTransactionCommand) = Unit

    override suspend fun updateTransaction(command: UpdateTransactionCommand) {
        updateCalls++
        lastUpdatedCommand = command
    }

    override suspend fun deleteTransaction(transactionId: String) {
        deleteCalls++
        lastDeletedId = transactionId
    }
}
