package com.example.domain.repository

import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(): Flow<List<FinancialTransaction>>
    suspend fun refresh()
    suspend fun addTransaction(command: CreateTransactionCommand)
}
