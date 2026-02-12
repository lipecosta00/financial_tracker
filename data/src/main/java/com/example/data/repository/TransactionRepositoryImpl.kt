package com.example.data.repository

import com.example.core.coroutines.DispatcherProvider
import com.example.data.source.TransactionApi
import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class TransactionRepositoryImpl(
    private val api: TransactionApi,
    private val dispatchers: DispatcherProvider
) : TransactionRepository {

    private val transactions = MutableStateFlow<List<FinancialTransaction>>(emptyList())

    override fun observeTransactions() = transactions.asStateFlow()

    override suspend fun refresh() {
        val remoteData = withContext(dispatchers.io) { api.fetchTransactions() }
        transactions.value = remoteData
    }

    override suspend fun addTransaction(command: CreateTransactionCommand) {
        withContext(dispatchers.io) {
            api.createTransaction(command)
        }
        refresh()
    }
}
