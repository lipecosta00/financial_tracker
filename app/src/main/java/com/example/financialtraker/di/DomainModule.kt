package com.example.financialtraker.di

import com.example.domain.usecase.AddTransactionUseCase
import com.example.domain.usecase.CalculateMonthlySummaryUseCase
import com.example.domain.usecase.ObserveTransactionsUseCase
import com.example.domain.usecase.RefreshTransactionsUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { ObserveTransactionsUseCase(get()) }
    factory { AddTransactionUseCase(get()) }
    factory { RefreshTransactionsUseCase(get()) }
    factory { CalculateMonthlySummaryUseCase() }
}
