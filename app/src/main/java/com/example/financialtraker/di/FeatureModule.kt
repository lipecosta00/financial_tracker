package com.example.financialtraker.di

import com.example.feature.auth.ui.AuthViewModel
import com.example.feature.dashboard.ui.DashboardViewModel
import com.example.feature.transactions.ui.TransactionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { TransactionsViewModel(get(), get(), get(), get(), get(), get()) }
}
