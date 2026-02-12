package com.example.financialtraker.di

import androidx.room.Room
import com.example.data.local.FinancialDatabase
import com.example.data.local.TransactionDao
import com.example.data.repository.TransactionRepositoryImpl
import com.example.data.source.FakeTransactionApi
import com.example.data.source.TransactionApi
import com.example.domain.repository.TransactionRepository
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            get(),
            FinancialDatabase::class.java,
            "financial_tracker.db"
        ).build()
    }
    single<TransactionDao> { get<FinancialDatabase>().transactionDao() }
    single<TransactionApi> { FakeTransactionApi() }
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get(), get()) }
}
