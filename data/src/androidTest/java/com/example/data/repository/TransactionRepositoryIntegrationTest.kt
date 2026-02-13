package com.example.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.coroutines.DispatcherProvider
import com.example.data.local.FinancialDatabase
import com.example.data.source.FakeTransactionApi
import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.TransactionFilters
import com.example.domain.model.TransactionType
import com.example.domain.model.UpdateTransactionCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class TransactionRepositoryIntegrationTest {

    private lateinit var database: FinancialDatabase
    private lateinit var repository: TransactionRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, FinancialDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        repository = TransactionRepositoryImpl(
            api = FakeTransactionApi(),
            dao = database.transactionDao(),
            dispatchers = IntegrationTestDispatcherProvider()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun refreshAndAdd_shouldPersistAndReturnTransactionsFromRoom() = runBlocking {
        repository.refresh()

        repository.addTransaction(
            CreateTransactionCommand(
                description = "Mercado",
                amount = BigDecimal("125.90"),
                type = TransactionType.EXPENSE
            )
        )

        val transactions = repository.observeTransactions(TransactionFilters()).first()

        assertEquals(3, transactions.size)
        assertTrue(transactions.any { it.description == "Mercado" })
        assertTrue(transactions.any { it.description == "Salary" })
        assertTrue(transactions.any { it.description == "Rent" })
    }

    @Test
    fun updateAndDelete_shouldKeepRoomInSyncWithRemoteActions() = runBlocking {
        repository.addTransaction(
            CreateTransactionCommand(
                description = "Internet",
                amount = BigDecimal("80.00"),
                type = TransactionType.EXPENSE
            )
        )

        val created = repository.observeTransactions(TransactionFilters()).first()
            .first { it.description == "Internet" }

        repository.updateTransaction(
            UpdateTransactionCommand(
                id = created.id,
                description = "Internet Fibra",
                amount = BigDecimal("95.00"),
                type = TransactionType.EXPENSE,
                createdAt = created.createdAt
            )
        )

        repository.deleteTransaction(created.id)

        val result = repository.observeTransactions(TransactionFilters()).first()

        assertTrue(result.none { it.id == created.id })
        assertTrue(result.none { it.description == "Internet" })
        assertTrue(result.none { it.description == "Internet Fibra" })
    }

    @Test
    fun refreshAndFilter_shouldReturnOnlyExpensesMatchingQuery() = runBlocking {
        repository.refresh()
        repository.addTransaction(
            CreateTransactionCommand(
                description = "Padaria do bairro",
                amount = BigDecimal("32.40"),
                type = TransactionType.EXPENSE
            )
        )

        val filtered = repository.observeTransactions(
            TransactionFilters(
                query = "Padaria",
                type = TransactionType.EXPENSE
            )
        ).first()

        assertEquals(1, filtered.size)
        assertTrue(filtered.all { it.type == TransactionType.EXPENSE })
        assertTrue(filtered.any { it.description.contains("Padaria") })
    }
}

internal class IntegrationTestDispatcherProvider : DispatcherProvider {
    override val io = Dispatchers.Unconfined
    override val default = Dispatchers.Unconfined
    override val main = Dispatchers.Unconfined
}
