package com.example.feature.transactions.ui

import app.cash.turbine.test
import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionType
import com.example.domain.repository.TransactionRepository
import com.example.domain.usecase.AddTransactionUseCase
import com.example.domain.usecase.CalculateMonthlySummaryUseCase
import com.example.domain.usecase.ObserveTransactionsUseCase
import com.example.domain.usecase.RefreshTransactionsUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `add transaction updates state`() = runTest(dispatcher) {
        val fakeRepository = FakeTransactionRepository()
        val viewModel = TransactionsViewModel(
            observeTransactions = ObserveTransactionsUseCase(fakeRepository),
            refreshTransactions = RefreshTransactionsUseCase(fakeRepository),
            addTransaction = AddTransactionUseCase(fakeRepository),
            calculateSummary = CalculateMonthlySummaryUseCase()
        )

        viewModel.state.test {
            skipItems(1)
            viewModel.addTransaction("Book", "40.00", isIncome = false)
            val latest = awaitItem()
            assertThat(latest.transactions).isNotEmpty()
        }
    }
}

private class FakeTransactionRepository : TransactionRepository {
    private val transactions = MutableStateFlow(
        listOf(
            FinancialTransaction(
                id = "1",
                description = "Salary",
                amount = BigDecimal("1000.00"),
                type = TransactionType.INCOME,
                createdAt = Instant.now()
            )
        )
    )

    override fun observeTransactions() = transactions

    override suspend fun refresh() = Unit

    override suspend fun addTransaction(command: CreateTransactionCommand) {
        transactions.value = transactions.value + FinancialTransaction(
            id = "2",
            description = command.description,
            amount = command.amount,
            type = command.type,
            createdAt = Instant.now()
        )
    }
}
