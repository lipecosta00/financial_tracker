package com.example.financialtraker.di

import com.example.data.repository.TransactionRepositoryImpl
import com.example.data.source.FakeTransactionApi
import com.example.data.source.TransactionApi
import com.example.domain.repository.TransactionRepository
import org.koin.dsl.module

val dataModule = module {
    single<TransactionApi> { FakeTransactionApi() }
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get()) }
}
