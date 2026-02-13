package com.example.feature.dashboard.ui

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.coroutines.DispatcherProvider
import com.example.data.local.FinancialDatabase
import com.example.data.repository.TransactionRepositoryImpl
import com.example.data.source.FakeTransactionApi
import com.example.domain.usecase.CalculateDashboardInsightsUseCase
import com.example.domain.usecase.CalculateMonthlySummaryUseCase
import com.example.domain.usecase.ObserveTransactionsUseCase
import com.example.domain.usecase.RefreshTransactionsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardViewModelIntegrationTest {

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
    fun refreshAndMonthNavigation_shouldExposeInsightsFromIntegratedDataFlow() = runBlocking {
        val repository = TransactionRepositoryImpl(
            api = FakeTransactionApi(),
            dao = database.transactionDao(),
            dispatchers = AndroidTestDispatcherProvider()
        )

        val viewModel = DashboardViewModel(
            observeTransactions = ObserveTransactionsUseCase(repository),
            refreshTransactions = RefreshTransactionsUseCase(repository),
            calculateDashboardInsights = CalculateDashboardInsightsUseCase(CalculateMonthlySummaryUseCase())
        )

        awaitUntil { !viewModel.state.value.isLoading }
        awaitUntil { viewModel.state.value.insights?.topExpenses?.any { it.description == "Rent" } == true }

        val initialMonth = viewModel.state.value.selectedMonth
        assertNotNull(viewModel.state.value.insights)
        assertTrue(viewModel.state.value.insights!!.topExpenses.any { it.description == "Rent" })

        viewModel.previousMonth()
        awaitUntil { viewModel.state.value.selectedMonth == initialMonth.minusMonths(1) }

        assertTrue(viewModel.state.value.selectedMonth == initialMonth.minusMonths(1))
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
