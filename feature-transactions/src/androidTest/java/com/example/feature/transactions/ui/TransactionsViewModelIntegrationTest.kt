package com.example.feature.transactions.ui

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.coroutines.DispatcherProvider
import com.example.data.local.FinancialDatabase
import com.example.data.repository.TransactionRepositoryImpl
import com.example.data.source.FakeTransactionApi
import com.example.domain.usecase.AddTransactionUseCase
import com.example.domain.usecase.CalculateMonthlySummaryUseCase
import com.example.domain.usecase.DeleteTransactionUseCase
import com.example.domain.usecase.ObserveTransactionsUseCase
import com.example.domain.usecase.RefreshTransactionsUseCase
import com.example.domain.usecase.UpdateTransactionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionsViewModelIntegrationTest {

    private lateinit var database: FinancialDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, FinancialDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addTransactionAndFilter_shouldReflectInUiState() = runBlocking {
        val repository = TransactionRepositoryImpl(
            api = FakeTransactionApi(),
            dao = database.transactionDao(),
            dispatchers = AndroidTestDispatcherProvider()
        )

        val viewModel = TransactionsViewModel(
            observeTransactions = ObserveTransactionsUseCase(repository),
            refreshTransactions = RefreshTransactionsUseCase(repository),
            addTransaction = AddTransactionUseCase(repository),
            updateTransaction = UpdateTransactionUseCase(repository),
            deleteTransaction = DeleteTransactionUseCase(repository),
            calculateSummary = CalculateMonthlySummaryUseCase()
        )

        awaitUntil { !viewModel.state.value.isLoading }

        viewModel.addTransaction(description = "Academia", amount = "90.00", isIncome = false)
        awaitUntil { viewModel.state.value.transactions.any { it.description == "Academia" } }

        viewModel.onSearchChanged("Acad")
        awaitUntil {
            viewModel.state.value.transactions.isNotEmpty() &&
                viewModel.state.value.transactions.all { it.description.contains("Acad", ignoreCase = true) }
        }

        assertTrue(viewModel.state.value.transactions.all { it.description.contains("Acad", ignoreCase = true) })
    }

    private suspend fun awaitUntil(condition: () -> Boolean) {
        withTimeout(5_000) {
            while (!condition()) {
                delay(50)
            }
        }
    }
}

private class AndroidTestDispatcherProvider : DispatcherProvider {
    override val io = Dispatchers.Unconfined
    override val default = Dispatchers.Unconfined
    override val main = Dispatchers.Main
}
