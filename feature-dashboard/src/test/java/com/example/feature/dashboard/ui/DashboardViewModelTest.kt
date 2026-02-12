package com.example.feature.dashboard.ui

import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionFilters
import com.example.domain.model.TransactionType
import com.example.domain.model.UpdateTransactionCommand
import com.example.domain.repository.TransactionRepository
import com.example.domain.usecase.CalculateDashboardInsightsUseCase
import com.example.domain.usecase.CalculateMonthlySummaryUseCase
import com.example.domain.usecase.ObserveTransactionsUseCase
import com.example.domain.usecase.RefreshTransactionsUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

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
    fun `should expose insights from repository data`() = runTest(dispatcher) {
        val repository = FakeDashboardRepository()
        val viewModel = DashboardViewModel(
            observeTransactions = ObserveTransactionsUseCase(repository),
            refreshTransactions = RefreshTransactionsUseCase(repository),
            calculateDashboardInsights = CalculateDashboardInsightsUseCase(CalculateMonthlySummaryUseCase())
        )

        advanceUntilIdle()

        assertThat(viewModel.state.value.insights).isNotNull()
        assertThat(viewModel.state.value.insights?.topExpenses).isNotEmpty()
    }
}

private class FakeDashboardRepository : TransactionRepository {
    private val source = MutableStateFlow(
        listOf(
            FinancialTransaction(
                id = "1",
                description = "Salary",
                amount = BigDecimal("2500.00"),
                type = TransactionType.INCOME,
                createdAt = Instant.now()
            ),
            FinancialTransaction(
                id = "2",
                description = "Rent",
                amount = BigDecimal("900.00"),
                type = TransactionType.EXPENSE,
                createdAt = Instant.now()
            )
        )
    )

    override fun observeTransactions(filters: TransactionFilters) = source.map { list ->
        list.filter { item ->
            val queryMatches = filters.query.isBlank() || item.description.contains(filters.query, ignoreCase = true)
            val typeMatches = filters.type == null || item.type == filters.type
            queryMatches && typeMatches
        }
    }

    override suspend fun refresh() = Unit

    override suspend fun addTransaction(command: CreateTransactionCommand) = Unit

    override suspend fun updateTransaction(command: UpdateTransactionCommand) = Unit

    override suspend fun deleteTransaction(transactionId: String) = Unit
}
