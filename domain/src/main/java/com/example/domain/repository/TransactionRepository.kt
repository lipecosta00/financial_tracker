package com.example.domain.repository

import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionFilters
import com.example.domain.model.UpdateTransactionCommand
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(filters: TransactionFilters): Flow<List<FinancialTransaction>>
    suspend fun refresh()
    suspend fun addTransaction(command: CreateTransactionCommand)
    suspend fun updateTransaction(command: UpdateTransactionCommand)
    suspend fun deleteTransaction(transactionId: String)
}
