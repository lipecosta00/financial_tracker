package com.example.data.repository

import com.example.core.coroutines.DispatcherProvider
import com.example.data.local.TransactionDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.data.source.TransactionApi
import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.TransactionFilters
import com.example.domain.model.UpdateTransactionCommand
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.ZoneOffset

class TransactionRepositoryImpl(
    private val api: TransactionApi,
    private val dao: TransactionDao,
    private val dispatchers: DispatcherProvider
) : TransactionRepository {

    override fun observeTransactions(filters: TransactionFilters) = dao.observeTransactionsFiltered(
        query = filters.query.takeIf { it.isNotBlank() },
        type = filters.type,
        startEpochMillis = filters.month
            ?.atDay(1)
            ?.atStartOfDay()
            ?.toInstant(ZoneOffset.UTC)
            ?.toEpochMilli(),
        endEpochMillis = filters.month
            ?.plusMonths(1)
            ?.atDay(1)
            ?.atStartOfDay()
            ?.toInstant(ZoneOffset.UTC)
            ?.toEpochMilli()
    ).map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun refresh() {
        val remoteData = withContext(dispatchers.io) { api.fetchTransactions() }
        withContext(dispatchers.io) {
            dao.insertAll(remoteData.map { it.toEntity() })
        }
    }

    override suspend fun addTransaction(command: CreateTransactionCommand) {
        withContext(dispatchers.io) {
            val remoteCreated = api.createTransaction(command)
            dao.insert(remoteCreated.toEntity())
        }
    }

    override suspend fun updateTransaction(command: UpdateTransactionCommand) {
        withContext(dispatchers.io) {
            val remoteUpdated = api.updateTransaction(command)
            dao.insert(remoteUpdated.toEntity())
        }
    }

    override suspend fun deleteTransaction(transactionId: String) {
        withContext(dispatchers.io) {
            api.deleteTransaction(transactionId)
            dao.deleteById(transactionId)
        }
    }
}
