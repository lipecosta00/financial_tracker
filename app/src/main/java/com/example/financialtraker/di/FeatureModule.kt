package com.example.financialtraker.di

import com.example.feature.transactions.ui.TransactionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    viewModel { TransactionsViewModel(get(), get(), get(), get()) }
}
