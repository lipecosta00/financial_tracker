package com.example.financialtraker

import android.app.Application
import com.example.financialtraker.di.appModule
import com.example.financialtraker.di.dataModule
import com.example.financialtraker.di.domainModule
import com.example.financialtraker.di.featureModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinancialTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FinancialTrackerApp)
            modules(appModule, domainModule, dataModule, featureModule)
        }
    }
}
