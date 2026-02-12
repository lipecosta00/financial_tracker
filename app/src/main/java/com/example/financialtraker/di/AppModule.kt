package com.example.financialtraker.di

import com.example.core.coroutines.DefaultDispatcherProvider
import com.example.core.coroutines.DispatcherProvider
import com.example.core.security.EncryptedTokenStore
import com.example.core.security.SecureTokenStore
import org.koin.dsl.module

val appModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<SecureTokenStore> { EncryptedTokenStore(get()) }
}
