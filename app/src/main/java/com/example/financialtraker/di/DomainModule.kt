package com.example.financialtraker.di

import com.example.domain.usecase.AddTransactionUseCase
import com.example.domain.usecase.CalculateDashboardInsightsUseCase
import com.example.domain.usecase.CalculateMonthlySummaryUseCase
import com.example.domain.usecase.DeleteTransactionUseCase
import com.example.domain.usecase.ObserveTransactionsUseCase
import com.example.domain.usecase.RefreshTransactionsUseCase
import com.example.domain.usecase.UpdateTransactionUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { ObserveTransactionsUseCase(get()) }
    factory { AddTransactionUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { RefreshTransactionsUseCase(get()) }
    factory { CalculateMonthlySummaryUseCase() }
    factory { CalculateDashboardInsightsUseCase(get()) }
}
