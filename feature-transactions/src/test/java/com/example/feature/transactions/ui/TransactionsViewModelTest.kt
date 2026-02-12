package com.example.feature.transactions.ui

import app.cash.turbine.test
import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionFilters
import com.example.domain.model.TransactionType
import com.example.domain.model.UpdateTransactionCommand
import com.example.domain.repository.TransactionRepository
import com.example.domain.usecase.AddTransactionUseCase
import com.example.domain.usecase.CalculateMonthlySummaryUseCase
import com.example.domain.usecase.DeleteTransactionUseCase
import com.example.domain.usecase.ObserveTransactionsUseCase
import com.example.domain.usecase.RefreshTransactionsUseCase
import com.example.domain.usecase.UpdateTransactionUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.advanceUntilIdle
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
            updateTransaction = UpdateTransactionUseCase(fakeRepository),
            deleteTransaction = DeleteTransactionUseCase(fakeRepository),
            calculateSummary = CalculateMonthlySummaryUseCase()
        )

        viewModel.state.test {
            skipItems(1)
            viewModel.addTransaction("Book", "40.00", isIncome = false)
            val latest = awaitItem()
            assertThat(latest.transactions).isNotEmpty()
        }
    }

    @Test
    fun `edit transaction updates existing item`() = runTest(dispatcher) {
        val fakeRepository = FakeTransactionRepository()
        val viewModel = TransactionsViewModel(
            observeTransactions = ObserveTransactionsUseCase(fakeRepository),
            refreshTransactions = RefreshTransactionsUseCase(fakeRepository),
            addTransaction = AddTransactionUseCase(fakeRepository),
            updateTransaction = UpdateTransactionUseCase(fakeRepository),
            deleteTransaction = DeleteTransactionUseCase(fakeRepository),
            calculateSummary = CalculateMonthlySummaryUseCase()
        )

        advanceUntilIdle()
        val firstItem = viewModel.state.value.transactions.first()
        viewModel.editTransaction(firstItem, "Updated Salary", "1200.00", isIncome = true)
        advanceUntilIdle()

        assertThat(viewModel.state.value.transactions.first().description).isEqualTo("Updated Salary")
    }

    @Test
    fun `delete transaction removes item`() = runTest(dispatcher) {
        val fakeRepository = FakeTransactionRepository()
        val viewModel = TransactionsViewModel(
            observeTransactions = ObserveTransactionsUseCase(fakeRepository),
            refreshTransactions = RefreshTransactionsUseCase(fakeRepository),
            addTransaction = AddTransactionUseCase(fakeRepository),
            updateTransaction = UpdateTransactionUseCase(fakeRepository),
            deleteTransaction = DeleteTransactionUseCase(fakeRepository),
            calculateSummary = CalculateMonthlySummaryUseCase()
        )

        advanceUntilIdle()
        val firstItem = viewModel.state.value.transactions.first()
        viewModel.deleteTransaction(firstItem)
        advanceUntilIdle()

        assertThat(viewModel.state.value.transactions).isEmpty()
    }

    @Test
    fun `search filter should return matching transactions`() = runTest(dispatcher) {
        val fakeRepository = FakeTransactionRepository()
        val viewModel = TransactionsViewModel(
            observeTransactions = ObserveTransactionsUseCase(fakeRepository),
            refreshTransactions = RefreshTransactionsUseCase(fakeRepository),
            addTransaction = AddTransactionUseCase(fakeRepository),
            updateTransaction = UpdateTransactionUseCase(fakeRepository),
            deleteTransaction = DeleteTransactionUseCase(fakeRepository),
            calculateSummary = CalculateMonthlySummaryUseCase()
        )

        advanceUntilIdle()
        viewModel.addTransaction("Netflix", "20.00", isIncome = false)
        advanceUntilIdle()
        viewModel.onSearchChanged("net")
        advanceUntilIdle()

        assertThat(viewModel.state.value.transactions).hasSize(1)
        assertThat(viewModel.state.value.transactions.first().description).isEqualTo("Netflix")
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

    override fun observeTransactions(filters: TransactionFilters) = transactions.map { list ->
        list.filter { item ->
            val queryMatches = filters.query.isBlank() ||
                item.description.contains(filters.query, ignoreCase = true)
            val typeMatches = filters.type == null || item.type == filters.type
            queryMatches && typeMatches
        }
    }

    override suspend fun refresh() = Unit

    override suspend fun addTransaction(command: CreateTransactionCommand) {
        transactions.value += FinancialTransaction(
                    id = "2",
                    description = command.description,
                    amount = command.amount,
                    type = command.type,
                    createdAt = Instant.now()
                )
    }

    override suspend fun updateTransaction(command: UpdateTransactionCommand) {
        transactions.value = transactions.value.map {
            if (it.id == command.id) {
                it.copy(
                    description = command.description,
                    amount = command.amount,
                    type = command.type,
                    createdAt = command.createdAt
                )
            } else {
                it
            }
        }
    }

    override suspend fun deleteTransaction(transactionId: String) {
        transactions.value = transactions.value.filterNot { it.id == transactionId }
    }
}
